package com.capstone.kkumteul.domain.voice.service;

import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.domain.voice.web.dto.TtsFileResponse;

public interface VoiceService {
    Void saveWav(byte[] wavFile, String originalFilename, User user);
    Boolean hasTtsHistory(Long userId, Long paragraphId);
    TtsFileResponse getTtsFile(Long userId, Long paragraphId);
    Void createTtsFile(Long userId, Long paragraphId);
}
