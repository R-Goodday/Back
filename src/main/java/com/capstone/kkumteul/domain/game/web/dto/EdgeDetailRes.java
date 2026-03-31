package com.capstone.kkumteul.domain.game.web.dto;

import com.capstone.kkumteul.domain.game.entity.GraphEdge;

/**
 * GET /game/edge 응답 DTO.
 * 3단계 관계도에서 선 클릭 시 관계 설명을 모달로 표시하기 위한 데이터.
 */
public record EdgeDetailRes(
        String fromWord,
        String toWord,
        String description
) {

    public static EdgeDetailRes from(GraphEdge edge) {
        return new EdgeDetailRes(
                edge.getFromNode().getWord(),
                edge.getToNode().getWord(),
                edge.getDescription()
        );
    }
}
