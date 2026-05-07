package com.capstone.kkumteul.domain.kafka.service;

import com.capstone.kkumteul.domain.fairytale.web.dto.FairytaleGenerateReq;
import com.capstone.kkumteul.domain.kafka.dto.FairytaleGenerateMessage;
import com.capstone.kkumteul.domain.kafka.dto.MessageInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/* 동화 생성 이벤트 전파 */

@Service
@Slf4j(topic = "event")
@RequiredArgsConstructor
public class EventService {

    private final KafkaTemplate<String, MessageInterface> kafkaTemplate;

    public void createFairytaleMessageSend(Long userId, FairytaleGenerateReq request) {

        FairytaleGenerateMessage message = FairytaleGenerateMessage.builder()
                .userId(userId)
                .background(request.getBackground())
                .charSpecies(request.getCharSpecie())
                .morality(request.getMorality())
                .build();

        log.info("fairytale_generate userId={}, message={}", userId, message);

        kafkaTemplate.send("fairytale_generate", message);

    }
}
