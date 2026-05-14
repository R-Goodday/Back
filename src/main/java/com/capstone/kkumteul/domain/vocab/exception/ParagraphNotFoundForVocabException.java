package com.capstone.kkumteul.domain.vocab.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class ParagraphNotFoundForVocabException extends BaseException {
    public ParagraphNotFoundForVocabException() {
        super(VocabErrorCode.PARAGRAPH_NOT_FOUND_FOR_VOCAB);
    }
}
