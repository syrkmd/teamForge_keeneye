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
public class TeamMemberConstraintsTest {

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private SystemRoleRepository systemRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectTemplateRepository projectTemplateRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ProjectRoleRepository projectRoleRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:17");

    @Test
    void activeTeamMemberCannotBeDuplicated() {
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

        ProjectRole role = ProjectRole.builder()
                .roleName("Developer")
                .description("Test role")
                .requiredCount(1)
                .status(ProjectRoleStatus.OPEN)
                .project(project)
                .build();

        projectRoleRepository.save(role);

        TeamMember member = TeamMember.builder()
                .status(TeamMemberStatus.ACTIVE)
                .joinedAt(Instant.now())
                .team(team)
                .user(user)
                .projectRole(role)
                .build();

        teamMemberRepository.saveAndFlush(member);

        TeamMember duplicate = TeamMember.builder()
                .status(TeamMemberStatus.ACTIVE)
                .joinedAt(Instant.now())
                .team(team)
                .user(user)
                .projectRole(role)
                .build();

        assertThrows(
                DataIntegrityViolationException.class,
                () -> teamMemberRepository.saveAndFlush(duplicate)
        );
    }
}
