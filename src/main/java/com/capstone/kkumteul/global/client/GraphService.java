package com.capstone.kkumteul.global.client;

import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;
import com.capstone.kkumteul.global.client.dto.GraphExtractRequest;
import com.capstone.kkumteul.global.client.dto.GraphExtractResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * FastAPI 지식그래프 추출 클라이언트.
 *
 * <p>외부 I/O 호출만 담당하고, DB 저장은 {@link GraphPersister} 에 위임한다.
 * 트랜잭션 경계는 GraphPersister 가 별도 빈으로 보유하므로 본 클래스에는 {@code @Transactional} 을 두지 않는다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GraphService {

    private final RestTemplate restTemplate;
    private final GraphPersister graphPersister;

    @Value("${fastapi.base-url:http://localhost:8000}")
    private String fastApiBaseUrl;

    public void extractAndSave(Fairytale fairytale, String content) {
        GraphExtractRequest request = new GraphExtractRequest(fairytale.getId(), content);

        GraphExtractResponse response = restTemplate.postForObject(
                fastApiBaseUrl + "/graph/extract",
                request,
                GraphExtractResponse.class
        );

        if (response == null || response.getNodes() == null) {
            throw new RuntimeException("FastAPI 그래프 추출 응답이 비어있습니다.");
        }

        graphPersister.persist(fairytale, response);
        log.info("그래프 추출 완료: fairytaleId={}", fairytale.getId());
    }
}
