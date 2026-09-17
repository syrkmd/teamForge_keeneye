package org.yvl.notificationservice.sse.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class TicketEntry {

    private Long userId;
    private String purpose;
    private Instant expiresAt;
}
