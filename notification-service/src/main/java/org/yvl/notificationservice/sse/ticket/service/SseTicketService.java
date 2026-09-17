package org.yvl.notificationservice.sse.ticket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.yvl.notificationservice.config.SseProperties;
import org.yvl.notificationservice.sse.ticket.dto.TicketEntry;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SseTicketService {

    private final SseProperties sseProperties;
    private final Map<String, TicketEntry> tickets = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    public String issue(Long userId) {

        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        String ticket = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        Instant expiresAt = Instant.now().plusMillis(sseProperties.getTicketTtl());

        tickets.put(ticket, new TicketEntry(userId, "sse", expiresAt));

        return ticket;
    }

    public Optional<Long> validateAndConsume(String ticket) {
        TicketEntry entry = tickets.get(ticket);

        if (entry == null) {
            return Optional.empty();
        }

        boolean consumed = tickets.remove(ticket, entry);

        if (!consumed) {
            return Optional.empty();
        }

        if (entry.getExpiresAt().isBefore(Instant.now())) {
            return Optional.empty();
        }

        if (!entry.getPurpose().equals("sse")) {
            return Optional.empty();
        }

        return Optional.of(entry.getUserId());
    }

    @Scheduled(fixedDelayString = "${sse.ticket-cleanup-interval}")
    public void cleanup() {
        tickets.entrySet().removeIf(
                entry -> entry.getValue().getExpiresAt().isBefore(Instant.now())
        );
    }
}
