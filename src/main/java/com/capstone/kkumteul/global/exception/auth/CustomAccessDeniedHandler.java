package com.capstone.kkumteul.global.exception.auth;

import com.capstone.kkumteul.global.response.ErrorResponse;
import com.capstone.kkumteul.global.response.code.ErrorResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * JWT 403 - Forbidden
 */
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // 403 - Forbidden 허용되지 않은 리소스 접근으로 status 설정
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        // ResponseBody - 403 Forbidden 으로 설정
        ErrorResponse<?> errorResponse = ErrorResponse.from(ErrorResponseCode.ACCESS_DENIED_REQUEST);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
