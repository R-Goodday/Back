package com.capstone.kkumteul.domain.fairytale.voice.service;

import com.capstone.kkumteul.domain.fairytale.voice.web.dto.TtsModelingRequest;
import com.capstone.kkumteul.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface VoiceService {
    TtsModelingRequest saveMp3(MultipartFile wavFile, User user);
}
