package com.capstone.kkumteul.domain.game.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class InvalidCategoryException extends BaseException {

    public InvalidCategoryException() {
        super(GameErrorCode.INVALID_CATEGORY);
    }
}
