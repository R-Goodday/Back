package com.capstone.kkumteul.domain.kafka.service;

import com.capstone.kkumteul.domain.fairytale.service.FairytaleCheckService;
import com.capstone.kkumteul.domain.kafka.dto.VocabExtractedMessage;
import com.capstone.kkumteul.domain.vocab.service.VocabService;
import com.capstone.kkumteul.domain.vocab.service.dto.VocabExtractionResult;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * vocab_extracted 토픽 Listener의 EmbeddedKafka 통합 테스트.
 *
 * <p>실제 Kafka broker(임베디드)를 띄우고 메시지 1건 발행 → Listener가
 * VocabService.processExtractedWord를 호출하는지 wire-format 라운드트립을 검증한다.</p>
 */
@Tag("kafka-broker")
@SpringBootTest(properties = {
        "KAFKA_URL=${spring.embedded.kafka.brokers}",
        "FAIRYTALE_GENERATION=fairytale_generate",
        "VOCAB_EXTRACTED=vocab_extracted",
        "VOCAB_EXTRACTED_GROUP_ID=kkumteul-vocab-test"
})
@EmbeddedKafka(partitions = 1, topics = {"vocab_extracted", "fairytale_generate"})
@DirtiesContext
class VocabExtractedListenerIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @MockBean
    private VocabService vocabService;

    @MockBean
    private FairytaleCheckService fairytaleCheckService;

    @Test
    @DisplayName("vocab_extracted 토픽 메시지 1건 → Listener가 VocabService.processExtractedWord 호출")
    void listenerInvokesProcessExtractedWord() {
        when(vocabService.processExtractedWord(any(VocabExtractedMessage.class)))
                .thenReturn(VocabExtractionResult.noDifficultWord());

        for (MessageListenerContainer container : registry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
        }

        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        producerProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        ProducerFactory<String, VocabExtractedMessage> pf = new DefaultKafkaProducerFactory<>(producerProps);
        KafkaTemplate<String, VocabExtractedMessage> template = new KafkaTemplate<>(pf);

        VocabExtractedMessage msg = VocabExtractedMessage.builder()
                .fairytaleId(11L)
                .pageNo(3)
                .word(null)
                .meaning(null)
                .userId(42L)
                .messageId("integration-1")
                .build();
        template.send("vocab_extracted", msg);
        template.flush();

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                verify(vocabService, times(1)).processExtractedWord(any(VocabExtractedMessage.class))
        );
    }
}
