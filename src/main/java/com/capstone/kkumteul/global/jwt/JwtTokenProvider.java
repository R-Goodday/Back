package com.capstone.kkumteul.global.jwt;

import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.domain.user.exception.UserNotFoundException;
import com.capstone.kkumteul.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

// JWT 토큰 생성, 검증, 추출
@Component
public class JwtTokenProvider {

    private final UserRepository userRepository;
    private final String secretKey;

    private final long expiration;
    private Key key;

    public JwtTokenProvider(
            UserRepository userRepository,
            @Value("${jwt.secretKey}") String secretKey,
            @Value("${jwt.exprition}") long expiration
    ) {
        this.userRepository = userRepository;
        this.secretKey = secretKey;
        this.expiration = expiration; // 3 days
    }

    // signature -> Byte 배열 -> hmac sha 이용하여 Key 객체 할당
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(User user) {
        // 현재 시각 기반으로 만료일 설정
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);


        return Jwts.builder()
                // subject : User의 고유 식별 필드
                .subject(String.valueOf(user.getUserId()))

                // claim : User에서 자주 쓰이는 필드(DB 접근 최소화)
                .claim("userId", user.getUserId())
//                .claim("role", user.getRole()) FIXME 추후 Role 열거형 선언 시 수정 요망

                // issuedAt : 토큰 발행 시간
                .issuedAt(now)

                // expiration : 만료 시간
                .expiration(expiry)

                // signWith : signature를 위한 키 설정 (default: HS256)
                .signWith(key)

                // JWT 토큰(subejct.claim.signature)로 발행
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
            }
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)   // signature, isExpired validate, parsing
                .getPayload();  // return payload
    }

    // JWT 토큰에서 사용자 정보 추출, Spring Security가 이해할 수 있게 Authentication 객체로 변환
    public Authentication getAuthentication(String token) {

        // extract Unique field
        String userId = getClaims(token).getSubject();

        // find by Unique val & exception handling
        User user = userRepository.findByUserId(userId)
                .orElseThrow(UserNotFoundException::new);

        // create UserDetails implementation
        CustomUserDetails userDetail = new CustomUserDetails(user);

        // Spring Security 내에 저장
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        )
    }
}
