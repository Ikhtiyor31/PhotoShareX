package com.ikhtiyor.photosharex.notification.email;

import com.ikhtiyor.photosharex.exception.EmailSendingFailException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String sender;
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendVerificationEmail(EmailNotificationEvent emailNotificationEvent) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            mimeMessage.setFrom(sender);
            mimeMessage.addRecipient(RecipientType.TO,
                new InternetAddress(emailNotificationEvent.to()));
            mimeMessage.setSubject(emailNotificationEvent.title());
            mimeMessage.setText(emailNotificationEvent.message());
            javaMailSender.send(mimeMessage);
            LOGGER.info("Verification email successfully sent to {}", emailNotificationEvent.to());
        } catch (Exception e) {
            throw new EmailSendingFailException("Error sending verification email", e);
        }
    }
}
