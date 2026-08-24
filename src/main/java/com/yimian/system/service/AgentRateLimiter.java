package com.yimian.system.service;

import com.yimian.system.common.exception.AgentProxyException;
import com.yimian.system.config.AgentServiceProperties;
import java.time.Duration;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgentRateLimiter {

    private static final DefaultRedisScript<Long> RATE_LIMIT_SCRIPT = new DefaultRedisScript<>(
            "local current = redis.call('INCR', KEYS[1]); "
                    + "if current == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]); end; "
                    + "return current;",
            Long.class
    );

    private final StringRedisTemplate stringRedisTemplate;
    private final AgentServiceProperties properties;

    public void check(Long userId) {
        AgentServiceProperties.RateLimit config = properties.getRateLimit();
        Duration window = config.getWindow();
        long windowSeconds = Math.max(1, window.toSeconds());
        String key = "agent:rate-limit:chat:" + userId;

        try {
            Long current = stringRedisTemplate.execute(
                    RATE_LIMIT_SCRIPT,
                    Collections.singletonList(key),
                    String.valueOf(windowSeconds)
            );
            if (current == null) {
                throw AgentProxyException.unavailable();
            }
            if (current > config.getMaxRequests()) {
                throw AgentProxyException.rateLimited(windowSeconds);
            }
        } catch (DataAccessException e) {
            throw AgentProxyException.unavailable();
        }
    }
}
