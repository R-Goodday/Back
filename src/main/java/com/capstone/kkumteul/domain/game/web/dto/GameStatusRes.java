package com.capstone.kkumteul.domain.game.web.dto;

/**
 * GET /api/game/status 응답 DTO.
 * 앱이 동화 모음집에서 "동화 해설" 진입 시 게임 시작 / 관계도 조회 분기에 사용한다.
 */
public record GameStatusRes(
        Long fairytaleId,
        boolean completed
) {

    public static GameStatusRes of(Long fairytaleId, boolean completed) {
        return new GameStatusRes(fairytaleId, completed);
    }
}
