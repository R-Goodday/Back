package com.capstone.kkumteul.domain.game.web.controller;

import com.capstone.kkumteul.domain.game.service.GameService;
import com.capstone.kkumteul.domain.game.web.dto.*;
import com.capstone.kkumteul.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/game")
public class GameController {

    private final GameService gameService;

    // TODO: JWT 인증 도입 시 @AuthenticationPrincipal로 교체
    private static final Long TEMP_USER_ID = 1L;

    @PostMapping("/start")
    public ResponseEntity<SuccessResponse<GameStartRes>> startGame(
            @Valid @RequestBody GameStartReq req
    ) {
        GameStartRes res = gameService.startGame(TEMP_USER_ID, req.getFairytaleId());
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.ok(res));
    }

    @PostMapping("/classify")
    public ResponseEntity<SuccessResponse<ClassifyRes>> classify(
            @Valid @RequestBody ClassifyReq req
    ) {
        ClassifyRes res = gameService.classify(req.getSessionId(), req.getNodeId(), req.getCategory());
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.ok(res));
    }

    /** 2단계 — 두 노드 사이 선을 그으면 퀴즈 반환 */
    @PostMapping("/quiz")
    public ResponseEntity<SuccessResponse<QuizRes>> requestQuiz(
            @Valid @RequestBody QuizReq req
    ) {
        QuizRes res = gameService.requestQuiz(req.getSessionId(), req.getFromNodeId(), req.getToNodeId());
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.ok(res));
    }

    /** 2단계 — 퀴즈 정답 제출 (choice_id PK 기반 채점) */
    @PostMapping("/quiz/answer")
    public ResponseEntity<SuccessResponse<QuizAnswerRes>> answerQuiz(
            @Valid @RequestBody QuizAnswerReq req
    ) {
        QuizAnswerRes res = gameService.answerQuiz(req.getSessionId(), req.getQuizId(), req.getSelectedChoiceId());
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.ok(res));
    }
}
