package com.yimian.system.config;

import com.yimian.system.common.exception.AgentProxyException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.util.StringUtils;

@Data
@Validated
@ConfigurationProperties(prefix = "agent.service")
public class AgentServiceProperties {

    private boolean enabled = false;

    @NotBlank
    private String baseUrl = "http://127.0.0.1:8000";

    private String internalToken = "";

    @NotNull
    private Duration connectTimeout = Duration.ofSeconds(5);

    @NotNull
    private Duration readTimeout = Duration.ofMinutes(2);

    @NotNull
    private Duration streamReadTimeout = Duration.ofMinutes(10);

    @Valid
    @NotNull
    private RateLimit rateLimit = new RateLimit();

    public void assertReady() {
        if (!enabled) {
            throw AgentProxyException.disabled();
        }
        if (!StringUtils.hasText(internalToken) || internalToken.trim().length() < 32) {
            throw AgentProxyException.misconfigured();
        }
    }

    public String normalizedBaseUrl() {
        URI uri;
        try {
            uri = URI.create(baseUrl.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("agent.service.base-url 格式不正确", e);
        }
        String scheme = uri.getScheme();
        if (uri.getHost() == null || !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))) {
            throw new IllegalStateException("agent.service.base-url 必须是完整的 http/https 地址");
        }
        if (uri.getUserInfo() != null || uri.getQuery() != null || uri.getFragment() != null
                || (uri.getPath() != null && !uri.getPath().isBlank() && !"/".equals(uri.getPath()))) {
            throw new IllegalStateException("agent.service.base-url 只能配置服务根地址，不能包含凭据、路径、查询或片段");
        }
        String normalized = uri.toString();
        return normalized.endsWith("/") ? normalized.substring(0, normalized.length() - 1) : normalized;
    }

    @Data
    public static class RateLimit {

        @Min(1)
        private int maxRequests = 20;

        @NotNull
        private Duration window = Duration.ofMinutes(1);
    }
}
