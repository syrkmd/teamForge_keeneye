package org.yvl.notificationservice.sse.ticket.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yvl.notificationservice.config.SseProperties;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SseTicketServiceTest {

    @Mock
    private SseProperties sseProperties;

    @InjectMocks
    private SseTicketService sseTicketService;

    @Test
    void ticketCanBeConsumedOnlyOnce() {

        when(sseProperties.getTicketTtl())
                .thenReturn(30000L);

        String ticket = sseTicketService.issue(100L);

        var result1 = sseTicketService.validateAndConsume(ticket);
        var result2 = sseTicketService.validateAndConsume(ticket);

        assertEquals(Optional.of(100L), result1);
        assertEquals(Optional.empty(), result2);
    }

    @Test
    void expiredTicketCannotBeConsumed() throws InterruptedException {

        when(sseProperties.getTicketTtl())
                .thenReturn(1L);

        String ticket = sseTicketService.issue(100L);

        Thread.sleep(10);

        var result = sseTicketService.validateAndConsume(ticket);

        assertEquals(Optional.empty(), result);
    }
}