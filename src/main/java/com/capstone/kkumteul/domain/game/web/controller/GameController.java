package com.capstone.kkumteul.domain.game.web.controller;

import com.capstone.kkumteul.domain.game.service.GameService;
import com.capstone.kkumteul.domain.game.web.dto.*;
import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.global.response.SuccessResponse;
import com.capstone.kkumteul.global.security.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;

    @PostMapping("/start")
    public ResponseEntity<SuccessResponse<GameStartRes>> startGame(
            @AuthUser User user,
            @Valid @RequestBody GameStartReq req
    ) {
        GameStartRes res = gameService.startGame(user.getId(), req.getFairytaleId());
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

    /** 3단계 — 관계도에서 선 클릭 시 관계 설명 조회 */
    @GetMapping("/edge")
    public ResponseEntity<SuccessResponse<EdgeDetailRes>> getEdgeDetail(
            @AuthUser User user,
            @RequestParam("edge_id") Long edgeId
    ) {
        EdgeDetailRes res = gameService.getEdgeDetail(user.getId(), edgeId);
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.ok(res));
    }

    /** 3단계 — 동화 모음집에서 완성된 관계도 전체 조회 */
    @GetMapping("/graph")
    public ResponseEntity<SuccessResponse<GraphDetailRes>> getGraph(
            @AuthUser User user,
            @RequestParam("fairytale_id") Long fairytaleId
    ) {
        GraphDetailRes res = gameService.getGraph(user.getId(), fairytaleId);
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.ok(res));
    }
}
