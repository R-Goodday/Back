package com.capstone.kkumteul.domain.vocab.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class VocabForbiddenException extends BaseException {
    public VocabForbiddenException() {
        super(VocabErrorCode.VOCAB_FORBIDDEN);
    }
}
