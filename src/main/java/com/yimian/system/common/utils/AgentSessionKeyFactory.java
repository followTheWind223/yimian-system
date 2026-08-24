package com.yimian.system.common.utils;

import com.yimian.system.config.AgentServiceProperties;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgentSessionKeyFactory {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final AgentServiceProperties properties;

    public String create(Long userId, String sessionId) {
        properties.assertReady();
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(
                    properties.getInternalToken().trim().getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            ));
            byte[] digest = mac.doFinal((userId + ":" + sessionId).getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("无法生成 Agent 会话键", e);
        }
    }
}
