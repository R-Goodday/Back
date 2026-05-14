package com.capstone.kkumteul.domain.fairytale.exception;

import com.capstone.kkumteul.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.capstone.kkumteul.global.constant.StaticValue.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum FairytaleErrorCode implements BaseResponseCode {

    FAIRYTALE_NOT_FOUND("FAIRYTALE_404_1", NOT_FOUND, "동화를 찾을 수 없습니다."),
    PARAGRAPH_NOT_FOUND("FAIRYTALE_404_2", NOT_FOUND, "요청한 ID에 해당하는 Paragraph를 찾을 수 없습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
