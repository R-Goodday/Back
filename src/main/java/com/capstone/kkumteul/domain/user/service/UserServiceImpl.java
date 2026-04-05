package com.capstone.kkumteul.domain.user.service;

import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.domain.user.exception.UserNotFoundException;
import com.capstone.kkumteul.domain.user.repository.UserRepository;
import com.capstone.kkumteul.domain.user.web.dto.ProfileRes;
import com.capstone.kkumteul.domain.user.web.dto.ProfileUpdateReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public ProfileRes getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        return ProfileRes.from(user);
    }

    @Override
    @Transactional
    public ProfileRes updateProfile(Long userId, ProfileUpdateReq req) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        user.updateProfile(req.getGender(), req.getAge());
        return ProfileRes.from(user);
    }
}
