package com.capstone.kkumteul.global.response.code;

/* 응답 코드 인터페이스 */
public interface BaseResponseCode {

    String getCode();

    int getHttpStatus();

    String getMessage();
}
