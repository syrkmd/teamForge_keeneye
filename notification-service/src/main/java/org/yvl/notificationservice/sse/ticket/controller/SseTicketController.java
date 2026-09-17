package org.yvl.notificationservice.sse.ticket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yvl.notificationservice.sse.ticket.dto.SseTicketResponse;
import org.yvl.notificationservice.sse.ticket.service.SseTicketService;

@RestController
@RequiredArgsConstructor
public class SseTicketController {

    private final SseTicketService sseTicketService;

    @PostMapping("/sse/ticket")
    public ResponseEntity<SseTicketResponse> issueTicket(
            @AuthenticationPrincipal Long userId
    ) {
        String ticket = sseTicketService.issue(userId);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(new SseTicketResponse(ticket));
    }
}
