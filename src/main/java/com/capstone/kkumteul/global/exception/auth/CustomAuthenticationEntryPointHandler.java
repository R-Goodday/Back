package com.capstone.kkumteul.global.exception.auth;

import com.capstone.kkumteul.global.response.BaseResponse;
import com.capstone.kkumteul.global.response.code.ErrorResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * JWT 401 - Unauthorized
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        BaseResponse errorResponse = BaseResponse.of(ErrorResponseCode.UNAUTHORIZED_ERROR);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
