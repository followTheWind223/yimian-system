package com.yimian.system.common.exception;

import com.yimian.system.common.result.ResultCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AgentProxyException extends RuntimeException {

    private final ResultCode resultCode;
    private final HttpStatus httpStatus;
    private final long retryAfterSeconds;

    private AgentProxyException(ResultCode resultCode, HttpStatus httpStatus, long retryAfterSeconds) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
        this.httpStatus = httpStatus;
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public static AgentProxyException disabled() {
        return new AgentProxyException(ResultCode.AGENT_SERVICE_DISABLED, HttpStatus.SERVICE_UNAVAILABLE, 0);
    }

    public static AgentProxyException misconfigured() {
        return new AgentProxyException(ResultCode.AGENT_SERVICE_MISCONFIGURED, HttpStatus.SERVICE_UNAVAILABLE, 0);
    }

    public static AgentProxyException unavailable() {
        return new AgentProxyException(ResultCode.AGENT_SERVICE_UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE, 5);
    }

    public static AgentProxyException timeout() {
        return new AgentProxyException(ResultCode.AGENT_REQUEST_TIMEOUT, HttpStatus.GATEWAY_TIMEOUT, 0);
    }

    public static AgentProxyException rateLimited(long retryAfterSeconds) {
        return new AgentProxyException(
                ResultCode.AGENT_RATE_LIMITED,
                HttpStatus.TOO_MANY_REQUESTS,
                Math.max(1, retryAfterSeconds)
        );
    }

    public static AgentProxyException badResponse() {
        return new AgentProxyException(ResultCode.AGENT_BAD_RESPONSE, HttpStatus.BAD_GATEWAY, 0);
    }
}
