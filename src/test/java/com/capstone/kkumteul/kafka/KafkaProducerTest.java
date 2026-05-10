package com.capstone.kkumteul.kafka;

import com.capstone.kkumteul.domain.fairytale.entity.Background;
import com.capstone.kkumteul.domain.fairytale.entity.CharSpecies;
import com.capstone.kkumteul.domain.fairytale.entity.Morality;
import com.capstone.kkumteul.domain.kafka.dto.FairytaleGenerateMessage;
import com.capstone.kkumteul.domain.kafka.dto.MessageInterface;
import com.capstone.kkumteul.global.config.KafkaProducerConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Tag("kafka-broker")
@EnabledIfEnvironmentVariable(named = "KAFKA_URL", matches = ".+")
@SpringBootTest(
        classes = KafkaProducerConfig.class
)
class KafkaProducerTest {

    @Autowired
    private KafkaTemplate<String, MessageInterface> kafkaTemplate;

    @Test
    void messagePublicationTest() throws Exception {

        FairytaleGenerateMessage message = FairytaleGenerateMessage.builder()
                .userId(1L)
                .background(Background.FOREST_NATURE)
                .charSpecies(CharSpecies.ANIMAL)
                .morality(Morality.KINDNESS_REWARDED)
                .build();

        CompletableFuture<SendResult<String, MessageInterface>> future =
                kafkaTemplate.send("fairytale_generate", message);

        SendResult<String, MessageInterface> result = future.get(10, TimeUnit.SECONDS);

        assertThat(result.getRecordMetadata().topic()).isEqualTo("fairytale_generate");
        assertThat(result.getRecordMetadata().offset()).isGreaterThanOrEqualTo(0);

        log.info("토픽: {}", result.getRecordMetadata().topic());
        log.info("파티션: {}", result.getRecordMetadata().partition());
        log.info("오프셋: {}", result.getRecordMetadata().offset());
    }
}
