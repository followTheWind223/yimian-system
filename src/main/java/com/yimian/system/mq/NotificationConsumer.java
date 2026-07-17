package com.yimian.system.mq;

import com.yimian.system.config.RabbitConfig;
import com.yimian.system.dto.NotificationMessage;
import com.yimian.system.entity.Notification;
import com.yimian.system.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(RabbitConfig.class)
public class NotificationConsumer {

    private final NotificationMapper notificationMapper;

    @RabbitListener(queues = RabbitConfig.QUEUE_NOTIFICATION)
    @Transactional
    public void handleNotification(NotificationMessage message) {
        if (message.getReceiverId() == null || message.getReceiverId().equals(message.getSenderId())) {
            return;
        }
        log.info("Consuming notification: type={}, sender={}, receiver={}",
                message.getType(), message.getSenderId(), message.getReceiverId());

        Notification notification = new Notification();
        notification.setType(message.getType());
        notification.setSenderId(message.getSenderId());
        notification.setReceiverId(message.getReceiverId());
        notification.setTargetType(message.getTargetType());
        notification.setTargetId(message.getTargetId());
        notification.setTitle(message.getTitle());
        notification.setContent(message.getContent());
        notification.setExtra(message.getExtra());
        notification.setRead(false);
        notificationMapper.insert(notification);
    }
}