package com.yimian.system.config;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yimian.system.common.exception.AgentProxyException;
import org.junit.jupiter.api.Test;

class AgentServicePropertiesTest {

    @Test
    void rejectsInternalTokensShorterThanThirtyTwoCharacters() {
        AgentServiceProperties properties = new AgentServiceProperties();
        properties.setEnabled(true);
        properties.setInternalToken("too-short");

        assertThatThrownBy(properties::assertReady)
                .isInstanceOf(AgentProxyException.class);
    }
}
