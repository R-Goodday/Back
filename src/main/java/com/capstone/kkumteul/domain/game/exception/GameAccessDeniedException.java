package com.capstone.kkumteul.domain.game.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class GameAccessDeniedException extends BaseException {

    public GameAccessDeniedException() {
        super(GameErrorCode.GAME_ACCESS_DENIED);
    }
}
