package com.capstone.kkumteul.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * dev 프로필 한정 — {@code /internal/**} 경로를 인증 없이 통과시키는 SecurityFilterChain.
 *
 * <p>운영(prod) 프로필에선 이 빈이 등록되지 않으므로
 * 기존 {@link SecurityConfig}의 {@code .anyRequest().authenticated()}가 그대로 적용된다.</p>
 *
 * <p>{@link com.capstone.kkumteul.global.jwt.JwtTokenFilter}는 {@code @Component}로
 * 모든 요청에 적용되지만, 토큰이 없으면 SecurityContext에 인증 객체를 설정하지 않으므로
 * permitAll된 {@code /internal/**}은 정상 통과된다.</p>
 */
@Configuration
@Profile("dev")
public class InternalApiSecurityConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain internalApiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/internal/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
