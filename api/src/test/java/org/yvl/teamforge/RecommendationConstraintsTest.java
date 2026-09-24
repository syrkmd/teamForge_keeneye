package org.yvl.teamforge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.yvl.teamforge.entity.*;
import org.yvl.teamforge.entity.enums.*;
import org.yvl.teamforge.repository.*;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class RecommendationConstraintsTest {

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private SystemRoleRepository systemRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ProjectTemplateRepository projectTemplateRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:17");

    @Test
    public void activeRecommendationCannotBeDuplicated() {
        SystemRole systemRole = systemRoleRepository
                .findByName(SystemRoleName.USER)
                .orElseThrow();

        User user = User.builder()
                .email("test-constraint@example.com")
                .passwordHash("test-password")
                .firstName("Test")
                .lastName("User")
                .about("Test user")
                .githubUsername("test-user")
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .averageRating(0.0)
                .reviewsCount(0)
                .completedProjectsCount(0)
                .completionRate(0.0)
                .systemRole(systemRole)
                .build();

        userRepository.save(user);

        ProjectTemplate template = ProjectTemplate.builder()
                .name("Test Template")
                .description("Test template")
                .isActive(true)
                .createdAt(Instant.now())
                .build();

        projectTemplateRepository.save(template);

        Project project = Project.builder()
                .name("Test Project")
                .description("Test project")
                .status(ProjectStatus.DRAFT)
                .deadline(Instant.now().plusSeconds(86400))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .template(template)
                .owner(user)
                .build();

        projectRepository.save(project);

        Team team = Team.builder()
                .status(TeamStatus.FORMING)
                .createdAt(Instant.now())
                .project(project)
                .build();

        teamRepository.save(team);

        Recommendation recommendation = Recommendation.builder()
                .title("Test recommendation")
                .description("Test recommendation description")
                .priority(RecommendationPriority.MEDIUM)
                .status(RecommendationStatus.OPEN)
                .createdAt(Instant.now())
                .team(team)
                .build();

        recommendationRepository.saveAndFlush(recommendation);

        Recommendation duplicate = Recommendation.builder()
                .title("Duplicate recommendation")
                .description("Duplicate recommendation description")
                .priority(RecommendationPriority.HIGH)
                .status(RecommendationStatus.OPEN)
                .createdAt(Instant.now())
                .team(team)
                .build();

        assertThrows(
                DataIntegrityViolationException.class,
                () -> recommendationRepository.saveAndFlush(duplicate)
        );
    }
}
