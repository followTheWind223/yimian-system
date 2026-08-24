package com.yimian.system.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yimian.system.common.exception.AgentProxyException;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.config.AgentServiceProperties;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;

class AgentRateLimiterTest {

    @Test
    void rejectsRequestsOverConfiguredLimit() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        AgentServiceProperties properties = new AgentServiceProperties();
        properties.getRateLimit().setMaxRequests(2);
        properties.getRateLimit().setWindow(Duration.ofSeconds(60));
        when(redisTemplate.execute(any(), anyList(), any())).thenReturn(3L);

        AgentRateLimiter limiter = new AgentRateLimiter(redisTemplate, properties);

        assertThatThrownBy(() -> limiter.check(1001L))
                .isInstanceOfSatisfying(AgentProxyException.class, exception -> {
                    org.assertj.core.api.Assertions.assertThat(exception.getResultCode())
                            .isEqualTo(ResultCode.AGENT_RATE_LIMITED);
                    org.assertj.core.api.Assertions.assertThat(exception.getRetryAfterSeconds())
                            .isEqualTo(60);
                });
    }
}
