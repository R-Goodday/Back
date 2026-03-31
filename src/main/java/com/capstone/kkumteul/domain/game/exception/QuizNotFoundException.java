package com.capstone.kkumteul.domain.game.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class QuizNotFoundException extends BaseException {

    public QuizNotFoundException() {
        super(GameErrorCode.QUIZ_NOT_FOUND);
    }
}
