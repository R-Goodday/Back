package com.capstone.kkumteul.domain.fairytale.voice.exception;

import com.capstone.kkumteul.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VoiceErrorCode implements BaseResponseCode {

    INVALID_FILE_EXCEPTION("INVALID_FILE_400", 400, "잘못된 파일"),
    FILE_UPLOAD_FAIL("FILE_UPLOAD_FAIL_500", 500, "파일을 변환하고, S3에 업로드하는 것에 실패했습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
