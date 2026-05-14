package com.capstone.kkumteul.global.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.capstone.kkumteul.global.constant.StaticValue.CREATED;
import static com.capstone.kkumteul.global.constant.StaticValue.OK;

@Getter
@AllArgsConstructor
public enum SuccessResponseCode implements BaseResponseCode {

    SUCCESS_OK("SUCCESS_200", OK, "호출에 성공했습니다."),
    SUCCESS_CREATED("SUCCESS_201", CREATED, "호출에 성공했습니다."),
    SUCCESS_ACCEPTED("SUCCESS_202", 202, "요청 진행이 받아들여졌습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
