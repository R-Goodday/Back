package com.capstone.kkumteul.kafka;

import com.capstone.kkumteul.domain.fairytale.entity.Background;
import com.capstone.kkumteul.domain.fairytale.entity.CharSpecies;
import com.capstone.kkumteul.domain.fairytale.entity.Morality;
import com.capstone.kkumteul.domain.kafka.dto.FairytaleGenerateMessage;
import com.capstone.kkumteul.domain.kafka.dto.MessageInterface;
import com.capstone.kkumteul.global.config.KafkaProducerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = KafkaProducerConfig.class,
        properties = "KAFKA_URL=52.78.205.133:9092"
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

        System.out.println("토픽: " + result.getRecordMetadata().topic());
        System.out.println("파티션: " + result.getRecordMetadata().partition());
        System.out.println("오프셋: " + result.getRecordMetadata().offset());
    }
}
