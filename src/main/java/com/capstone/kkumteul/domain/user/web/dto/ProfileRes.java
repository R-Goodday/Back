package com.capstone.kkumteul.domain.user.web.dto;

import com.capstone.kkumteul.domain.user.entity.Gender;
import com.capstone.kkumteul.domain.user.entity.User;

public record ProfileRes(
        String username,
        Gender gender,
        Integer age
) {
    public static ProfileRes from(User user) {
        return new ProfileRes(user.getUsername(), user.getGender(), user.getAge());
    }
}
