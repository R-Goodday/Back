package com.capstone.kkumteul.domain.game.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class GraphExtractFailedException extends BaseException {

    public GraphExtractFailedException() {
        super(GameErrorCode.GRAPH_EXTRACT_FAILED);
    }
}
