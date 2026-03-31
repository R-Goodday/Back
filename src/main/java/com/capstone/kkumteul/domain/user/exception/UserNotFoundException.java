package com.capstone.kkumteul.domain.user.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }
}
