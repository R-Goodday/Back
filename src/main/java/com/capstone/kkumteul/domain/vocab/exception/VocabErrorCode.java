package com.capstone.kkumteul.domain.vocab.exception;

import com.capstone.kkumteul.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.capstone.kkumteul.global.constant.StaticValue.INTERNAL_SERVER_ERROR;
import static com.capstone.kkumteul.global.constant.StaticValue.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum VocabErrorCode implements BaseResponseCode {

    PARAGRAPH_NOT_FOUND_FOR_VOCAB("VOCAB_404_1", NOT_FOUND, "해당 페이지의 본문을 찾을 수 없습니다."),
    VOCAB_EXTRACT_FAILED("VOCAB_500_1", INTERNAL_SERVER_ERROR, "단어장 추출에 실패했습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
