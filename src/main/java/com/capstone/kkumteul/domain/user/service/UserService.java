package com.capstone.kkumteul.domain.user.service;

import com.capstone.kkumteul.domain.user.web.dto.ProfileRes;
import com.capstone.kkumteul.domain.user.web.dto.ProfileUpdateReq;

public interface UserService {

    ProfileRes getProfile(Long userId);

    ProfileRes updateProfile(Long userId, ProfileUpdateReq req);
}
