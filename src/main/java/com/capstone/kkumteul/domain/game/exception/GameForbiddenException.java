package com.capstone.kkumteul.domain.game.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class GameForbiddenException extends BaseException {

    public GameForbiddenException() {
        super(GameErrorCode.GAME_FORBIDDEN);
    }
}
