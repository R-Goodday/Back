package com.capstone.kkumteul.domain.fairytale.voice.exception;

import com.capstone.kkumteul.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VoiceErrorCode implements BaseResponseCode {

    INVALID_FILE_EXCEPTION("INVALID_FILE_400", 400, "잘못된 파일");

    private final String code;
    private final int httpStatus;
    private final String message;
}
