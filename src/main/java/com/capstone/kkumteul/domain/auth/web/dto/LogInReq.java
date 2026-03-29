package com.capstone.kkumteul.domain.auth.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LogInReq {

    @NotBlank(message = "ID 필드는 비어있을 수 없습니다.")
    private final String userId;

    @NotBlank(message = "비밀번호 필드는 비어있을 수 없습니다.")
    private final String password;
}
