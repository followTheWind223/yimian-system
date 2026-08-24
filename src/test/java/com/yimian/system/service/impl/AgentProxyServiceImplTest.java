package com.yimian.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yimian.system.common.utils.AgentSessionKeyFactory;
import com.yimian.system.config.AgentServiceProperties;
import com.yimian.system.dto.AgentChatDto;
import com.yimian.system.service.AgentRateLimiter;
import com.yimian.system.vo.AgentChatVO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class AgentProxyServiceImplTest {

    @Test
    void mapsAuthenticatedUserAndAgentSnakeCaseContract() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.baseUrl("http://agent.test").build();

        AgentServiceProperties properties = new AgentServiceProperties();
        properties.setEnabled(true);
        properties.setInternalToken("test-internal-token-with-enough-entropy");
        AgentRateLimiter rateLimiter = mock(AgentRateLimiter.class);
        AgentSessionKeyFactory keyFactory = new AgentSessionKeyFactory(properties);
        AgentProxyServiceImpl service = new AgentProxyServiceImpl(
                restClient,
                restClient,
                properties,
                rateLimiter,
                keyFactory,
                new ObjectMapper()
        );

        server.expect(requestTo("http://agent.test/api/chat"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Request-Id", matchesPattern("[a-f0-9]{32}")))
                .andExpect(content().string(containsString("\"user_id\":\"42\"")))
                .andExpect(content().string(containsString("\"session_type\":\"support\"")))
                .andExpect(content().string(containsString("\"message\":\"hello\"")))
                .andExpect(content().string(containsString("\"stream\":false")))
                .andExpect(content().string(not(containsString("\"session_id\":\"chat_public\""))))
                .andRespond(withSuccess(
                        "{\"reply\":\"world\",\"tool_calls\":[],\"session_id\":\"internal\"}",
                        MediaType.APPLICATION_JSON
                ));

        AgentChatDto dto = new AgentChatDto();
        dto.setSessionId("chat_public");
        dto.setSessionType("support");
        dto.setMessage("hello");
        AgentChatVO response = service.chat(42L, dto);

        assertThat(response.getReply()).isEqualTo("world");
        assertThat(response.getSessionId()).isEqualTo("chat_public");
        assertThat(response.getRequestId()).hasSize(32);
        server.verify();
    }
}
