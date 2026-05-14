package com.capstone.kkumteul.global.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    /**
     * 기본 RestTemplate — 그래프 추출(FastAPI → OpenAI) 호출에 사용.
     * OpenAI 평균 5~15초 응답 + cold start 대응으로 read 30초.
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(2))
                .readTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * 단어장 추출용 RestTemplate.
     * 페이지당 1회 호출로 응답 빠르게 받아야 하므로 짧은 timeout 적용.
     */
    @Bean
    public RestTemplate vocabRestTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(1))
                .readTimeout(Duration.ofSeconds(4))
                .build();
    }
}
