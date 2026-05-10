package com.capstone.kkumteul.global.config;

import com.capstone.kkumteul.domain.fairytale.service.FairytaleCheckService;
import com.capstone.kkumteul.domain.kafka.dto.MessageInterface;
import com.capstone.kkumteul.domain.kafka.dto.VocabExtractedMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Consumer 인프라.
 *
 * <p>두 가지 컨슈머를 등록:</p>
 * <ul>
 *   <li>기본 String 컨슈머 — 일반 텍스트 메시지용 (기존 develop 컨벤션)</li>
 *   <li>vocab_extracted 전용 JSON 컨슈머 — ErrorHandlingDeserializer wrap, DLT recoverer가
 *   DLT publish 직전 markVocabDone 호출해 SSE hang 방지</li>
 * </ul>
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private final String kafkaUrl;
    private final String vocabGroupId;
    private final FairytaleCheckService fairytaleCheckService;

    public KafkaConsumerConfig(
            @Value("${KAFKA_URL}") String kafkaUrl,
            @Value("${VOCAB_EXTRACTED_GROUP_ID:kkumteul-vocab}") String vocabGroupId,
            FairytaleCheckService fairytaleCheckService
    ) {
        this.kafkaUrl = kafkaUrl;
        this.vocabGroupId = vocabGroupId;
        this.fairytaleCheckService = fairytaleCheckService;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, VocabExtractedMessage> vocabConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, vocabGroupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.capstone.kkumteul.domain.kafka.dto");
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, VocabExtractedMessage.class.getName());
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, VocabExtractedMessage> vocabKafkaListenerContainerFactory(
            KafkaTemplate<String, MessageInterface> kafkaTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, VocabExtractedMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(vocabConsumerFactory());
        factory.getContainerProperties().setAckMode(AckMode.RECORD);
        factory.setCommonErrorHandler(buildVocabErrorHandler(kafkaTemplate));
        return factory;
    }

    /**
     * DLT recoverer + retry backoff. record.value()가 VocabExtractedMessage이면
     * DLT publish 직전 markVocabDone을 호출해 SSE hang을 방지한다.
     */
    public DefaultErrorHandler buildVocabErrorHandler(KafkaTemplate<String, MessageInterface> kafkaTemplate) {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                buildVocabRecoverer(kafkaTemplate),
                new FixedBackOff(0L, 2L)
        );
        errorHandler.addNotRetryableExceptions(DeserializationException.class, MethodArgumentNotValidException.class);
        return errorHandler;
    }

    /**
     * DLT 직전 markVocabDone 호출 + DeadLetterPublishingRecoverer 위임 람다.
     * 단위 테스트에서 직접 호출 가능하도록 분리.
     */
    public ConsumerRecordRecoverer buildVocabRecoverer(KafkaTemplate<String, MessageInterface> kafkaTemplate) {
        DeadLetterPublishingRecoverer dlpr = new DeadLetterPublishingRecoverer(kafkaTemplate);
        return (record, ex) -> {
            if (record.value() instanceof VocabExtractedMessage m) {
                fairytaleCheckService.markVocabDone(m.getFairytaleId(), m.getPageNo());
            }
            dlpr.accept(record, ex);
        };
    }
}
