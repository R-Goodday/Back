package com.capstone.kkumteul.domain.game.service;

import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;
import com.capstone.kkumteul.domain.game.entity.GraphEdge;
import com.capstone.kkumteul.domain.game.entity.GraphNode;
import com.capstone.kkumteul.domain.game.entity.NodeCategory;
import com.capstone.kkumteul.domain.game.exception.FairytaleNotFoundException;
import com.capstone.kkumteul.domain.game.exception.GameAlreadyCompletedException;
import com.capstone.kkumteul.domain.game.exception.GraphNotFoundException;
import com.capstone.kkumteul.domain.game.repository.GameResultRepository;
import com.capstone.kkumteul.domain.game.repository.GraphEdgeRepository;
import com.capstone.kkumteul.domain.game.repository.GraphNodeRepository;
import com.capstone.kkumteul.domain.game.web.dto.ClassifyRes;
import com.capstone.kkumteul.domain.game.web.dto.GameStartRes;
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
}
