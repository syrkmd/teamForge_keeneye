package org.yvl.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Import;
import org.yvl.jwtkeycommon.config.JwtProperties;
import org.yvl.jwtkeycommon.key.AuthJwtKeyProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Import({JwtProperties.class, AuthJwtKeyProvider.class})
@EnableScheduling
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

}
