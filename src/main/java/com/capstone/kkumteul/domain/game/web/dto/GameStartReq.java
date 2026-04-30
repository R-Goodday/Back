package com.capstone.kkumteul.domain.game.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * POST /game/start 요청 DTO.
 * 앱에서 게임을 시작할 동화의 ID를 전달받는다.
 */
@Getter
public class GameStartReq {

    @NotNull(message = "fairytaleId는 필수입니다.")
    private Long fairytaleId;
}
