package com.line4thon.fin4u.global.response.code;

/* 응답 코드 인터페이스 */
public interface BaseResponseCode {

    String getCode();
    int getHttpStatus();
    String getMessage();
}
