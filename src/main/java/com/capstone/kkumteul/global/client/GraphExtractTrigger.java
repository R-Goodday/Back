package com.capstone.kkumteul.global.client;

import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;
import com.capstone.kkumteul.domain.fairytale.entity.Paragraph;
import com.capstone.kkumteul.domain.fairytale.repository.FairytaleRepository;
import com.capstone.kkumteul.domain.fairytale.repository.ParagraphRepository;
import com.capstone.kkumteul.domain.game.repository.GraphNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 동화 본문이 모두 저장된 직후 호출되는 비동기 그래프 추출 트리거.
 *
 * <p>Kafka {@code fairytale_done} 컨슈머에서 호출하며, FastAPI {@code /graph/extract} 호출과 그래프 저장은
 * {@link GraphService} 가 담당한다. 본 메서드는 {@code @Async("graphExtractExecutor")} 로 호출되어
 * 컨슈머 스레드를 점유하지 않는다.</p>
 *
 * <p>실패는 동화 생성 흐름에 영향을 주지 않도록 try/catch 로 흡수하고 ERROR 로깅만 남긴다.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GraphExtractTrigger {

    private final FairytaleRepository fairytaleRepository;
    private final ParagraphRepository paragraphRepository;
    private final GraphNodeRepository graphNodeRepository;
    private final GraphService graphService;

    @Async("graphExtractExecutor")
    public void triggerAsync(Long fairytaleId) {
        try {
            if (graphNodeRepository.existsByFairytaleId(fairytaleId)) {
                log.info("그래프 이미 존재 — skip fairytaleId={}", fairytaleId);
                return;
            }

            Fairytale fairytale = fairytaleRepository.findById(fairytaleId).orElse(null);
            if (fairytale == null) {
                log.warn("그래프 추출 보류 - 동화 미존재 fairytaleId={}", fairytaleId);
                return;
            }

            List<Paragraph> paragraphs = paragraphRepository.findByFairytaleIdOrderByPageAsc(fairytaleId);
            if (paragraphs.isEmpty()) {
                log.warn("그래프 추출 보류 - paragraphs 미존재 fairytaleId={}", fairytaleId);
                return;
            }

            String content = paragraphs.stream()
                    .map(Paragraph::getText)
                    .collect(Collectors.joining(" "));

            graphService.extractAndSave(fairytale, content);
        } catch (Exception e) {
            log.error("그래프 추출 실패 fairytaleId={}", fairytaleId, e);
        }
    }
}
