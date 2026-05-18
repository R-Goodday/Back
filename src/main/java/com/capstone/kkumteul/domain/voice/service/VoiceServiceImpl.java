package com.capstone.kkumteul.domain.voice.service;

import com.capstone.kkumteul.domain.fairytale.exception.ParagraphNotFoundException;
import com.capstone.kkumteul.domain.fairytale.validator.ParagraphValidator;
import com.capstone.kkumteul.domain.kafka.service.EventService;
import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.domain.voice.entity.TtsHistory;
import com.capstone.kkumteul.domain.voice.entity.VoiceModel;
import com.capstone.kkumteul.domain.voice.exception.FileUploadFailException;
import com.capstone.kkumteul.domain.voice.exception.VoiceFileNotFoundException;
import com.capstone.kkumteul.domain.voice.repository.TtsHistoryRepository;
import com.capstone.kkumteul.domain.voice.repository.VoiceModelRepository;
import com.capstone.kkumteul.domain.voice.web.dto.TtsFileRequest;
import com.capstone.kkumteul.domain.voice.web.dto.TtsFileResponse;
import com.capstone.kkumteul.domain.voice.web.dto.TtsModelingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoiceServiceImpl implements VoiceService {

    private final S3Uploader s3Uploader;
    private final VoiceModelRepository voiceModelRepository;
    private final TtsHistoryRepository ttsRepository;
    private final EventService eventService;
    private final ParagraphValidator paragraphValidator;

    @Override
    @Transactional
    public Void saveWav(byte[] wavFile, String originalFilename, User user) {

        String uploadedUrl;

        try {
            uploadedUrl = s3Uploader.upload(wavFile, originalFilename, user);
        } catch (IOException e) {
            log.error("VoiceService error occurred. userId = {}, filename = {}", user.getId(), originalFilename);
            throw new FileUploadFailException();
        }

        VoiceModel saved = VoiceModel.builder()
                .user(user)
                .wavFileUrl(uploadedUrl)
                .build();

        voiceModelRepository.save(saved);
        sendTtsModelingRequest(user.getId(), uploadedUrl);

        return null;
    }

    @Override
    public Boolean hasTtsHistory(Long userId, Long paragraphId) {
        return ttsRepository.findByParagraphIdAndUserId(paragraphId, userId).isPresent();
    }

    @Override
    public Void createTtsFile(Long userId, Long fairytaleId) {

        if(!paragraphValidator.fairytaleIdIsValid(fairytaleId)) {
            throw new ParagraphNotFoundException();
        }

        sendTtsFileRequest(userId, fairytaleId);
        return null;
    }

    @Override
    public TtsFileResponse getTtsFile(Long userId, Long paragraphId) {

        // validation paragraph id by using fairytale package's validator
        if (!paragraphValidator.paragraphIdIsValid(paragraphId))
            throw new ParagraphNotFoundException();

        // User가 해당하는 paragraphId에 대한 tts 음성 파일을 만든 이력이 있는지 체크
        Optional<TtsHistory> found = ttsRepository.findByParagraphIdAndUserId(paragraphId, userId);
        boolean isFound = found.isPresent();

        TtsFileResponse response;

        if (!isFound) {
            // 만든 적 없다면 해당 요청에 들어오면 안됨. 예외처리
            log.error("Voice file not created. userId={}, paragraphId={}", userId, paragraphId);
            throw new VoiceFileNotFoundException();
        } else {
            // 만든 적 있다면 DB에서 그 음성 파일을 조회
            TtsHistory ttsHistory = found.get();
            response = new TtsFileResponse(
                    ttsHistory.getParagraph().getId(),
                    ttsHistory.getTtsUrl()
            );
        }

        return response;
    }

    private void sendTtsFileRequest(Long userId, Long paragraphId) {
        TtsFileRequest request = new TtsFileRequest(userId, paragraphId);

        eventService.sendTtsFileRequest(request);
    }

    private void sendTtsModelingRequest(Long userId, String uploadedUrl) {
        TtsModelingRequest requestBody = new TtsModelingRequest(userId, uploadedUrl);
        eventService.sendTtsModelingRequest(requestBody);
    }
}
