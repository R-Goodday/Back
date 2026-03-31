package com.capstone.kkumteul.domain.game.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class FairytaleNotFoundException extends BaseException {

    public FairytaleNotFoundException() {
        super(GameErrorCode.FAIRYTALE_NOT_FOUND);
    }
}
