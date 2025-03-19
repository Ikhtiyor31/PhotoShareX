package com.ikhtiyor.photosharex.comment.service;


import com.ikhtiyor.photosharex.comment.dto.CommentNotificationEvent;
import com.ikhtiyor.photosharex.notification.fcm.PushNotificationEvent;
import com.ikhtiyor.photosharex.rabbitmq.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class CommentNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public CommentNotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @ApplicationModuleListener
    public void processCommentNotifications(CommentNotificationEvent events) {
        PushNotificationEvent pushNotificationEvent = PushNotificationEvent.fromComment(events);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.NOTIFICATION_EXCHANGE_NAME,
            RabbitMQConfig.FCM_ROUTING_KEY,
            pushNotificationEvent
        );
    }
}
