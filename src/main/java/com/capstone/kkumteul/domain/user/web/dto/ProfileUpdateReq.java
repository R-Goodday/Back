package com.capstone.kkumteul.domain.user.web.dto;

import com.capstone.kkumteul.domain.user.entity.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ProfileUpdateReq {

    @NotNull(message = "gender는 필수입니다")
    private Gender gender;

    @NotNull(message = "age는 필수입니다")
    private Integer age;
}
