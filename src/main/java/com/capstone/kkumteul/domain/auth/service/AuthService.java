package com.capstone.kkumteul.domain.auth.service;

import com.capstone.kkumteul.domain.auth.web.dto.LogInReq;
import com.capstone.kkumteul.domain.auth.web.dto.SignUpReq;

public interface AuthService {

    // 회원가입 시 유저를 DB에 저장하는 메소드
    String saveUser(SignUpReq req);

    // DB에서 회원 조회 후 성공하면 토큰 발급해주는 메소드
    String loginUser(LogInReq req);
}
