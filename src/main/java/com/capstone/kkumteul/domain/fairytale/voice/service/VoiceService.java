package com.capstone.kkumteul.domain.fairytale.voice.service;

import com.capstone.kkumteul.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface VoiceService {
    Void saveWav(MultipartFile wavFile, User user);
}
