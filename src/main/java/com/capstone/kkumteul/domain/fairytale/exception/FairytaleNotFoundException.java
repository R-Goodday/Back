package com.capstone.kkumteul.domain.fairytale.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class FairytaleNotFoundException extends BaseException {

    public FairytaleNotFoundException() {
        super(FairytaleErrorCode.FAIRYTALE_NOT_FOUND);
    }
}
