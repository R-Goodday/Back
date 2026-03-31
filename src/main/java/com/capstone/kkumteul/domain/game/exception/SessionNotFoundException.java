package com.capstone.kkumteul.domain.game.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class SessionNotFoundException extends BaseException {

    public SessionNotFoundException() {
        super(GameErrorCode.SESSION_NOT_FOUND);
    }
}
