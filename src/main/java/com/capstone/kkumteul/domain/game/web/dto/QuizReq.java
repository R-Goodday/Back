package com.capstone.kkumteul.domain.game.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * POST /game/quiz 요청 DTO.
 * 앱에서 두 노드 사이에 선을 그을 때 호출.
 * 양방향 매칭이므로 from/to 순서는 무관.
 */
@Getter
public class QuizReq {

    @NotNull(message = "session_id는 필수입니다.")
    private String sessionId;

    @NotNull(message = "from_node_id는 필수입니다.")
    private Long fromNodeId;

    @NotNull(message = "to_node_id는 필수입니다.")
    private Long toNodeId;
}
