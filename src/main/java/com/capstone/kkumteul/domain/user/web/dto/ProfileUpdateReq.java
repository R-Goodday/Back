package com.capstone.kkumteul.domain.user.web.dto;

import com.capstone.kkumteul.domain.user.entity.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ProfileUpdateReq {

    @NotNull(message = "gender는 필수입니다")
    private Gender gender;

    @NotNull(message = "age는 필수입니다")
    @Min(value = 1, message = "나이는 1 이상이어야 합니다")
    @Max(value = 10, message = "나이는 10 이하여야 합니다")
    private Integer age;
}
