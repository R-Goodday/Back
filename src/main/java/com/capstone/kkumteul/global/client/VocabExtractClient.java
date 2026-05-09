package com.capstone.kkumteul.global.client;

import com.capstone.kkumteul.global.client.dto.VocabExtractRequest;
import com.capstone.kkumteul.global.client.dto.VocabExtractResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

/**
 * FastAPI(AI 서버)의 단어장 추출 엔드포인트 호출 클라이언트.
 *
 * <p>POST {fastApiBaseUrl}/vocab/extract 호출 → {word, meaning} JSON 응답.
 * timeout/5xx에 한해 1회 retry, 4xx는 즉시 실패.
 * 호출자에 예외를 전파하지 않고 {@code Optional.empty()}로 fail-open.</p>
 */
@Slf4j
@Service
public class VocabExtractClient {

    private final RestTemplate vocabRestTemplate;
    private final String fastApiBaseUrl;

    public VocabExtractClient(
            @Qualifier("vocabRestTemplate") RestTemplate vocabRestTemplate,
            @Value("${fastapi.base-url:http://localhost:8000}") String fastApiBaseUrl
    ) {
        this.vocabRestTemplate = vocabRestTemplate;
        this.fastApiBaseUrl = fastApiBaseUrl;
    }

    /**
     * 부팅 시 FastAPI 헬스체크를 한 번 호출해 cold-start 비용을 미리 흡수.
     * 실패해도 부팅은 계속 (WARN만 남김).
     */
    @PostConstruct
    public void warmup() {
        try {
            vocabRestTemplate.getForObject(fastApiBaseUrl + "/health", String.class);
            log.info("FastAPI warmup 성공: {}", fastApiBaseUrl);
        } catch (RestClientException e) {
            log.warn("FastAPI warmup 실패 (서버 미기동 가능): {}", e.getMessage());
        }
    }

    /**
     * 3문장에서 어려운 단어 1개와 풀이를 추출.
     * timeout/5xx 시 1회 retry. 모두 실패하면 {@code Optional.empty()}.
     */
    public Optional<VocabExtractResponse> extract(List<String> sentences) {
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                VocabExtractResponse response = vocabRestTemplate.postForObject(
                        fastApiBaseUrl + "/vocab/extract",
                        new VocabExtractRequest(sentences),
                        VocabExtractResponse.class
                );
                return Optional.ofNullable(response);
            } catch (ResourceAccessException | HttpServerErrorException retryable) {
                log.warn("vocab extract 일시적 실패 attempt={}: {}", attempt, retryable.getMessage());
            } catch (RestClientException e) {
                HttpStatusCode status = (e instanceof org.springframework.web.client.HttpStatusCodeException hse)
                        ? hse.getStatusCode() : null;
                log.warn("vocab extract 실패 (status={}): {}", status, e.getMessage());
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
