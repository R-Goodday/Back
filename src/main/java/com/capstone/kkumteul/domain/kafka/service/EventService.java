package com.capstone.kkumteul.domain.kafka.service;

import com.capstone.kkumteul.domain.fairytale.web.dto.FairytaleGenerateReq;
import com.capstone.kkumteul.domain.kafka.dto.FairytaleGenerateMessage;
import com.capstone.kkumteul.domain.kafka.dto.MessageInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/* 동화 생성 이벤트 전파 */

@Service
@Slf4j(topic = "event")
@RequiredArgsConstructor
public class EventService {

    private final KafkaTemplate<String, MessageInterface> kafkaTemplate;

    @Value("${FAIRYTALE_GENERATION}")
    private String FAIRYTALE_GENERATION;

    public void createFairytaleMessageSend(Long userId, FairytaleGenerateReq request) {

        FairytaleGenerateMessage message = FairytaleGenerateMessage.builder()
                .userId(userId)
                .background(request.getBackground())
                .charSpecies(request.getCharSpecies())
                .morality(request.getMorality())
                .build();

        log.info("fairytale_generate userId={}, message={}", userId, message);

        kafkaTemplate.send(FAIRYTALE_GENERATION, message)
                .whenComplete((result, e) -> {
                    if (e != null) {
                        log.error("fairytale_generate failed", e);
                    }
                });

    }
}
