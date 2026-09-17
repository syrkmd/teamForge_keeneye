package org.yvl.notificationservice.notification.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.yvl.notificationservice.notification.dto.response.NotificationItemView;
import org.yvl.notificationservice.notification.service.NotificationService;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public Page<NotificationItemView> getNotifications(
            @AuthenticationPrincipal Long userId,
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @Max(100) @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean unreadOnly
    ) {
        return notificationService.getNotifications(userId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")), unreadOnly);
    }

    @PostMapping("/{notificationId}/read")
    public NotificationItemView readNotification(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long notificationId
    ) {
        return notificationService.markAsRead(userId, notificationId);
    }
}
