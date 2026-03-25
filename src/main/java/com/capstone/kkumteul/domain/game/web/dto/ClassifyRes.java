package com.capstone.kkumteul.domain.game.web.dto;

import com.capstone.kkumteul.domain.game.entity.GraphNode;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collection;
import java.util.List;

/**
 * POST /game/classify 응답 DTO.
 * 상황별로 다른 필드가 채워지며, null 필드는 JSON에서 제외(@JsonInclude).
 *
 * <ul>
 *   <li>정답 (진행중): correct=true, stageComplete=false</li>
 *   <li>오답: correct=false, hint="다시 시도해보세요!"</li>
 *   <li>1단계 완료: correct=true, stageComplete=true, nextStage="ASSEMBLE",
 *       question에 카테고리 포함된 노드 목록 + 총 엣지 수 (2단계 진입 데이터)</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClassifyRes(
        boolean correct,
        boolean stageComplete,
        String hint,
        String nextStage,
        AssembleQuestionRes question
) {

    /** 정답 — stageComplete 여부만 전달 */
    public static ClassifyRes correct(boolean stageComplete) {
        return new ClassifyRes(true, stageComplete, null, null, null);
    }

    /** 오답 — 힌트 메시지 포함 */
    public static ClassifyRes incorrect() {
        return new ClassifyRes(false, false, "다시 시도해보세요!", null, null);
    }

    /** 1단계 완료 — 2단계 진입 데이터(카테고리 포함 노드 + 총 엣지 수) 포함 */
    public static ClassifyRes stageComplete(Collection<GraphNode> nodes, int totalEdges) {
        List<NodeWithCategoryRes> nodeResList = nodes.stream()
                .map(NodeWithCategoryRes::from)
                .toList();
        AssembleQuestionRes question = new AssembleQuestionRes(nodeResList, totalEdges);
        return new ClassifyRes(true, true, null, "ASSEMBLE", question);
    }

    public record AssembleQuestionRes(List<NodeWithCategoryRes> nodes, int totalEdges) {}
}
