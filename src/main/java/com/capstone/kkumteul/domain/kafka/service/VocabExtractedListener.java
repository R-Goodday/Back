package com.capstone.kkumteul.domain.kafka.service;

import com.capstone.kkumteul.domain.kafka.dto.VocabExtractedMessage;
import com.capstone.kkumteul.domain.vocab.service.VocabService;
import com.capstone.kkumteul.domain.vocab.service.dto.VocabExtractionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * vocab_extracted 토픽 Consumer.
 *
 * <p>처리 실패는 그대로 throw해서 {@link com.capstone.kkumteul.global.config.KafkaConsumerConfig}의
 * DefaultErrorHandler + DLT recoverer에 위임한다 (재시도 후 DLT 직전 markVocabDone 호출).</p>
 */
@Slf4j
@Component
@Profile("!dev")
@RequiredArgsConstructor
public class VocabExtractedListener {

    private final VocabService vocabService;

    @KafkaListener(
            topics = "${VOCAB_EXTRACTED:vocab_extracted}",
            groupId = "${VOCAB_EXTRACTED_GROUP_ID:kkumteul-vocab}",
            containerFactory = "vocabKafkaListenerContainerFactory"
    )
    public void onMessage(VocabExtractedMessage message) {
        VocabExtractionResult result = vocabService.processExtractedWord(message);
        log.info("vocab_extracted processed fairytaleId={}, pageNo={}, status={}, messageId={}",
                message.getFairytaleId(), message.getPageNo(), result.status(), message.getMessageId());
    }
}
