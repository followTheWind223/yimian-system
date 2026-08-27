package com.yimian.system.service.impl;

import com.yimian.system.common.exception.AgentProxyException;
import com.yimian.system.config.AgentServiceProperties;
import com.yimian.system.dto.AgentModelCreateDto;
import com.yimian.system.dto.AgentModelUpdateDto;
import com.yimian.system.dto.AgentProviderCreateDto;
import com.yimian.system.dto.AgentProviderUpdateDto;
import com.yimian.system.dto.AgentProfileModelsUpdateDto;
import com.yimian.system.service.AgentModelControlService;
import com.yimian.system.vo.AgentModelVO;
import com.yimian.system.vo.AgentProfileModelVO;
import com.yimian.system.vo.AgentProfileVO;
import com.yimian.system.vo.AgentProviderTestVO;
import com.yimian.system.vo.AgentProviderVO;
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Service
public class AgentModelControlServiceImpl implements AgentModelControlService {

    private final RestClient agentRestClient;
    private final AgentServiceProperties properties;

    public AgentModelControlServiceImpl(
            @Qualifier("agentRestClient") RestClient agentRestClient,
            AgentServiceProperties properties) {
        this.agentRestClient = agentRestClient;
        this.properties = properties;
    }

    @Override
    public List<AgentProviderVO> listProviders() {
        return getList("/api/ai/admin/providers", AgentProviderVO[].class);
    }

    @Override
    public AgentProviderVO createProvider(AgentProviderCreateDto request) {
        return post("/api/ai/admin/providers", request, AgentProviderVO.class);
    }

    @Override
    public AgentProviderVO updateProvider(Long id, AgentProviderUpdateDto request) {
        return put("/api/ai/admin/providers/{id}", request, AgentProviderVO.class, id);
    }

    @Override
    public AgentProviderTestVO testProvider(Long id) {
        return post("/api/ai/admin/providers/{id}/test", null, AgentProviderTestVO.class, id);
    }

    @Override
    public AgentProviderTestVO testProviderConfig(AgentProviderCreateDto request) {
        return post("/api/ai/admin/providers/test", request, AgentProviderTestVO.class);
    }

    @Override
    public List<AgentModelVO> listModels() {
        return getList("/api/ai/admin/models", AgentModelVO[].class);
    }

    @Override
    public AgentModelVO createModel(AgentModelCreateDto request) {
        return post("/api/ai/admin/models", request, AgentModelVO.class);
    }

    @Override
    public AgentModelVO updateModel(Long id, AgentModelUpdateDto request) {
        return put("/api/ai/admin/models/{id}", request, AgentModelVO.class, id);
    }

    @Override
    public List<AgentProfileVO> listProfiles() {
        return getList("/api/ai/admin/profiles", AgentProfileVO[].class);
    }

    @Override
    public List<AgentProfileModelVO> listProfileModels(String profileCode) {
        return getList("/api/ai/profiles/{profileCode}/models", AgentProfileModelVO[].class, profileCode);
    }

    @Override
    public List<AgentProfileModelVO> updateProfileModels(String profileCode, AgentProfileModelsUpdateDto request) {
        AgentProfileModelVO[] response = put(
                "/api/ai/admin/profiles/{profileCode}/models",
                request,
                AgentProfileModelVO[].class,
                profileCode
        );
        return response == null ? List.of() : List.of(response);
    }

    private <T> List<T> getList(String path, Class<T[]> responseType, Object... uriVariables) {
        T[] response = get(path, responseType, uriVariables);
        return response == null ? List.of() : List.of(response);
    }

    private <T> T get(String path, Class<T> responseType, Object... uriVariables) {
        properties.assertReady();
        try {
            T response = agentRestClient.get()
                    .uri(path, uriVariables)
                    .header("X-Request-Id", requestId())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(responseType);
            if (response == null) throw AgentProxyException.badResponse();
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

    private <T> T post(String path, Object body, Class<T> responseType, Object... uriVariables) {
        properties.assertReady();
        try {
            var request = agentRestClient.post()
                    .uri(path, uriVariables)
                    .header("X-Request-Id", requestId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
            T response = body == null ? request.retrieve().body(responseType) : request.body(body).retrieve().body(responseType);
            if (response == null) throw AgentProxyException.badResponse();
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

    private <T> T put(String path, Object body, Class<T> responseType, Object... uriVariables) {
        properties.assertReady();
        try {
            T response = agentRestClient.put()
                    .uri(path, uriVariables)
                    .header("X-Request-Id", requestId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(responseType);
            if (response == null) throw AgentProxyException.badResponse();
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
        if (status == 404) return AgentProxyException.modelConfigNotFound();
        if (status == 409) return AgentProxyException.modelConfigConflict();
        if (status == 422) return AgentProxyException.modelConfigInvalid();
        if (status == 408 || status == 504) return AgentProxyException.timeout();
        if (exception.getStatusCode().is5xxServerError() || status == 429) return AgentProxyException.unavailable();
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

    private String requestId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
