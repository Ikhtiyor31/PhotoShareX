package com.ikhtiyor.photosharex.like.service;

import com.ikhtiyor.photosharex.like.dto.LikeNotificationEvent;
import com.ikhtiyor.photosharex.notification.fcm.PushNotificationEvent;
import com.ikhtiyor.photosharex.rabbitmq.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class LikeNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public LikeNotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @ApplicationModuleListener
    public void processLikeNotifications(LikeNotificationEvent events) {
        PushNotificationEvent pushNotificationEvent = PushNotificationEvent.fromLike(events);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.NOTIFICATION_EXCHANGE_NAME,
            RabbitMQConfig.FCM_ROUTING_KEY,
            pushNotificationEvent
        );
    }

}
