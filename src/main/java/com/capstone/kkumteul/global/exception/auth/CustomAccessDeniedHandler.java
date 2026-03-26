package com.capstone.kkumteul.global.exception.auth;

import com.capstone.kkumteul.global.response.BaseResponse;
import com.capstone.kkumteul.global.response.code.ErrorResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

// 인증된 사용자가 인가되지 않은 요청 시 발생하는 예외
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // 403 - Forbidden 허용되지 않은 리소스 접근으로 status 설정
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        // ResponseBody - 403 Forbidden 으로 설정
        BaseResponse errorResponse = BaseResponse.of(ErrorResponseCode.ACCESS_DENIED_REQUEST);

        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
