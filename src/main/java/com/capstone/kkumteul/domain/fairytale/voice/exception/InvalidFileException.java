package com.capstone.kkumteul.domain.fairytale.voice.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class InvalidFileException extends BaseException {
    public InvalidFileException() {
        super(VoiceErrorCode.INVALID_FILE_EXCEPTION);
    }
}
