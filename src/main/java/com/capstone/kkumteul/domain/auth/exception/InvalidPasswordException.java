package com.capstone.kkumteul.domain.auth.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class InvalidPasswordException extends BaseException {
    public InvalidPasswordException() {
        super(AuthErrorCode.INVALID_PASSWORD_400);
    }
}
