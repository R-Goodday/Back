package com.capstone.kkumteul.domain.fairytale.voice.exception;

import com.capstone.kkumteul.global.exception.BaseException;

public class FileUploadFailException extends BaseException {
    public FileUploadFailException() {
        super(VoiceErrorCode.FILE_UPLOAD_FAIL);
    }
}
