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
    public ProfileRes getProfile(User user) {
        return ProfileRes.from(user);
    }

    @Override
    @Transactional
    public ProfileRes updateProfile(User user, ProfileUpdateReq req) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(UserNotFoundException::new);
        managedUser.updateProfile(req.getGender(), req.getAge());
        return ProfileRes.from(managedUser);
    }
}
