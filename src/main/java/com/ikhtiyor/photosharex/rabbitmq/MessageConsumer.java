package com.ikhtiyor.photosharex.rabbitmq;

import com.ikhtiyor.photosharex.notification.email.EmailNotificationEvent;
import com.ikhtiyor.photosharex.notification.email.EmailService;
import com.ikhtiyor.photosharex.notification.fcm.FCMService;
import com.ikhtiyor.photosharex.notification.fcm.PushNotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    private final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

    private final EmailService emailService;
    private final FCMService fcmService;

    public MessageConsumer(EmailService emailService, FCMService fcmService) {
        this.emailService = emailService;
        this.fcmService = fcmService;
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void receiveEmailDto(EmailNotificationEvent emailNotificationEvent) {
        LOGGER.info("rabbitmq receiving email {} ", emailNotificationEvent);
        emailService.sendVerificationEmail(emailNotificationEvent);
    }

    @RabbitListener(queues = RabbitMQConfig.FCM_QUEUE)
    public void receiveFcmDto(PushNotificationEvent pushNotificationEvent) {
        LOGGER.info("rabbitmq consuming fmc {}", pushNotificationEvent);
        fcmService.sendNotifications(pushNotificationEvent);
    }
}
