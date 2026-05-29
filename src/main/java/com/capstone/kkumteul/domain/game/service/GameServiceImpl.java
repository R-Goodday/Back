package com.capstone.kkumteul.domain.game.service;

import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;
import com.capstone.kkumteul.domain.fairytale.exception.FairytaleNotFoundException;
import com.capstone.kkumteul.domain.game.entity.*;
import com.capstone.kkumteul.domain.game.exception.*;
import com.capstone.kkumteul.domain.game.repository.EdgeChoiceRepository;
import com.capstone.kkumteul.domain.game.repository.GameResultRepository;
import com.capstone.kkumteul.domain.game.repository.GraphEdgeRepository;
import com.capstone.kkumteul.domain.game.repository.GraphNodeRepository;
import com.capstone.kkumteul.domain.game.web.dto.*;
import com.capstone.kkumteul.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameServiceImpl implements GameService {

    private final GraphNodeRepository graphNodeRepository;
    private final GraphEdgeRepository graphEdgeRepository;
    private final EdgeChoiceRepository edgeChoiceRepository;
    private final GameResultRepository gameResultRepository;
    private final GameSessionManager sessionManager;
    private final EntityManager entityManager;

    /**
     * 그래프는 Kafka consumer 가 비동기로 추출하므로, 본 메서드에서 동기 폴백 호출은 하지 않는다.
     */
    @Override
    @Transactional
    public GameStartRes startGame(Long userId, Long fairytaleId) {
        // 동화 존재 확인
        Fairytale fairytale = entityManager.find(Fairytale.class, fairytaleId);
        if (fairytale == null) {
            throw new FairytaleNotFoundException();
        }

        // (userId, fairytaleId) UNIQUE 조합으로 1회 플레이 제한 확인
        gameResultRepository.findByUserIdAndFairytaleId(userId, fairytaleId)
                .ifPresent(result -> {
                    if (result.isCompleted()) {
                        throw new GameAlreadyCompletedException();
                    }
                });

        // graph_nodes 테이블에서 fairytaleId 로 그래프 존재 확인 → 없으면 즉시 404
        if (!graphNodeRepository.existsByFairytaleId(fairytaleId)) {
            log.warn("그래프 미존재 — fairytaleId={}", fairytaleId);
            throw new GraphNotFoundException();
        }

        // 기존 세션 제거 — 뒤로 가기 후 재진입 시 새 세션으로 처음부터
        sessionManager.removeByUserAndFairytale(userId, fairytaleId);

        // DB에서 노드/엣지 조회 후 세션에 캐싱 (이후 채점은 세션 데이터로 처리)
        List<GraphNode> nodes = graphNodeRepository.findByFairytaleId(fairytaleId);
        List<GraphEdge> edges = graphEdgeRepository.findByFairytaleId(fairytaleId);

        GameSession session = new GameSession(userId, fairytaleId, nodes, edges);
        sessionManager.save(session);

        return GameStartRes.of(session.getSessionId(), fairytale, session.getNodes());
    }

    @Override
    public ClassifyRes classify(String sessionId, Long nodeId, String category) {
        // 세션 조회 + TTL 갱신 (없으면 404)
        GameSession session = sessionManager.get(sessionId);

        // 이미 정답 처리된 노드 → 멱등하게 correct 반환
        if (session.isAlreadyClassified(nodeId)) {
            return ClassifyRes.correct(session.isClassifyComplete());
        }

        // 한글 라벨 → enum 변환 후 세션 내 nodeMap에서 O(1) 채점
        NodeCategory nodeCategory = NodeCategory.fromLabel(category);
        boolean correct = session.checkClassify(nodeId, nodeCategory);

        if (!correct) {
            return ClassifyRes.incorrect();
        }

        // 모든 노드 분류 완료 → 1단계 종료, 2단계 데이터 반환
        if (session.isClassifyComplete()) {
            Fairytale fairytale = entityManager.find(Fairytale.class, session.getFairytaleId());
            return ClassifyRes.stageComplete(fairytale, session.getNodes(), session.getTotalEdges());
        }

        return ClassifyRes.correct(false);
    }

    @Override
    public QuizRes requestQuiz(String sessionId, Long fromNodeId, Long toNodeId) {
        GameSession session = sessionManager.get(sessionId);

        // 양방향 매칭으로 엣지 조회
        GraphEdge edge = graphEdgeRepository.findByNodePair(fromNodeId, toNodeId)
                .orElseThrow(InvalidEdgeException::new);

        // 이미 정답 처리된 엣지 방어 처리
        if (session.isEdgeCompleted(edge.getId())) {
            throw new AlreadyAnsweredException();
        }

        // quizId 생성 후 세션에 매핑 저장
        String quizId = session.registerQuiz(edge.getId());

        // 보기 조회
        List<EdgeChoice> choices = edgeChoiceRepository.findByEdgeId(edge.getId());

        return QuizRes.of(quizId, edge.getFromNode().getWord(), edge.getToNode().getWord(), choices);
    }

    /**
     * 텍스트 매칭이 아닌 choice_id(PK) 비교로 채점한다.
     */
    @Override
    @Transactional
    public QuizAnswerRes answerQuiz(String sessionId, String quizId, Long selectedChoiceId) {
        GameSession session = sessionManager.get(sessionId);

        // quizId로 edgeId 조회
        Long edgeId = session.getEdgeIdByQuizId(quizId);
        if (edgeId == null) {
            throw new QuizNotFoundException();
        }

        // choice_id로 정답 여부 확인
        EdgeChoice selectedChoice = edgeChoiceRepository.findById(selectedChoiceId)
                .orElseThrow(QuizNotFoundException::new);

        if (!selectedChoice.getEdge().getId().equals(edgeId) || !selectedChoice.isAnswer()) {
            return QuizAnswerRes.incorrect();
        }

        // 정답 처리 — 엣지 완료 표시 (description 은 세션 캐시에서 읽음)
        GameSession.SessionEdge edge = session.findEdge(edgeId);
        if (edge == null) {
            throw new InvalidEdgeException();
        }
        session.markEdgeCompleted(edgeId);

        // 모든 엣지 완료 → 2단계 종료
        if (session.isAssembleComplete()) {
            // game_results 자동 저장 (UNIQUE 제약으로 중복 방지)
            saveGameResult(session);
            // 세션 제거
            sessionManager.remove(sessionId);
            Fairytale fairytale = entityManager.find(Fairytale.class, session.getFairytaleId());
            return QuizAnswerRes.stageComplete(fairytale, edge.getDescription(), session.getNodes(), session.getEdges());
        }

        return QuizAnswerRes.correct(edge.getDescription());
    }

    @Override
    public EdgeDetailRes getEdgeDetail(Long userId, Long edgeId) {
        GraphEdge edge = graphEdgeRepository.findById(edgeId)
                .orElseThrow(EdgeNotFoundException::new);

        Fairytale fairytale = edge.getFromNode().getFairytale();
        validateOwnedAndCompleted(userId, fairytale);

        return EdgeDetailRes.from(edge);
    }

    @Override
    public GraphDetailRes getGraph(Long userId, Long fairytaleId) {
        validateGraphCompleted(userId, fairytaleId);

        Fairytale fairytale = entityManager.find(Fairytale.class, fairytaleId);
        if (fairytale == null) {
            throw new FairytaleNotFoundException();
        }

        List<GraphNode> nodes = graphNodeRepository.findByFairytaleId(fairytaleId);
        List<GraphEdge> edges = graphEdgeRepository.findByFairytaleId(fairytaleId);

        return GraphDetailRes.of(fairytale, nodes, edges);
    }

    /**
     * 게임 완료 여부 조회 — GET /api/game/status
     *
     * <p>앱 진입 시 "동화 해설" 버튼 분기에 사용. fairytale 미존재만 404, 그 외에는 completed boolean 으로 반환한다.</p>
     */
    @Override
    public GameStatusRes getStatus(Long userId, Long fairytaleId) {
        Fairytale fairytale = entityManager.find(Fairytale.class, fairytaleId);
        if (fairytale == null) {
            throw new FairytaleNotFoundException();
        }

        boolean completed = gameResultRepository.findByUserIdAndFairytaleId(userId, fairytaleId)
                .map(GameResult::isCompleted)
                .orElse(false);

        return GameStatusRes.of(fairytaleId, completed);
    }

    /**
     * 본인 동화 + 게임 완료 여부 검증 — GET /game/edge 전용.
     * <p>① 동화 소유권: fairytale.user.id != userId 면 {@link GameForbiddenException} (403).
     * ② 완료 여부: game_results.completed != true 면 {@link GameNotCompletedException} (404).</p>
     */
    private void validateOwnedAndCompleted(Long userId, Fairytale fairytale) {

        GameResult result = gameResultRepository.findByUserIdAndFairytaleId(userId, fairytale.getId())
                .orElseThrow(GameNotCompletedException::new);
        if (!result.isCompleted()) {
            throw new GameNotCompletedException();
        }
    }

    /**
     * 완성된 그래프 조회 자격 검증 — GET /game/graph 전용.
     * <p>game_results.completed != true (또는 row 없음) 인 경우를 모두 {@link GraphNotFoundException} (404 GRAPH_NOT_FOUND) 로 통일.</p>
     */
    private void validateGraphCompleted(Long userId, Long fairytaleId) {
        boolean completed = gameResultRepository.findByUserIdAndFairytaleId(userId, fairytaleId)
                .map(GameResult::isCompleted)
                .orElse(false);
        if (!completed) {
            throw new GraphNotFoundException();
        }
    }

    /**
     * game_results INSERT — 2단계 완료 시 서버가 자동 저장 (앱 크래시 대비).
     *
     * <p>(userId, fairytaleId) UNIQUE 제약이 걸려 있어 동시 INSERT 시 한쪽은 {@link DataIntegrityViolationException} 을 던진다.
     * race 패자는 INFO 로 흡수하고 정상 흐름으로 반환한다.</p>
     */
    private void saveGameResult(GameSession session) {
        if (gameResultRepository.existsByUserIdAndFairytaleId(session.getUserId(), session.getFairytaleId())) {
            return;
        }
        User user = entityManager.find(User.class, session.getUserId());
        Fairytale fairytale = entityManager.find(Fairytale.class, session.getFairytaleId());
        GameResult result = GameResult.builder()
                .user(user)
                .fairytale(fairytale)
                .completed(true)
                .build();
        try {
            gameResultRepository.save(result);
        } catch (DataIntegrityViolationException e) {
            log.info("game result race 흡수 userId={}, fairytaleId={}",
                    session.getUserId(), session.getFairytaleId());
        }
    }
}
