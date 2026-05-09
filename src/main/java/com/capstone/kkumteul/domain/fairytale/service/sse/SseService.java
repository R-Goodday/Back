package com.capstone.kkumteul.domain.fairytale.service.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {
    SseEmitter subscribe(Long fairytaleId);
    void sendToClient(Long fairytaleId, String eventName, Object data);
}