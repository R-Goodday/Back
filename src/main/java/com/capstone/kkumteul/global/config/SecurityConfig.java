package com.capstone.kkumteul.global.config;

import com.capstone.kkumteul.global.exception.auth.CustomAccessDeniedHandler;
import com.capstone.kkumteul.global.exception.auth.CustomAuthenticationEntryPointHandler;
import com.capstone.kkumteul.global.jwt.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {

        // 비밀번호 암호화 알고리즘을 Map 객체로 담기 위해 선언
        Map<String, PasswordEncoder> encoders = new HashMap<>();

        // Bcrypt 암호화 알고리즘 선언 및 Map 객체에 저장
        PasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        encoders.put("bcrypt", bCryptPasswordEncoder);

        // DelegatingPasswordEncoder의 기본 암호화 알고리즘을 Bcrypt로 지정
        DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder("bcrypt", encoders);

        // 이전에 사용하던 암호화 알고리즘을 아래 매개변수에 전달하고, 위의 기본 암호화 알고리즘을 변경하면,
        // 정책이 바뀌어도 DelegatingPasswordEncoder가 감지하여 변경해줌!

        // delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches({이전에 사용하던 암호화 알고리즘});

        return delegatingPasswordEncoder;
    }

    // Spring Security 내부에서 인증 로직을 실행할 때 사용하는 핵심 객체
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // 필터 체인 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // JWT 기반 API 서버에서는 stateless를 지향하기 때문에, 세션 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // API path 마다 역할 기반 인가 정책 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/*").permitAll()
                        .anyRequest().authenticated()   // 필터링되지 않은 모든 URL에 대해서 인증 강제
                )

                // Spring Security 기본 로그인 필터보다 Jwt 토큰 필터를 먼저 실행하도록 설정
                // Authorization 에서 Bearer 토큰을 추출, 인증 및 SecurityContext에 토큰 정보를 저장
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(e -> e
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPointHandler)
                );

        return http.build();
    }
}
