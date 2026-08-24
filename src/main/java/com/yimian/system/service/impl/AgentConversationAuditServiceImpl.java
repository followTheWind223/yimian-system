package com.yimian.system.service.impl;

import com.yimian.system.common.exception.AgentProxyException;
import com.yimian.system.config.AgentServiceProperties;
import com.yimian.system.service.AgentConversationAuditService;
import com.yimian.system.vo.AgentConversationDetailVO;
import com.yimian.system.vo.AgentConversationSessionPageVO;
import com.yimian.system.vo.AgentConversationStatsVO;
import com.yimian.system.vo.AgentUsageAnalyticsVO;
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Service
public class AgentConversationAuditServiceImpl implements AgentConversationAuditService {

    private final RestClient agentRestClient;
    private final AgentServiceProperties properties;

    public AgentConversationAuditServiceImpl(
            @Qualifier("agentRestClient") RestClient agentRestClient,
            AgentServiceProperties properties) {
        this.agentRestClient = agentRestClient;
        this.properties = properties;
    }

    @Override
    public AgentConversationStatsVO getStats() {
        return get("/api/chat/admin/stats", AgentConversationStatsVO.class);
    }

    @Override
    public AgentUsageAnalyticsVO getUsageAnalytics(int days) {
        properties.assertReady();
        try {
            AgentUsageAnalyticsVO response = agentRestClient.get()
                    .uri(builder -> builder
                            .path("/api/chat/admin/analytics")
                            .queryParam("days", days)
                            .build())
                    .header("X-Request-Id", newRequestId())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(AgentUsageAnalyticsVO.class);
            if (response == null || response.summary() == null || response.tokenTrend() == null) {
                throw AgentProxyException.badResponse();
            }
            return response;
        } catch (AgentProxyException e) {
            throw e;
        } catch (RestClientResponseException e) {
            throw mapStatus(e);
        } catch (ResourceAccessException e) {
            throw mapResourceAccess(e);
        } catch (RestClientException e) {
            throw AgentProxyException.unavailable();
        }
    }

    @Override
    public AgentConversationSessionPageVO listSessions(
            int page,
            int size,
            Long userId,
            String sessionType,
            Integer status) {
        properties.assertReady();
        try {
            AgentConversationSessionPageVO response = agentRestClient.get()
                    .uri(builder -> {
                        builder.path("/api/chat/admin/sessions")
                                .queryParam("page_num", page)
                                .queryParam("page_size", size);
                        if (userId != null) {
                            builder.queryParam("user_id", userId);
                        }
                        if (sessionType != null && !sessionType.isBlank()) {
                            builder.queryParam("session_type", sessionType);
                        }
                        if (status != null) {
                            builder.queryParam("status", status);
                        }
                        return builder.build();
                    })
                    .header("X-Request-Id", newRequestId())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(AgentConversationSessionPageVO.class);
            if (response == null || response.list() == null) {
                throw AgentProxyException.badResponse();
            }
            return response;
        } catch (AgentProxyException e) {
            throw e;
        } catch (RestClientResponseException e) {
            throw mapStatus(e);
        } catch (ResourceAccessException e) {
            throw mapResourceAccess(e);
        } catch (RestClientException e) {
            throw AgentProxyException.unavailable();
        }
    }

    @Override
    public AgentConversationDetailVO getSessionDetail(Long sessionId, int page, int size) {
        properties.assertReady();
        try {
            AgentConversationDetailVO response = agentRestClient.get()
                    .uri(builder -> builder
                            .path("/api/chat/admin/sessions/{sessionId}")
                            .queryParam("page_num", page)
                            .queryParam("page_size", size)
                            .build(sessionId))
                    .header("X-Request-Id", newRequestId())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(AgentConversationDetailVO.class);
            if (response == null || response.session() == null || response.messages() == null) {
                throw AgentProxyException.badResponse();
            }
            return response;
        } catch (AgentProxyException e) {
            throw e;
        } catch (RestClientResponseException e) {
            throw mapStatus(e);
        } catch (ResourceAccessException e) {
            throw mapResourceAccess(e);
        } catch (RestClientException e) {
            throw AgentProxyException.unavailable();
        }
    }

    private <T> T get(String path, Class<T> responseType) {
        properties.assertReady();
        try {
            T response = agentRestClient.get()
                    .uri(path)
                    .header("X-Request-Id", newRequestId())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(responseType);
            if (response == null) {
                throw AgentProxyException.badResponse();
            }
            return response;
        } catch (AgentProxyException e) {
            throw e;
        } catch (RestClientResponseException e) {
            throw mapStatus(e);
        } catch (ResourceAccessException e) {
            throw mapResourceAccess(e);
        } catch (RestClientException e) {
            throw AgentProxyException.unavailable();
        }
    }

    private AgentProxyException mapStatus(RestClientResponseException exception) {
        int status = exception.getStatusCode().value();
        if (status == 404) {
            return AgentProxyException.chatSessionNotFound();
        }
        if (status == 408 || status == 504) {
            return AgentProxyException.timeout();
        }
        if (exception.getStatusCode().is5xxServerError() || status == 429) {
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
}
