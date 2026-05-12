package com.capstone.kkumteul.domain.fairytale.voice.web.controller;

import com.capstone.kkumteul.domain.fairytale.voice.exception.InvalidFileException;
import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.global.response.SuccessResponse;
import com.capstone.kkumteul.global.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
// not hard-fix configuration convention
@RequestMapping("/api/users/voice")
@RequiredArgsConstructor
public class VoiceController {

    @PostMapping
    public ResponseEntity<SuccessResponse<?>> sendTtsRequestMessage(
            @AuthUser User user,
            @RequestPart MultipartFile wavFile
    ) {

        // File validation
        if(wavFile.isEmpty() || wavFile.getSize() == 0 || wavFile.getName().split("\\.")[1].equals("wav"))
            throw new InvalidFileException();


        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.empty());
    }
}
