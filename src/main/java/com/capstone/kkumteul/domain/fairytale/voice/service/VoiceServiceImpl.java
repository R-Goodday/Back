package com.capstone.kkumteul.domain.fairytale.voice.service;

import com.capstone.kkumteul.domain.fairytale.voice.entity.VoiceModel;
import com.capstone.kkumteul.domain.fairytale.voice.exception.FileUploadFailException;
import com.capstone.kkumteul.domain.fairytale.voice.repository.VoiceModelRepository;
import com.capstone.kkumteul.domain.fairytale.voice.web.dto.TtsModelingRequest;
import com.capstone.kkumteul.domain.kafka.service.EventService;
import com.capstone.kkumteul.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoiceServiceImpl implements VoiceService {

    private final S3Uploader s3Uploader;
    private final VoiceModelRepository voiceModelRepository;
    private final EventService eventService;

    @Override
    @Transactional
    public Void saveMp3(MultipartFile wavFile, User user) {

        String uploadedUrl;

        try {
            uploadedUrl = s3Uploader.upload(wavFile, user);
        } catch (IOException e) {
            log.error("VoiceService error occurred. userId = {}, filename = {}", user.getId(), wavFile.getOriginalFilename());
            throw new FileUploadFailException();
        }

        VoiceModel saved = VoiceModel.builder()
                .user(user)
                .originFilename(wavFile.getOriginalFilename())
                .build();

        voiceModelRepository.save(saved);
        sendKafkaMessage(user.getId(), uploadedUrl);

        return null;
    }

    private TtsModelingRequest sendKafkaMessage(Long userId, String uploadedUrl) {
        TtsModelingRequest requestBody = new TtsModelingRequest(userId, uploadedUrl);
        eventService.sendTtsModelingRequest(requestBody);

        return requestBody;
    }
}
