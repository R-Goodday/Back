package com.capstone.kkumteul.domain.game.service;

import com.capstone.kkumteul.domain.game.exception.SessionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 게임 세션 저장소 — ConcurrentHashMap 기반 인메모리 관리.
 *
 * <p>명세 요구사항:</p>
 * <ul>
 *   <li>게임 세션은 서버 메모리에만 유지 (DB 저장 X)</li>
 *   <li>TTL 15분: 마지막 활동 시간 기준, 미활동 시 자동 삭제</li>
 *   <li>@Scheduled(fixedDelay=60000)로 1분마다 만료 세션 정리</li>
 *   <li>앱 종료 시 세션 소멸 → 재진입 시 처음부터 시작</li>
 * </ul>
 */
@Slf4j
@Component
public class GameSessionManager {

    private static final long SESSION_TTL_MINUTES = 15;

    /** sessionId → GameSession. ConcurrentHashMap으로 동시 접근 안전 */
    private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();

    public void save(GameSession session) {
        sessions.put(session.getSessionId(), session);
    }

    /**
     * 세션 조회 + TTL 갱신.
     * 세션이 없으면 SessionNotFoundException (404) → "게임을 다시 시작해주세요"
     */
    public GameSession get(String sessionId) {
        GameSession session = sessions.get(sessionId);
        if (session == null) {
            throw new SessionNotFoundException();
        }
        session.touch();
        return session;
    }

    public void remove(String sessionId) {
        sessions.remove(sessionId);
    }

    /**
     * 동일 유저+동화의 기존 세션 제거.
     * 게임 중 뒤로 가기 후 재진입 시 기존 세션 삭제 → 새 세션으로 처음부터 재시작.
     */
    public void removeByUserAndFairytale(Long userId, Long fairytaleId) {
        sessions.values().removeIf(session ->
                session.getUserId().equals(userId) && session.getFairytaleId().equals(fairytaleId));
    }

    /** 1분 주기로 TTL(15분) 초과 세션 정리 */
    @Scheduled(fixedDelay = 60000)
    public void cleanExpiredSessions() {
        int before = sessions.size();
        sessions.values().removeIf(session -> session.isExpired(SESSION_TTL_MINUTES));
        int removed = before - sessions.size();
        if (removed > 0) {
            log.info("Cleaned {} expired game sessions", removed);
        }
    }
}
