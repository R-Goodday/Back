package com.line4thon.fin4u.global.exception;

import com.line4thon.fin4u.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException {
  private final BaseResponseCode baseResponseCode;
}
