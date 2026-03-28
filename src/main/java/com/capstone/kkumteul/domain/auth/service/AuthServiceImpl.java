package com.capstone.kkumteul.domain.auth.service;

import com.capstone.kkumteul.domain.auth.web.dto.LogInReq;
import com.capstone.kkumteul.domain.auth.web.dto.SignUpReq;
import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.domain.user.exception.UserNotFoundException;
import com.capstone.kkumteul.domain.user.repository.UserRepository;
import com.capstone.kkumteul.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public String saveUser(SignUpReq request) {

        // 1. id 필드는 unique 속성이므로 중복 검증
        if(userRepository.existsByUserId(request.getUserId()))

            // FIXME 커스텀 예외로 변경 필요
            throw new RuntimeException();

        // 2, User 객체 build
        User user = User.builder()
                .userId(request.getUserId())
                .password(request.getPassword())
                .username(request.getNickname())
                .gender(request.getGender())
                .role("USER")
                .build();

        // 3. User 객체 저장
        userRepository.save(user);

        // 4. JWT 발급
        String token = jwtTokenProvider.generateToken(user);

        return token;
    }

    @Override
    public String loginUser(LogInReq req) {

        // 1. 회원 조회
        User found = userRepository.findByUserId(req.getUserId())
                .orElseThrow(UserNotFoundException::new);

        // 2. JWT 발급
        String token = jwtTokenProvider.generateToken(found);

        return token;
    }
}
