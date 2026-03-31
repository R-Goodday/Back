package com.capstone.kkumteul.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 매 HTTP 요청마다 실행되는 JWT 인증 필터
// Authorization 헤더 값 추출 -> validation -> SecurityContext에 주입
@RequiredArgsConstructor
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 순수한 JWT 토큰 추출
        String token = extractToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {

            // Authentication 객체로 추출
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // SecurityContextHolder에 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {

        // HttpServletRequest 에서 Authorization 헤더 추출
        String bearer = request.getHeader("Authorization");

        // Authorization 헤더가 유효하다면
        if (bearer != null && bearer.startsWith("Bearer ")) {
            // Bearer 필드 제거 후 반환
            return bearer.substring(7);
        } else
            return null;
    }
}
