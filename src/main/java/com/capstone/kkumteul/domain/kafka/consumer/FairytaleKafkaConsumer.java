package com.capstone.kkumteul.domain.kafka.consumer;

import com.capstone.kkumteul.domain.fairytale.entity.Paragraph;
import com.capstone.kkumteul.domain.fairytale.repository.ParagraphRepository;
import com.capstone.kkumteul.domain.fairytale.service.FairytaleCheckService;
import com.capstone.kkumteul.domain.kafka.dto.FairytaleCompletedMessage;
import com.capstone.kkumteul.domain.kafka.dto.ImageMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FairytaleKafkaConsumer {

    private final ParagraphRepository paragraphRepository;
    private final FairytaleCheckService fairytaleCheckService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "fairytale_done", groupId = "kkumteul-group")
    public void consumeDone(String message) {
        try {
            FairytaleCompletedMessage msg = objectMapper.readValue(message, FairytaleCompletedMessage.class);
            fairytaleCheckService.markTotalPages(msg.getFairytaleId(), msg.getTotalPages());
        } catch (Exception e) {
            log.error("fairytale_done 처리 실패 message={}", message, e);
        }
    }

    @Transactional
    @KafkaListener(topics = "fairytale_image", groupId = "kkumteul-group")
    public void consumeImage(String message) {
        try {
            ImageMessage img = objectMapper.readValue(message, ImageMessage.class);
            List<Paragraph> paragraphs = paragraphRepository.findByFairytaleIdAndPage(img.getFairytaleId(), img.getPageNo());
            if (paragraphs.isEmpty()) {
                log.warn("이미지 저장 실패 - 문단 없음 fairytaleId={}, page={}", img.getFairytaleId(), img.getPageNo());
                return;
            }
            paragraphs.getFirst().updateImageUrl(img.getImageurl());
            fairytaleCheckService.markImageDone(img.getFairytaleId(), img.getPageNo());
        } catch (Exception e) {
            log.error("fairytale_image 처리 실패 message={}", message, e);
        }
    }
}