package com.capstone.kkumteul.domain.auth.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class DuplicatedIdException extends BaseException {
    public DuplicatedIdException() {
        super(AuthErrorCode.DUPLICATED_USER_ID_409);
    }
}
