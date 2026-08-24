package com.yimian.system.common.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.yimian.system.config.AgentServiceProperties;
import org.junit.jupiter.api.Test;

class AgentSessionKeyFactoryTest {

    @Test
    void createsStableUserScopedOpaqueKey() {
        AgentServiceProperties properties = new AgentServiceProperties();
        properties.setEnabled(true);
        properties.setInternalToken("test-internal-token-with-enough-entropy");
        AgentSessionKeyFactory factory = new AgentSessionKeyFactory(properties);

        String first = factory.create(1001L, "chat_demo");
        String same = factory.create(1001L, "chat_demo");
        String anotherUser = factory.create(1002L, "chat_demo");

        assertThat(first).isEqualTo(same);
        assertThat(first).isNotEqualTo(anotherUser);
        assertThat(first).doesNotContain("1001", "chat_demo");
    }
}
