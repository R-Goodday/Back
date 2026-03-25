package com.capstone.kkumteul.domain.game.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class GraphNotFoundException extends BaseException {

    public GraphNotFoundException() {
        super(GameErrorCode.GRAPH_NOT_FOUND);
    }
}
