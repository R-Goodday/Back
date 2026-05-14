package com.capstone.kkumteul.domain.kafka.service;

import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;
import com.capstone.kkumteul.domain.fairytale.repository.FairytaleRepository;
import com.capstone.kkumteul.domain.fairytale.web.dto.FairytaleGenerateReq;
import com.capstone.kkumteul.domain.kafka.dto.FairytaleGenerateMessage;
import com.capstone.kkumteul.domain.kafka.dto.MessageInterface;
import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.domain.voice.web.dto.TtsFileRequest;
import com.capstone.kkumteul.domain.voice.web.dto.TtsModelingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/* 동화 생성 이벤트 전파 */

@Service
@Profile("!dev")
@Slf4j(topic = "event")
@RequiredArgsConstructor
public class EventService {

    private final KafkaTemplate<String, MessageInterface> kafkaTemplate;
    private final FairytaleRepository fairytaleRepository;

    @Value("${FAIRYTALE_GENERATION}")
    private String FAIRYTALE_GENERATION;

    @Value(("${TTS_MODELING}"))
    private String TTS_MODELING;

    @Transactional
    public Long createFairytaleMessageSend(User user, FairytaleGenerateReq request) {

        Fairytale created = Fairytale.builder()
                .user(user)
                .title("NONE")
                .content("")
                .morality(request.getMorality())
                .background(request.getBackground())
                .charSpecies(request.getCharSpecies())
                .build();

        Fairytale saved = fairytaleRepository.save(created);

        FairytaleGenerateMessage message = FairytaleGenerateMessage.builder()
                .userId(user.getId())
                .fairytaleId(saved.getId())
                .background(request.getBackground())
                .charSpecies(request.getCharSpecies())
                .morality(request.getMorality())
                .build();

        log.info("fairytale_generate userId={}, message={}", user.getId(), message);

        kafkaTemplate.send(FAIRYTALE_GENERATION, message)
                .whenComplete((result, e) -> {
                    if (e != null) {
                        log.error("fairytale_generate failed. userId={}, message={}", user.getId(), message, e);
                    }
                });

        return saved.getId();
    }

    public void sendTtsModelingRequest(TtsModelingRequest message) {

        kafkaTemplate.send(TTS_MODELING, message)
                .whenComplete((result, e) -> {
                    if(e != null) {
                        log.error("tts_modeling_request failed. userId={}, message={}", message.getUserId(), message.getUploadedUrl(), e);
                    }
                });
    }

    public void sendTtsFileRequest(TtsFileRequest message) {

        kafkaTemplate.send(TTS_MODELING, message)
                .whenComplete((result, e) -> {
                    if( e != null) {
                        log.error("tts_file_request occurred error. userId={}, paragraphId={}", message.getUserId(), message.getFairytaleId(), e);
                    }
                });
    }
}
