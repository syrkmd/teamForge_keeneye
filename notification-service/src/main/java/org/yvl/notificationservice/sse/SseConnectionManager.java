package org.yvl.notificationservice.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.yvl.notificationservice.config.SseProperties;
import org.yvl.notificationservice.sse.dto.NotificationView;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class SseConnectionManager {

    private final SseProperties sseProperties;

    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> registry = new ConcurrentHashMap<>();

    public SseEmitter register(Long userId) {
        SseEmitter emitter = new SseEmitter(sseProperties.getTimeout());

        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(() -> remove(userId, emitter));
        emitter.onError(_ -> remove(userId, emitter));

        registry.compute(userId, (_, emitters) -> {
            CopyOnWriteArrayList<SseEmitter> current = emitters != null ? emitters : new CopyOnWriteArrayList<>();
            current.add(emitter);
            return current;
        });

        return emitter;
    }

    public void push(Long userId, NotificationView view) {
        CopyOnWriteArrayList<SseEmitter> emitters = registry.get(userId);
        if (emitters != null) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(view)
                    );
                } catch (IOException | IllegalStateException _) {
                    remove(userId, emitter);
                }
            }
        }
    }

    private void remove(Long userId, SseEmitter emitter) {
        registry.computeIfPresent(userId, (_, emitters) -> {
            emitters.remove(emitter);
            return emitters.isEmpty() ? null : emitters;
        });
    }
}
