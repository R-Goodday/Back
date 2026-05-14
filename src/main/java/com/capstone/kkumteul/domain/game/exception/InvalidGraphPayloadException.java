package com.capstone.kkumteul.domain.game.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class InvalidGraphPayloadException extends BaseException {

    public InvalidGraphPayloadException() {
        super(GameErrorCode.INVALID_GRAPH_PAYLOAD);
    }
}
