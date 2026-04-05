package com.capstone.kkumteul.domain.user.web.controller;

import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.domain.user.service.UserService;
import com.capstone.kkumteul.domain.user.web.dto.ProfileRes;
import com.capstone.kkumteul.domain.user.web.dto.ProfileUpdateReq;
import com.capstone.kkumteul.global.response.SuccessResponse;
import com.capstone.kkumteul.global.security.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<SuccessResponse<ProfileRes>> getProfile(
            @AuthUser User user
    ) {
        ProfileRes res = userService.getProfile(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.ok(res));
    }

    @PutMapping("/profile")
    public ResponseEntity<SuccessResponse<ProfileRes>> updateProfile(
            @AuthUser User user,
            @Valid @RequestBody ProfileUpdateReq req
    ) {
        ProfileRes res = userService.updateProfile(user.getId(), req);
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.ok(res));
    }
}
