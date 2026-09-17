package org.yvl.notificationservice.sse.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.yvl.notificationservice.sse.SseConnectionManager;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class NotificationSseController {

    private final SseConnectionManager sseConnectionManager;

    @GetMapping("/sse/stream")
    public SseEmitter connect(
            @AuthenticationPrincipal Long userId
    ) throws IOException {
        SseEmitter emitter = sseConnectionManager.register(userId);

        emitter.send(
                SseEmitter.event()
                        .name("connected")
        );

        return emitter;
    }
}
