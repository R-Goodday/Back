package com.capstone.kkumteul.domain.game.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * POST /game/quiz/answer 요청 DTO.
 * 퀴즈 보기 선택 후 choice_id(PK)로 정답 제출.
 * 텍스트 매칭 대신 PK 비교로 인코딩 오류 방지.
 */
@Getter
public class QuizAnswerReq {

    @NotNull(message = "session_id는 필수입니다.")
    private String sessionId;

    @NotNull(message = "quiz_id는 필수입니다.")
    private String quizId;

    @NotNull(message = "selected_choice_id는 필수입니다.")
    private Long selectedChoiceId;
}
