package com.yimian.system.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(AgentServiceProperties.class)
public class AgentClientConfig {

    @Bean
    @Qualifier("agentRestClient")
    public RestClient agentRestClient(RestClient.Builder builder, AgentServiceProperties properties) {
        return buildClient(builder, properties, properties.getReadTimeout());
    }

    @Bean
    @Qualifier("agentStreamRestClient")
    public RestClient agentStreamRestClient(RestClient.Builder builder, AgentServiceProperties properties) {
        return buildClient(builder, properties, properties.getStreamReadTimeout());
    }

    private RestClient buildClient(RestClient.Builder builder,
                                   AgentServiceProperties properties,
                                   Duration readTimeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getConnectTimeout());
        requestFactory.setReadTimeout(readTimeout);

        RestClient.Builder clientBuilder = builder.clone()
                .baseUrl(properties.normalizedBaseUrl())
                .requestFactory(requestFactory)
                .defaultHeader("X-Yimian-Service", "system");

        if (StringUtils.hasText(properties.getInternalToken())) {
            clientBuilder.defaultHeader(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + properties.getInternalToken().trim()
            );
        }
        return clientBuilder.build();
    }
}
