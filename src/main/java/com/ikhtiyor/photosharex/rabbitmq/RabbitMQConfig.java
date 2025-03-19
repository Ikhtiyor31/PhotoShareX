package com.ikhtiyor.photosharex.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
public class RabbitMQConfig {

    public static final String NOTIFICATION_EXCHANGE_NAME = "notification-exchange";
    public static final String EMAIL_ROUTING_KEY = "email.routing-key";
    public static final String FCM_ROUTING_KEY = "fcm.routing-key";
    public static final String EMAIL_QUEUE = "email-queue";
    public static final String FCM_QUEUE = "fcm-queue";

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    public Queue fcmQueue() {
        return new Queue(FCM_QUEUE, true);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(emailQueue).to(topicExchange).with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public Binding fcmBinding(Queue fcmQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(fcmQueue).to(topicExchange).with(FCM_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RetryOperationsInterceptor retryInterceptor() {
        return RetryInterceptorBuilder.stateless()
            .maxAttempts(3)
            .backOffOptions(2000, 2.0, 100000)
            .build();
    }
}
