package org.yvl.teamforge;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.yvl.teamforge.entity.*;
import org.yvl.teamforge.entity.enums.*;
import org.yvl.teamforge.repository.*;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class NotificationConstraintsTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private SystemRoleRepository systemRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectTemplateRepository projectTemplateRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectRoleRepository projectRoleRepository;

    @Autowired
    private EntityManager entityManager;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:17");

    @Test
    void deletingInvitationSetsNotificationInvitationToNull() {
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
                .name("Notification Test Template")
                .description("Test template")
                .isActive(true)
                .createdAt(Instant.now())
                .build();

        projectTemplateRepository.save(template);

        Project project = Project.builder()
                .name("Notification Test Project")
                .description("Test project")
                .status(ProjectStatus.DRAFT)
                .deadline(Instant.now().plusSeconds(86400))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .template(template)
                .owner(user)
                .build();

        projectRepository.save(project);

        ProjectRole role = ProjectRole.builder()
                .roleName("Developer")
                .description("Test role")
                .requiredCount(1)
                .status(ProjectRoleStatus.OPEN)
                .project(project)
                .build();

        projectRoleRepository.save(role);

        Invitation invitation = Invitation.builder()
                .status(InvitationStatus.PENDING)
                .createdAt(Instant.now())
                .project(project)
                .projectRole(role)
                .user(user)
                .build();

        invitationRepository.saveAndFlush(invitation);

        Notification notification = Notification.builder()
                .type(NotificationType.INVITATION_RECEIVED)
                .title("Test notification")
                .message("Test invitation")
                .isRead(false)
                .createdAt(Instant.now())
                .user(user)
                .invitation(invitation)
                .build();

        notificationRepository.saveAndFlush(notification);

        entityManager.clear();

        invitationRepository.deleteById(invitation.getId());
        invitationRepository.flush();

        entityManager.clear();

        Notification savedNotification = notificationRepository
                .findById(notification.getId())
                .orElseThrow();

        assertNull(savedNotification.getInvitation());
    }
}
