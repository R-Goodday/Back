package com.capstone.kkumteul.global.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 단어장 추출용 RestTemplate.
     * 페이지당 1회 호출로 응답 빠르게 받아야 하므로 짧은 timeout 적용.
     * (기본 restTemplate은 GraphService 등 그래프 추출에 쓰여 timeout 길어도 무방)
     */
    @Bean
    public RestTemplate vocabRestTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(1))
                .readTimeout(Duration.ofSeconds(4))
                .build();
    }
}
