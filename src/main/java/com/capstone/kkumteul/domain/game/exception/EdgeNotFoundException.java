package com.capstone.kkumteul.domain.game.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class EdgeNotFoundException extends BaseException {

    public EdgeNotFoundException() {
        super(GameErrorCode.EDGE_NOT_FOUND);
    }
}
