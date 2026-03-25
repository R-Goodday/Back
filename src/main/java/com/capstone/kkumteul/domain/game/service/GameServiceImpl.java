package com.capstone.kkumteul.domain.game.service;

import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;
import com.capstone.kkumteul.domain.game.entity.GraphEdge;
import com.capstone.kkumteul.domain.game.entity.GraphNode;
import com.capstone.kkumteul.domain.game.entity.NodeCategory;
import com.capstone.kkumteul.domain.game.entity.EdgeChoice;
import com.capstone.kkumteul.domain.game.entity.GameResult;
import com.capstone.kkumteul.domain.game.exception.*;
import com.capstone.kkumteul.domain.game.repository.EdgeChoiceRepository;
import com.capstone.kkumteul.domain.game.repository.GameResultRepository;
import com.capstone.kkumteul.domain.game.repository.GraphEdgeRepository;
import com.capstone.kkumteul.domain.game.repository.GraphNodeRepository;
import com.capstone.kkumteul.domain.game.web.dto.*;
import com.capstone.kkumteul.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
     * 게임 시작 — POST /game/start
     *
     * <p>처리 흐름:</p>
     * <ol>
     *   <li>동화 존재 확인 (EntityManager.find → 없으면 404)</li>
     *   <li>game_results에서 (userId, fairytaleId) 조회 → completed=true면 409</li>
     *   <li>graph_nodes에서 fairytaleId로 그래프 존재 확인 → 없으면 404</li>
     *   <li>기존 세션 제거 (뒤로 가기 후 재진입 시 처음부터 재시작)</li>
     *   <li>노드/엣지 DB 조회 → 인메모리 세션에 캐싱</li>
     * </ol>
     */
    @Override
    public GameStartRes startGame(Long userId, Long fairytaleId) {
        // 동화 존재 확인
        Fairytale fairytale = entityManager.find(Fairytale.class, fairytaleId);
        if (fairytale == null) {
            throw new FairytaleNotFoundException();
        }

        // (userId, fairytaleId) UNIQUE 조합으로 1회 플레이 제한 확인
        gameResultRepository.findByUserIdAndFairytaleId(userId, fairytaleId)
                .ifPresent(result -> {
                    if (Boolean.TRUE.equals(result.getCompleted())) {
                        throw new GameAlreadyCompletedException();
                    }
                });

        // graph_nodes 테이블에서 fairytaleId로 그래프 존재 확인
        if (!graphNodeRepository.existsByFairytaleId(fairytaleId)) {
            throw new GraphNotFoundException();
        }

        // 기존 세션 제거 — 뒤로 가기 후 재진입 시 새 세션으로 처음부터
        sessionManager.removeByUserAndFairytale(userId, fairytaleId);

        // DB에서 노드/엣지 조회 후 세션에 캐싱 (이후 채점은 세션 데이터로 처리)
        List<GraphNode> nodes = graphNodeRepository.findByFairytaleId(fairytaleId);
        List<GraphEdge> edges = graphEdgeRepository.findByFairytaleId(fairytaleId);

        GameSession session = new GameSession(userId, fairytaleId, nodes, edges);
        sessionManager.save(session);

        return GameStartRes.of(session.getSessionId(), session.getNodes());
    }

    /**
     * 1단계 바구니 분류 — POST /game/classify
     *
     * <p>드래그할 때마다 즉시 호출. 세션 캐싱 데이터로 채점하므로 DB 조회 없음.</p>
     * <ul>
     *   <li>이미 정답 처리된 노드 재제출 → correct: true (멱등성 보장)</li>
     *   <li>카테고리 한글 라벨("등장인물") → NodeCategory.fromLabel()로 변환 후 비교</li>
     *   <li>전체 노드 분류 완료 시 stage_complete=true + 2단계 데이터(노드+카테고리+총 엣지 수) 반환</li>
     * </ul>
     */
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
            return ClassifyRes.stageComplete(session.getNodes(), session.getTotalEdges());
        }

        return ClassifyRes.correct(false);
    }

    /**
     * 2단계 퀴즈 요청 — POST /game/quiz
     *
     * <p>두 노드 사이에 선을 그을 때 호출.</p>
     * <ul>
     *   <li>양방향 매칭으로 엣지 조회 (A→B든 B→A든 동일 엣지)</li>
     *   <li>이미 정답 처리된 엣지면 409 (ALREADY_ANSWERED)</li>
     *   <li>유효하지 않은 조합이면 400 (INVALID_EDGE)</li>
     *   <li>보기 3개는 랜덤 셔플하여 반환</li>
     * </ul>
     */
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
     * 2단계 퀴즈 정답 제출 — POST /game/quiz/answer
     *
     * <p>choice_id(PK)로 정답 제출. 텍스트 매칭 대신 PK 비교로 안전 채점.</p>
     * <ul>
     *   <li>정답 시 description 반환 → 앱에서 관계 설명 모달 표시</li>
     *   <li>오답 시 힌트 반환 (재시도 제한 없음 — 유아 대상)</li>
     *   <li>모든 엣지 완료 시 game_results 자동 저장 + 완성된 그래프 반환</li>
     * </ul>
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

        if (!Boolean.TRUE.equals(selectedChoice.getIsAnswer())) {
            return QuizAnswerRes.incorrect();
        }

        // 정답 처리 — 엣지 완료 표시
        GraphEdge edge = graphEdgeRepository.findById(edgeId)
                .orElseThrow(InvalidEdgeException::new);
        session.markEdgeCompleted(edgeId);

        // 모든 엣지 완료 → 2단계 종료
        if (session.isAssembleComplete()) {
            // game_results 자동 저장 (UNIQUE 제약으로 중복 방지)
            saveGameResult(session);
            // 세션 제거
            sessionManager.remove(sessionId);
            return QuizAnswerRes.stageComplete(edge.getDescription(), session.getNodes(), session.getEdges());
        }

        return QuizAnswerRes.correct(edge.getDescription());
    }

    /**
     * 3단계 엣지 상세 조회 — GET /game/edge
     *
     * <p>관계도 화면에서 선 클릭 시 호출. edge_id로 단건 조회 후 description 반환.</p>
     * <ul>
     *   <li>edge_id → graph_edges 조회 (없으면 404)</li>
     *   <li>fromNode의 fairytale_id로 game_results 검증 → 본인 완료 데이터가 아니면 403</li>
     * </ul>
     */
    @Override
    public EdgeDetailRes getEdgeDetail(Long userId, Long edgeId) {
        GraphEdge edge = graphEdgeRepository.findById(edgeId)
                .orElseThrow(EdgeNotFoundException::new);

        // fromNode → fairytale → game_results에서 해당 유저의 완료 여부 검증
        Long fairytaleId = edge.getFromNode().getFairytale().getId();
        validateGameCompleted(userId, fairytaleId);

        return EdgeDetailRes.from(edge);
    }

    /**
     * 3단계 전체 관계도 조회 — GET /game/graph
     *
     * <p>동화 모음집에서 '관계도' 버튼 클릭 시 호출. 완성된 그래프(노드+엣지)를 반환.</p>
     * <ul>
     *   <li>game_results에서 (userId, fairytaleId) 완료 검증 → 미완료/미존재 시 404</li>
     *   <li>graph_nodes + graph_edges 조회 후 반환</li>
     * </ul>
     */
    @Override
    public GraphDetailRes getGraph(Long userId, Long fairytaleId) {
        validateGameCompleted(userId, fairytaleId);

        List<GraphNode> nodes = graphNodeRepository.findByFairytaleId(fairytaleId);
        List<GraphEdge> edges = graphEdgeRepository.findByFairytaleId(fairytaleId);

        return GraphDetailRes.of(fairytaleId, nodes, edges);
    }

    /**
     * 게임 완료 여부 검증 — 3단계 조회 API 공통.
     * game_results에서 (userId, fairytaleId) 조합으로 completed=true인지 확인.
     * 결과가 없거나 미완료면 GameNotCompletedException,
     * 다른 유저의 데이터에 접근하면 GameAccessDeniedException.
     */
    private void validateGameCompleted(Long userId, Long fairytaleId) {
        GameResult result = gameResultRepository.findByUserIdAndFairytaleId(userId, fairytaleId)
                .orElseThrow(GameNotCompletedException::new);
        if (!Boolean.TRUE.equals(result.getCompleted())) {
            throw new GameNotCompletedException();
        }
    }

    /** game_results INSERT — 2단계 완료 시 서버가 자동 저장 (앱 크래시 대비) */
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
        gameResultRepository.save(result);
    }
}
