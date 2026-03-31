package com.capstone.kkumteul.domain.game.service;

import com.capstone.kkumteul.domain.game.entity.GraphEdge;
import com.capstone.kkumteul.domain.game.entity.GraphNode;
import com.capstone.kkumteul.domain.game.entity.NodeCategory;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 게임 세션 — 서버 메모리에만 존재하며 DB에 저장하지 않음.
 *
 * <p>게임 시작(POST /game/start) 시 생성되고, 15분 미활동 시 만료된다.
 * 노드/엣지 정보를 캐싱하여 매 요청마다 DB를 조회하지 않도록 한다.</p>
 *
 * <ul>
 *   <li>nodeMap: nodeId → GraphNode 매핑. 1단계 분류 채점 시 O(1) 조회용</li>
 *   <li>classifiedNodeIds: 1단계에서 정답 처리된 노드 ID Set (중복 카운트 방지)</li>
 *   <li>completedEdgeIds: 2단계에서 정답 처리된 엣지 ID Set</li>
 * </ul>
 */
@Getter
public class GameSession {

    private final String sessionId;
    private final Long userId;
    private final Long fairytaleId;
    /** nodeId → GraphNode 매핑. 1단계 분류 채점 시 O(1) 조회 */
    private final Map<Long, GraphNode> nodeMap;
    private final List<GraphEdge> edges;
    /** 1단계에서 정답 처리된 노드 ID (중복 카운트 방지) */
    private final Set<Long> classifiedNodeIds;
    /** 2단계에서 정답 처리된 엣지 ID */
    private final Set<Long> completedEdgeIds;
    /** quizId → edgeId 매핑. 퀴즈 요청 시 생성, 정답 제출 시 조회 */
    private final Map<String, Long> quizEdgeMap;
    /** 마지막 활동 시간 — TTL 판단 기준 */
    private LocalDateTime lastActivity;

    public GameSession(Long userId, Long fairytaleId, List<GraphNode> nodes, List<GraphEdge> edges) {
        this.sessionId = UUID.randomUUID().toString();
        this.userId = userId;
        this.fairytaleId = fairytaleId;
        this.nodeMap = new LinkedHashMap<>();
        for (GraphNode node : nodes) {
            this.nodeMap.put(node.getId(), node);
        }
        this.edges = edges;
        this.classifiedNodeIds = new HashSet<>();
        this.completedEdgeIds = new HashSet<>();
        this.quizEdgeMap = new HashMap<>();
        this.lastActivity = LocalDateTime.now();
    }

    /** 세션 활동 시간 갱신 — 매 API 호출 시 TTL 리셋 */
    public void touch() {
        this.lastActivity = LocalDateTime.now();
    }

    /** lastActivity + ttlMinutes가 현재 시각보다 이전이면 만료 */
    public boolean isExpired(long ttlMinutes) {
        return lastActivity.plusMinutes(ttlMinutes).isBefore(LocalDateTime.now());
    }

    /**
     * 1단계 바구니 분류 채점.
     * nodeMap에서 nodeId로 O(1) 조회 후 카테고리 일치 여부 확인.
     * 정답이면 classifiedNodeIds에 추가하여 중복 카운트 방지.
     */
    public boolean checkClassify(Long nodeId, NodeCategory category) {
        GraphNode node = nodeMap.get(nodeId);
        if (node == null) {
            return false;
        }
        if (node.getCategory() == category) {
            classifiedNodeIds.add(nodeId);
            return true;
        }
        return false;
    }

    /** 이미 정답 처리된 노드인지 확인 (재제출 시 correct: true 반환용) */
    public boolean isAlreadyClassified(Long nodeId) {
        return classifiedNodeIds.contains(nodeId);
    }

    /** 모든 노드가 정답 처리되었으면 1단계 완료 */
    public boolean isClassifyComplete() {
        return classifiedNodeIds.size() >= nodeMap.size();
    }

    public void markEdgeCompleted(Long edgeId) {
        completedEdgeIds.add(edgeId);
    }

    /** 이미 정답 처리된 엣지인지 확인 */
    public boolean isEdgeCompleted(Long edgeId) {
        return completedEdgeIds.contains(edgeId);
    }

    /** 모든 엣지가 정답 처리되었으면 2단계 완료 */
    public boolean isAssembleComplete() {
        return completedEdgeIds.size() >= edges.size();
    }

    /**
     * 퀴즈 생성 시 quizId → edgeId 매핑 저장.
     * 정답 제출 시 quizId로 어떤 엣지에 대한 퀴즈인지 조회.
     */
    public String registerQuiz(Long edgeId) {
        String quizId = UUID.randomUUID().toString().substring(0, 8);
        quizEdgeMap.put(quizId, edgeId);
        return quizId;
    }

    /** quizId로 매핑된 edgeId 조회 */
    public Long getEdgeIdByQuizId(String quizId) {
        return quizEdgeMap.get(quizId);
    }

    public int getTotalEdges() {
        return edges.size();
    }

    public Collection<GraphNode> getNodes() {
        return nodeMap.values();
    }
}
