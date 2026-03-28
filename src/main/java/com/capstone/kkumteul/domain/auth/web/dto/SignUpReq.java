package com.capstone.kkumteul.domain.auth.web.dto;

import com.capstone.kkumteul.domain.user.entity.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpReq {

    @NotBlank(message = "ID 필드는 비어있을 수 없습니다.")
    private final String userId;

    @NotBlank(message = "비밀번호 필드는 비어있을 수 없습니다.")
    private final String password;

    @NotBlank(message = "닉네임 필드는 비어있을 수 없습니다.")
    private final String nickname;

    @NotNull(message = "나이 필드는 비어있을 수 없습니다.")
    private final int age;

    @NotBlank(message = "성별 필드는 비어있을 수 없습니다.")
    private final Gender gender;
}
