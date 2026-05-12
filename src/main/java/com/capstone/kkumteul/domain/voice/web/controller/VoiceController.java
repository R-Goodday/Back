package com.capstone.kkumteul.domain.voice.web.controller;

import com.capstone.kkumteul.domain.voice.exception.InvalidFileException;
import com.capstone.kkumteul.domain.voice.service.VoiceService;
import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.domain.voice.web.dto.TtsFileResponse;
import com.capstone.kkumteul.global.response.SuccessResponse;
import com.capstone.kkumteul.global.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
// not hard-fix configuration convention
@RequestMapping("/api/users/voice")
@RequiredArgsConstructor
public class VoiceController {

    private final VoiceService voiceService;

    @PostMapping
    public ResponseEntity<SuccessResponse<?>> sendTtsRequestMessage(
            @AuthUser User user,
            @RequestPart MultipartFile wavFile
    ) {

        // File validation
        String originName = wavFile.getOriginalFilename();
        if(wavFile.isEmpty()
                || originName.isBlank()
        || !originName.toLowerCase().endsWith(".wav"))
            throw new InvalidFileException();

        voiceService.saveWav(wavFile, user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.created(user.getUserId()));
    }

    @GetMapping("/{paragraphId}")
    public ResponseEntity<SuccessResponse<?>> getTtsFile(
            @AuthUser User user,
            @PathVariable Long paragraphId
    ) {
        TtsFileResponse response = voiceService.getTtsFile(user.getId(), paragraphId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.ok(response));
    }

    @PostMapping("/{paragraphId}")
    public ResponseEntity<SuccessResponse<?>> postTtsFile(
            @AuthUser User user,
            @PathVariable Long paragraphId
    ) {
        voiceService.createTtsFile(user.getId(), paragraphId);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(SuccessResponse.accepted());
    }

    @GetMapping("/{paragraphId}/check")
    public ResponseEntity<SuccessResponse<?>> hasTtsHistory(
            @AuthUser User user,
            @PathVariable Long paragraphId
    ) {
        Boolean response = voiceService.hasTtsHistory(user.getId(), paragraphId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.ok(response));
    }

}
