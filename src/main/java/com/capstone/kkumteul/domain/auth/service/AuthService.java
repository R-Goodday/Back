package com.capstone.kkumteul.domain.auth.service;

import com.capstone.kkumteul.domain.auth.web.dto.SignUpReq;

public interface AuthService {

    // 회원가입 시 유저를 DB에 저장하는 메소드
    Void saveUser(SignUpReq req);
}
