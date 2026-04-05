package com.capstone.kkumteul.domain.user.service;

import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.domain.user.web.dto.ProfileRes;
import com.capstone.kkumteul.domain.user.web.dto.ProfileUpdateReq;

public interface UserService {

    ProfileRes getProfile(User user);

    ProfileRes updateProfile(User user, ProfileUpdateReq req);
}
