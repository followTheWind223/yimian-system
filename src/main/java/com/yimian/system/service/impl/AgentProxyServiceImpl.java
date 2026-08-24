package com.yimian.system.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yimian.system.common.exception.AgentProxyException;
import com.yimian.system.common.utils.AgentSessionKeyFactory;
import com.yimian.system.common.utils.AgentSseRelay;
import com.yimian.system.config.AgentServiceProperties;
import com.yimian.system.dto.AgentChatDto;
import com.yimian.system.service.AgentProxyService;
import com.yimian.system.service.AgentRateLimiter;
import com.yimian.system.vo.AgentChatVO;
import com.yimian.system.vo.AgentToolCallVO;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
@Service
public class AgentProxyServiceImpl implements AgentProxyService {

    @Qualifier("agentRestClient")
    private final RestClient agentRestClient;

    @Qualifier("agentStreamRestClient")
    private final RestClient agentStreamRestClient;

    private final AgentServiceProperties properties;
    private final AgentRateLimiter rateLimiter;
    private final AgentSessionKeyFactory sessionKeyFactory;
    private final ObjectMapper objectMapper;

    public AgentProxyServiceImpl(
            @Qualifier("agentRestClient") RestClient agentRestClient,
            @Qualifier("agentStreamRestClient") RestClient agentStreamRestClient,
            AgentServiceProperties properties,
            AgentRateLimiter rateLimiter,
            AgentSessionKeyFactory sessionKeyFactory,
            ObjectMapper objectMapper) {
        this.agentRestClient = agentRestClient;
        this.agentStreamRestClient = agentStreamRestClient;
        this.properties = properties;
        this.rateLimiter = rateLimiter;
        this.sessionKeyFactory = sessionKeyFactory;
        this.objectMapper = objectMapper;
    }

    @Override
    public AgentChatVO chat(Long userId, AgentChatDto dto) {
        properties.assertReady();
        rateLimiter.check(userId);

        String requestId = newRequestId();
        String upstreamSessionId = sessionKeyFactory.create(userId, dto.getSessionType(), dto.getSessionId());
        AgentUpstreamChatRequest request = new AgentUpstreamChatRequest(
                String.valueOf(userId),
                upstreamSessionId,
                dto.getSessionType(),
                dto.getMessage(),
                false
        );
        long startedAt = System.currentTimeMillis();

        try {
            AgentUpstreamChatResponse response = agentRestClient.post()
                    .uri("/api/chat")
                    .header("X-Request-Id", requestId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(AgentUpstreamChatResponse.class);
            if (response == null || response.reply() == null) {
                throw AgentProxyException.badResponse();
            }

            List<AgentToolCallVO> toolCalls = response.toolCalls() == null
                    ? Collections.emptyList()
                    : response.toolCalls().stream()
                            .filter(java.util.Objects::nonNull)
                            .map(this::toToolCallVO)
                            .toList();
            log.info("Agent chat completed. requestId={}, userId={}, sessionId={}, durationMs={}",
                    requestId, userId, dto.getSessionId(), System.currentTimeMillis() - startedAt);
            return AgentChatVO.builder()
                    .reply(response.reply())
                    .toolCalls(toolCalls)
                    .sessionId(dto.getSessionId())
                    .requestId(requestId)
                    .build();
        } catch (AgentProxyException e) {
            throw e;
        } catch (RestClientResponseException e) {
            throw mapStatus(e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw mapResourceAccess(e);
        } catch (RestClientException e) {
            throw AgentProxyException.unavailable();
        }
    }

    @Override
    public StreamingResponseBody streamChat(Long userId, AgentChatDto dto) {
        properties.assertReady();
        rateLimiter.check(userId);

        String requestId = newRequestId();
        String upstreamSessionId = sessionKeyFactory.create(userId, dto.getSessionType(), dto.getSessionId());
        AgentUpstreamChatRequest request = new AgentUpstreamChatRequest(
                String.valueOf(userId),
                upstreamSessionId,
                dto.getSessionType(),
                dto.getMessage(),
                true
        );

        return output -> {
            long startedAt = System.currentTimeMillis();
            try {
                AgentSseRelay.writeEvent(
                        output,
                        objectMapper,
                        "meta",
                        Map.of("requestId", requestId, "sessionId", dto.getSessionId())
                );
                agentStreamRestClient.post()
                        .uri("/api/chat")
                        .header("X-Request-Id", requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_EVENT_STREAM)
                        .body(request)
                        .exchange((clientRequest, clientResponse) -> {
                            if (!clientResponse.getStatusCode().is2xxSuccessful()) {
                                throw mapStatus(clientResponse.getStatusCode());
                            }
                            AgentSseRelay.relay(
                                    clientResponse.getBody(),
                                    output,
                                    objectMapper,
                                    dto.getSessionId(),
                                    requestId
                            );
                            return null;
                        });
                log.info("Agent stream completed. requestId={}, userId={}, sessionId={}, durationMs={}",
                        requestId, userId, dto.getSessionId(), System.currentTimeMillis() - startedAt);
            } catch (IOException e) {
                log.debug("Agent stream disconnected. requestId={}, userId={}, sessionId={}",
                        requestId, userId, dto.getSessionId());
            } catch (Exception e) {
                log.warn("Agent stream failed. requestId={}, userId={}, sessionId={}, reason={}",
                        requestId, userId, dto.getSessionId(), e.getClass().getSimpleName());
                try {
                    AgentSseRelay.writeEvent(
                            output,
                            objectMapper,
                            "error",
                            Map.of("error", "Agent 服务暂时不可用", "requestId", requestId)
                    );
                } catch (IOException ignored) {
                    log.debug("Agent stream error event could not be written. requestId={}", requestId);
                }
            }
        };
    }

    @Override
    public void clearSession(Long userId, String sessionId) {
        properties.assertReady();
        String requestId = newRequestId();
        String upstreamSessionId = sessionKeyFactory.create(userId, sessionId);
        try {
            agentRestClient.delete()
                    .uri("/api/chat/session/{sessionId}", upstreamSessionId)
                    .header("X-Request-Id", requestId)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Agent session cleared. requestId={}, userId={}, sessionId={}",
                    requestId, userId, sessionId);
        } catch (RestClientResponseException e) {
            throw mapStatus(e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw mapResourceAccess(e);
        } catch (RestClientException e) {
            throw AgentProxyException.unavailable();
        }
    }

    private AgentToolCallVO toToolCallVO(AgentUpstreamToolCall toolCall) {
        return AgentToolCallVO.builder()
                .name(toolCall.name())
                .args(toolCall.args() == null ? Collections.emptyMap() : toolCall.args())
                .id(toolCall.id())
                .build();
    }

    private AgentProxyException mapStatus(HttpStatusCode status) {
        if (status.value() == 408 || status.value() == 504) {
            return AgentProxyException.timeout();
        }
        if (status.is5xxServerError() || status.value() == 429) {
            return AgentProxyException.unavailable();
        }
        return AgentProxyException.badResponse();
    }

    private AgentProxyException mapResourceAccess(ResourceAccessException exception) {
        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof SocketTimeoutException || cause instanceof HttpTimeoutException) {
                return AgentProxyException.timeout();
            }
            cause = cause.getCause();
        }
        return AgentProxyException.unavailable();
    }

    private String newRequestId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public record AgentUpstreamChatRequest(
            @JsonProperty("user_id") String userId,
            @JsonProperty("session_id") String sessionId,
            @JsonProperty("session_type") String sessionType,
            String message,
            boolean stream
    ) {
    }

    public record AgentUpstreamChatResponse(
            String reply,
            @JsonProperty("tool_calls") List<AgentUpstreamToolCall> toolCalls,
            @JsonProperty("session_id") String sessionId
    ) {
    }

    public record AgentUpstreamToolCall(
            String name,
            Map<String, Object> args,
            String id
    ) {
    }
}
