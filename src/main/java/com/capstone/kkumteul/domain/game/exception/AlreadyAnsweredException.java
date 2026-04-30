package com.capstone.kkumteul.domain.game.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class AlreadyAnsweredException extends BaseException {

    public AlreadyAnsweredException() {
        super(GameErrorCode.ALREADY_ANSWERED);
    }
}
