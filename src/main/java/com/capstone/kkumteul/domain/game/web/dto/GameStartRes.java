package com.capstone.kkumteul.domain.game.web.dto;

import com.capstone.kkumteul.domain.game.entity.GraphNode;
import com.capstone.kkumteul.domain.game.entity.NodeCategory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * POST /game/start 응답 DTO.
 * 세션 ID, 현재 단계("CLASSIFY"), 1단계 문제 데이터(노드 목록 + 카테고리 바구니 목록)를 반환.
 *
 * <p>응답 예시:</p>
 * <pre>
 * {
 *   "sessionId": "abc123",
 *   "stage": "CLASSIFY",
 *   "question": {
 *     "nodes": [{"id": 1, "word": "토끼"}, ...],
 *     "categories": ["등장인물", "장소", "사건", "교훈"]
 *   }
 * }
 * </pre>
 */
public record GameStartRes(
        String sessionId,
        String stage,
        ClassifyQuestionRes question
) {

    public static GameStartRes of(String sessionId, Collection<GraphNode> nodes) {
        List<NodeRes> nodeResList = nodes.stream()
                .map(NodeRes::from)
                .toList();
        List<String> categories = Arrays.stream(NodeCategory.values())
                .map(NodeCategory::getLabel)
                .toList();
        return new GameStartRes(sessionId, "CLASSIFY", new ClassifyQuestionRes(nodeResList, categories));
    }

    public record ClassifyQuestionRes(List<NodeRes> nodes, List<String> categories) {}
}
