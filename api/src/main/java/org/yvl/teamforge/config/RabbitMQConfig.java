package org.yvl.teamforge.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "notification")
public class RabbitMQConfig {

    private String notificationExchangeName;

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(notificationExchangeName, true, false);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

}
