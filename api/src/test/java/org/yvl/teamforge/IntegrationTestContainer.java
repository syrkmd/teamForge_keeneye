package org.yvl.teamforge;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.yvl.teamforge.entity.User;
import org.yvl.teamforge.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class IntegrationTestContainer {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:17");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbitMQContainer =
            new RabbitMQContainer("rabbitmq:4-management");

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    EntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Test
    void postgresConnectionWorks() {
        Object result = entityManager
                .createNativeQuery("SELECT 1")
                .getSingleResult();

        assertEquals(1, result);
    }

    @Test
    void rabbitMqConnectionWorks() {
        rabbitTemplate.execute(channel -> {
            assertTrue(channel.isOpen());
            return null;
        });
    }

    @Test
    void userRepositoryFindsSeededUserByEmail() {
        String email = "admin@teamforge.example";

        Optional<User> user = userRepository.findByEmail(email);

        assertTrue(user.isPresent());
        assertEquals(email, user.get().getEmail());
        assertNotNull(user.get().getSystemRole());
    }

}
