package com.yimian.system.common.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class AgentSseRelayTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void rewritesInternalSessionAndSanitizesUpstreamErrors() throws Exception {
        String upstream = """
                event: token
                data: {"content":"hello"}

                event: done
                data: {"reply":"hello","session_id":"internal-secret-session"}

                event: error
                data: {"error":"stack trace and provider details"}

                """;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        AgentSseRelay.relay(
                new ByteArrayInputStream(upstream.getBytes(StandardCharsets.UTF_8)),
                output,
                objectMapper,
                "chat_public"
        );

        String result = output.toString(StandardCharsets.UTF_8);
        assertThat(result).contains("event: token", "hello", "chat_public", "Agent 服务暂时不可用");
        assertThat(result).doesNotContain("internal-secret-session", "stack trace", "provider details");
    }
}
