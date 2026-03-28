package com.capstone.kkumteul.domain.auth.web.controller;

import com.capstone.kkumteul.domain.auth.service.AuthService;
import com.capstone.kkumteul.domain.auth.web.dto.SignUpReq;
import com.capstone.kkumteul.global.response.SuccessResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<SuccessResponse<?>> signUp(
            // 회원가입 요청 DTO
            @Valid @RequestBody SignUpReq req,
            // Authorization 필드 접근용 응답 객체
            HttpServletResponse response
    ) {

        // 회원가입 비즈니스 메소드 호출
        String result = authService.saveUser(req);

        // Authorization 필드에 Bearer 토큰 삽입
        response.addHeader("Authorization", "Bearer " + result);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponse.created(null));
    }
}
