package org.yvl.teamforge;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Import;
import org.yvl.jwtkeycommon.config.JwtProperties;
import org.yvl.jwtkeycommon.key.AuthJwtKeyProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Import({JwtProperties.class, AuthJwtKeyProvider.class})
@EnableScheduling
@EnableAsync
public class TeamForgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamForgeApplication.class, args);
    }

}
