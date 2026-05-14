package com.capstone.kkumteul.domain.fairytale.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class ParagraphNotFoundException extends BaseException {
    public ParagraphNotFoundException() {
        super(FairytaleErrorCode.PARAGRAPH_NOT_FOUND);
    }
}
