package org.yvl.notificationservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.StatelessRetryOperationsInterceptor;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;
import org.yvl.notificationservice.exception.NotificationNotFoundException;

import java.time.Duration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "notification")
public class RabbitMQConfig {

    private String notificationExchangeName;
    private String notificationQueueName;
    private String dlxExchangeName;
    private String dlqQueueName;
    private String routingKey;

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(notificationExchangeName, true, false);
    }

    @Bean
    public FanoutExchange notificationDlx() {
        return new FanoutExchange(dlxExchangeName, true, false);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(notificationQueueName)
                .deadLetterExchange(dlxExchangeName)
                .build();
    }

    @Bean
    public Queue notificationDlq() {
        return QueueBuilder.durable(dlqQueueName)
                .build();
    }

    @Bean
    public Binding notificationQueueBinding(
            Queue notificationQueue,
            TopicExchange notificationExchange
    ) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(notificationExchange)
                .with(routingKey);
    }

    @Bean
    public Binding notificationDlqBinding(
            Queue notificationDlq,
            FanoutExchange notificationDlx
    ) {
        return BindingBuilder
                .bind(notificationDlq)
                .to(notificationDlx);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RetryPolicy retryPolicy() {
        return RetryPolicy.builder()
                .maxRetries(2)
                .delay(Duration.ofSeconds(1))
                .multiplier(2)
                .maxDelay(Duration.ofSeconds(10))
                .excludes(NotificationNotFoundException.class)
                .build();
    }

    @Bean
    public RejectAndDontRequeueRecoverer rejectAndDontRequeueRecoverer() {
        return new RejectAndDontRequeueRecoverer();
    }

    @Bean
    public StatelessRetryOperationsInterceptor retryInterceptor(
            RetryPolicy retryPolicy,
            RejectAndDontRequeueRecoverer rejectAndDontRequeueRecoverer
    ) {
        return RetryInterceptorBuilder.stateless()
                .retryPolicy(retryPolicy)
                .recoverer(rejectAndDontRequeueRecoverer)
                .build();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter,
            StatelessRetryOperationsInterceptor retryInterceptor
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setAdviceChain(retryInterceptor);

        return factory;
    }
}
