package com.capstone.kkumteul.domain.game.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class GameNotCompletedException extends BaseException {

    public GameNotCompletedException() {
        super(GameErrorCode.GAME_NOT_COMPLETED);
    }
}
