package com.capstone.kkumteul.domain.game.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class GameAlreadyCompletedException extends BaseException {

    public GameAlreadyCompletedException() {
        super(GameErrorCode.GAME_ALREADY_COMPLETED);
    }
}
