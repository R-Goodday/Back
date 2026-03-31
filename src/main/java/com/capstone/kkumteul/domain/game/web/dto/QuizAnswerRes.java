package com.capstone.kkumteul.domain.game.web.dto;

import com.capstone.kkumteul.domain.game.service.GameSession.SessionEdge;
import com.capstone.kkumteul.domain.game.service.GameSession.SessionNode;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collection;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record QuizAnswerRes(
        boolean correct,
        boolean stageComplete,
        String description,
        String hint,
        String nextStage,
        GraphRes graph
) {

    /** 정답 — 엣지 설명 포함 */
    public static QuizAnswerRes correct(String description) {
        return new QuizAnswerRes(true, false, description, null, null, null);
    }

    /** 오답 — 힌트 메시지 포함 */
    public static QuizAnswerRes incorrect() {
        return new QuizAnswerRes(false, false, null, "틀렸어요! 다시 한번 생각해볼까요?", null, null);
    }

    public static QuizAnswerRes stageComplete(String description, Collection<SessionNode> nodes, List<SessionEdge> edges) {
        List<NodeWithCategoryRes> nodeResList = nodes.stream()
                .map(NodeWithCategoryRes::from)
                .toList();
        List<EdgeRes> edgeResList = edges.stream()
                .map(e -> new EdgeRes(e.getId(), e.getFromNodeId(), e.getToNodeId()))
                .toList();
        GraphRes graph = new GraphRes(nodeResList, edgeResList);
        return new QuizAnswerRes(true, true, description, null, "RELATION", graph);
    }

    public record GraphRes(List<NodeWithCategoryRes> nodes, List<EdgeRes> edges) {}

    public record EdgeRes(Long edgeId, Long from, Long to) {}
}
