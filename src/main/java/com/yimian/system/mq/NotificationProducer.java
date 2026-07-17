package com.yimian.system.mq;

import com.yimian.system.config.RabbitConfig;
import com.yimian.system.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(RabbitConfig.class)
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(NotificationMessage message) {
        if (message.getReceiverId() == null || message.getReceiverId().equals(message.getSenderId())) {
            return;
        }
        log.debug("Sending notification: type={}, receiverId={}", message.getType(), message.getReceiverId());
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NOTIFICATION,
                RabbitConfig.ROUTING_KEY,
                message);
    }
}