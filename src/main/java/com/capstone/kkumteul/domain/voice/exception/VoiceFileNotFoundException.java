package com.capstone.kkumteul.domain.voice.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class VoiceFileNotFoundException extends BaseException {
    public VoiceFileNotFoundException() {
        super(VoiceErrorCode.FILE_NOT_CREATED);
    }
}
