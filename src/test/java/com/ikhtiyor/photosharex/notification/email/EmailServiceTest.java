package com.ikhtiyor.photosharex.notification.email;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ikhtiyor.photosharex.exception.EmailSendingFailException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendVerificationEmail() {
        //Given
        EmailDTO emailDto = new EmailDTO("recipient@example.com", "Test Subject", "Test Message");
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        // Act
        emailService.sendVerificationEmail(emailDto);

        // Assert
        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendVerificationEmailThrowException_When() {
        // Given
        EmailDTO emailDto = new EmailDTO("recipient@example.com", "Test Subject", "Test Message");
        javaMailSender.createMimeMessage();

        assertThatThrownBy(() -> emailService.sendVerificationEmail(emailDto))
            .isInstanceOf(EmailSendingFailException.class)
            .hasMessage("Error sending verification email");

        // Assert
        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }
}