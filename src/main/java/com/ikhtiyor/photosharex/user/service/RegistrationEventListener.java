package com.ikhtiyor.photosharex.user.service;


import com.ikhtiyor.photosharex.notification.email.EmailDTO;
import com.ikhtiyor.photosharex.rabbitmq.RabbitMQConfig;
import com.ikhtiyor.photosharex.user.dto.RegistrationCompleteEvent;
import com.ikhtiyor.photosharex.user.model.VerificationCode;
import com.ikhtiyor.photosharex.user.repository.VerificationCodeRepository;
import com.ikhtiyor.photosharex.utils.VerificationCodeGenerator;
import java.time.LocalDateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class RegistrationEventListener {

    private final RabbitTemplate rabbitTemplate;
    private final VerificationCodeGenerator verificationCodeGenerator;
    private final VerificationCodeRepository verificationCodeRepository;

    public RegistrationEventListener(RabbitTemplate rabbitTemplate,
        VerificationCodeGenerator verificationCodeGenerator,
        VerificationCodeRepository verificationCodeRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.verificationCodeGenerator = verificationCodeGenerator;
        this.verificationCodeRepository = verificationCodeRepository;
    }

    @ApplicationModuleListener
    public void onSendVerificationEmail(RegistrationCompleteEvent event) {
        String verificationCode = verificationCodeGenerator.generate(4);
        saveVerificationCode(event.email(), verificationCode);
        final var emailDto = new EmailDTO(
            event.email(),
            "PhotoShareX Test",
            "Hi " + event.name() + ",\n" + "Please verify your email \n The verification code : " + verificationCode);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.EMAIL_ROUTING_KEY,
            emailDto
        );
    }

    public void saveVerificationCode(String email, String verificationCode) {
        long expirationMinute = 5;
        verificationCodeRepository.save(new VerificationCode(
            verificationCode,
            LocalDateTime.now().plusMinutes(expirationMinute),
            email
        ));
    }
}
