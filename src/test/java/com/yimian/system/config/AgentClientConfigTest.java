package com.yimian.system.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class AgentClientConfigTest {

    @Test
    void addsInternalAuthenticationHeadersToAgentRequests() throws IOException {
        AtomicReference<List<String>> authorization = new AtomicReference<>();
        AtomicReference<List<String>> serviceName = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/chat/health", exchange -> {
            authorization.set(exchange.getRequestHeaders().get("Authorization"));
            serviceName.set(exchange.getRequestHeaders().get("X-Yimian-Service"));
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.start();

        try {
            AgentServiceProperties properties = new AgentServiceProperties();
            properties.setBaseUrl("http://127.0.0.1:" + server.getAddress().getPort());
            properties.setInternalToken("test-internal-token-with-enough-entropy");
            RestClient client = new AgentClientConfig().agentRestClient(
                    RestClient.builder(),
                    properties
            );

            client.get().uri("/api/chat/health").retrieve().toBodilessEntity();

            assertThat(authorization.get())
                    .containsExactly("Bearer test-internal-token-with-enough-entropy");
            assertThat(serviceName.get()).containsExactly("system");
        } finally {
            server.stop(0);
        }
    }
}
