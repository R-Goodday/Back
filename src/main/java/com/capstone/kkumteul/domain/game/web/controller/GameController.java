package com.capstone.kkumteul.domain.game.web.controller;

import com.capstone.kkumteul.domain.game.service.GameService;
import com.capstone.kkumteul.domain.game.web.dto.ClassifyReq;
import com.capstone.kkumteul.domain.game.web.dto.ClassifyRes;
import com.capstone.kkumteul.domain.game.web.dto.GameStartReq;
import com.capstone.kkumteul.domain.game.web.dto.GameStartRes;
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
}
