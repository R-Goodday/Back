package com.capstone.kkumteul.domain.game.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class InvalidEdgeException extends BaseException {

    public InvalidEdgeException() {
        super(GameErrorCode.INVALID_EDGE);
    }
}
