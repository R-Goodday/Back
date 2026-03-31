package com.capstone.kkumteul.domain.game.web.dto;

import com.capstone.kkumteul.domain.game.entity.GraphEdge;
import com.capstone.kkumteul.domain.game.entity.GraphNode;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collection;
import java.util.List;

/**
 * POST /game/quiz/answer 응답 DTO.
 *
 * <ul>
 *   <li>정답 (진행중): correct=true, description=엣지 설명</li>
 *   <li>오답: correct=false, hint="틀렸어요! 다시 한번 생각해볼까요?"</li>
 *   <li>2단계 완료: correct=true, stageComplete=true, nextStage="RELATION",
 *       graph에 완성된 노드+엣지 목록 (3단계 관계도 진입 데이터)</li>
 * </ul>
 */
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

    /** 2단계 완료 — 마지막 엣지 설명 + 완성된 그래프 데이터 */
    public static QuizAnswerRes stageComplete(String description, Collection<GraphNode> nodes, List<GraphEdge> edges) {
        List<NodeWithCategoryRes> nodeResList = nodes.stream()
                .map(NodeWithCategoryRes::from)
                .toList();
        List<EdgeRes> edgeResList = edges.stream()
                .map(e -> new EdgeRes(e.getId(), e.getFromNode().getId(), e.getToNode().getId()))
                .toList();
        GraphRes graph = new GraphRes(nodeResList, edgeResList);
        return new QuizAnswerRes(true, true, description, null, "RELATION", graph);
    }

    public record GraphRes(List<NodeWithCategoryRes> nodes, List<EdgeRes> edges) {}

    public record EdgeRes(Long edgeId, Long from, Long to) {}
}
