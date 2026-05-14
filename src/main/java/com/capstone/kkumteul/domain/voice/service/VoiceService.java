package com.capstone.kkumteul.domain.voice.service;

import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.domain.voice.web.dto.TtsFileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface VoiceService {
    Void saveWav(MultipartFile wavFile, User user);
    Boolean hasTtsHistory(Long userId, Long paragraphId);
    TtsFileResponse getTtsFile(Long userId, Long paragraphId);
    Void createTtsFile(Long userId, Long paragraphId);
}
