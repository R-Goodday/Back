package com.capstone.kkumteul.domain.auth.exception;

import com.capstone.kkumteul.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseResponseCode {

    INVALID_PASSWORD_400("INVALID_PASSWORD_400", 400, "잘못된 패스워드입니다."),
    DUPLICATED_USER_ID_409("DUPLICATED_USER_ID_409", 409, "중복된 ID입니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
