package com.capstone.kkumteul.domain.game.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * POST /game/classify 요청 DTO.
 * 앱에서 노드를 바구니에 드래그(드롭)할 때마다 호출.
 * sessionId로 세션을 식별하고, nodeId + category 조합으로 정답 여부를 판단한다.
 */
@Getter
public class ClassifyReq {

    @NotNull(message = "session_id는 필수입니다.")
    private String sessionId;

    @NotNull(message = "node_id는 필수입니다.")
    private Long nodeId;

    @NotNull(message = "category는 필수입니다.")
    private String category;
}
