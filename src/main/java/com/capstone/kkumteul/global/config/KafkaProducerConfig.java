package com.capstone.kkumteul.global.config;

import com.capstone.kkumteul.domain.kafka.dto.MessageInterface;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("!dev")
public class KafkaProducerConfig {

    private final String KAFKA_URL;

    public KafkaProducerConfig(
            @Value("${KAFKA_URL}") String KAFKA_URL
    ) { this.KAFKA_URL = KAFKA_URL; }

    @Bean

    public ProducerFactory<String, MessageInterface> producerFactory() {

        Map<String, Object> config = new HashMap<>();

        // key - String Serialization
        // value - Json Serialization
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_URL);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, MessageInterface> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
