package com.ikhtiyor.photosharex.rabbitmq;

import com.ikhtiyor.photosharex.notification.email.EmailDTO;
import com.ikhtiyor.photosharex.notification.email.EmailService;
import com.ikhtiyor.photosharex.notification.fcm.FcmDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    private final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

    private final EmailService emailService;

    public MessageConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "email-queue")
    public void receiveEmailDto(EmailDTO emailDTO) {
        LOGGER.info("rabbitmq receiving email {} ", emailDTO);
        emailService.sendVerificationEmail(emailDTO);
    }

    @RabbitListener(queues = "fcm-queue")
    public void receiveFcmDto(FcmDTO fcmDTO) {
        LOGGER.info("rabbitmq consuming fmc {}", fcmDTO);
    }
}
