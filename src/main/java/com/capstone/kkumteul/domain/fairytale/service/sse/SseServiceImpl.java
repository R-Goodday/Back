package com.capstone.kkumteul.domain.fairytale.service.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseServiceImpl implements SseService {
    private final Map<Long, SseEmitter> sseEmittersMap = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long fairytaleId) {
        long timeout = 1000L * 60 * 60;

        SseEmitter emitter = new SseEmitter(timeout);
        sseEmittersMap.put(fairytaleId, emitter);

        emitter.onCompletion(() -> sseEmittersMap.remove(fairytaleId)); //complete시 콜백함수
        emitter.onTimeout(() -> sseEmittersMap.remove(fairytaleId)); //타임아웃시 삭제
        emitter.onError(e -> {
            log.error("SSE 에러 fairytaleId={}", fairytaleId, e);
            sseEmittersMap.remove(fairytaleId);
        }); //전송중 에러시 삭제

        //연결 성공시
        sendToClient(fairytaleId, "connect", "sse connect...");

        return emitter;
    }

    public void sendToClient(Long fairytaleId, String eventName, Object data) {
        SseEmitter emitter = sseEmittersMap.get(fairytaleId);
        if (emitter == null) return;
        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data));
            if ("done".equals(eventName)) {
                emitter.complete(); //sse 스트림 종료
            }
        } catch (IOException e) {
            log.error("SSE 전송 실패 fairytaleId={}", fairytaleId, e);
            sseEmittersMap.remove(fairytaleId);
        }
    }
}