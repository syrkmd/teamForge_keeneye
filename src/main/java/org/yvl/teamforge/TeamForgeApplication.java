package org.yvl.teamforge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TeamForgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamForgeApplication.class, args);
    }

}
