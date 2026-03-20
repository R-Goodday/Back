package com.line4thon.fin4u.global.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.line4thon.fin4u.global.constant.StaticValue.*;

@Getter
@AllArgsConstructor
public enum SuccessResponseCode implements BaseResponseCode {

    SUCCESS_OK("SUCCESS_200", OK, "호출에 성공했습니다."),
    SUCCESS_CREATED("SUCCESS_201", CREATED, "호출에 성공했습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
