package com.capstone.kkumteul.global.config;

import com.capstone.kkumteul.domain.fairytale.service.FairytaleCheckService;
import com.capstone.kkumteul.domain.kafka.dto.MessageInterface;
import com.capstone.kkumteul.domain.kafka.dto.VocabExtractedMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * KafkaConsumerConfig.buildVocabRecoverer 단위 테스트.
 *
 * <p>recoverer 람다가 다음을 만족함을 직접 호출로 검증:
 * <ul>
 *   <li>record.value()가 {@link VocabExtractedMessage}이면 markVocabDone 호출 후 DLT publish</li>
 *   <li>record.value()가 null이면 markVocabDone 미호출, DLT publish만 수행</li>
 * </ul>
 */
class KafkaConsumerConfigDltTest {

    @SuppressWarnings({"rawtypes", "unchecked"})
    private ConsumerRecordRecoverer buildRecoverer(
            FairytaleCheckService check, KafkaTemplate template
    ) {
        TopicPartition tp = new TopicPartition("vocab_extracted.DLT", 0);
        RecordMetadata md = new RecordMetadata(tp, 0L, 0, 0L, 0, 0);
        SendResult sendResult = new SendResult(new ProducerRecord<>("vocab_extracted.DLT", null), md);
        when(template.send(any(ProducerRecord.class))).thenReturn(CompletableFuture.completedFuture(sendResult));
        KafkaConsumerConfig config = new KafkaConsumerConfig("localhost:9092", "kkumteul-vocab", check);
        return config.buildVocabRecoverer(template);
    }

    @Test
    @DisplayName("VocabExtractedMessage 페이로드면 markVocabDone 호출 후 DLT publish")
    @SuppressWarnings({"rawtypes", "unchecked"})
    void recovererCallsMarkVocabDoneThenPublishesDlt() {
        FairytaleCheckService check = mock(FairytaleCheckService.class);
        KafkaTemplate<String, MessageInterface> template = mock(KafkaTemplate.class);
        ConsumerRecordRecoverer recoverer = buildRecoverer(check, template);

        VocabExtractedMessage payload = VocabExtractedMessage.builder()
                .fairytaleId(7L).pageNo(2).word("용감").meaning("...").messageId("m-7-2").build();
        ConsumerRecord<String, VocabExtractedMessage> record =
                new ConsumerRecord<>("vocab_extracted", 0, 0L, null, payload);

        recoverer.accept(record, new RuntimeException("simulated"));

        verify(check, times(1)).markVocabDone(eq(7L), eq(2));
        verify(template, atLeastOnce()).send(any(ProducerRecord.class));
    }

    @Test
    @DisplayName("record.value()가 null(역직렬화 실패)이면 markVocabDone 미호출, DLT publish만")
    @SuppressWarnings({"rawtypes", "unchecked"})
    void recovererSkipsMarkWhenValueNull() {
        FairytaleCheckService check = mock(FairytaleCheckService.class);
        KafkaTemplate<String, MessageInterface> template = mock(KafkaTemplate.class);
        ConsumerRecordRecoverer recoverer = buildRecoverer(check, template);

        ConsumerRecord<String, VocabExtractedMessage> record =
                new ConsumerRecord<>("vocab_extracted", 0, 0L, null, null);

        recoverer.accept(record, new RuntimeException("deserialization failure"));

        verify(check, never()).markVocabDone(any(), any(Integer.class));
        verify(template, atLeastOnce()).send(any(ProducerRecord.class));
    }
}
