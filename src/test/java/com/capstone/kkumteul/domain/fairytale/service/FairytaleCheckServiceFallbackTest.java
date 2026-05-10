package com.capstone.kkumteul.domain.fairytale.service;

import com.capstone.kkumteul.domain.fairytale.entity.Paragraph;
import com.capstone.kkumteul.domain.fairytale.repository.ParagraphRepository;
import com.capstone.kkumteul.domain.fairytale.service.sse.SseService;
import com.capstone.kkumteul.domain.vocab.repository.WordEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * markImageDone inline fallback (M1) 단위 테스트.
 * Paragraph 생성 시각이 임계 초과 + vocab 마커 미존재 시 빈 vocab으로 강제 mark.
 */
@ExtendWith(MockitoExtension.class)
class FairytaleCheckServiceFallbackTest {

    @Mock private RedisTemplate<String, String> redisTemplate;
    @Mock private ValueOperations<String, String> valueOps;
    @Mock private SseService sseService;
    @Mock private WordEntryRepository wordEntryRepository;
    @Mock private ParagraphRepository paragraphRepository;

    @InjectMocks private FairytaleCheckServiceImpl service;

    private static final Long FAIRYTALE_ID = 10L;
    private static final int PAGE = 3;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "vocabFallbackThresholdSeconds", 1L);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    @Test
    @DisplayName("vocab 마커 이미 있으면 fallback 미발화")
    void noFallbackWhenVocabAlreadyMarked() {
        when(valueOps.get("vocab:10:3")).thenReturn("done");

        service.markImageDone(FAIRYTALE_ID, PAGE);

        verify(valueOps).set(eq("image:10:3"), eq("done"));
        verify(valueOps, never()).set(eq("vocab:10:3"), anyString());
        verify(paragraphRepository, never()).findByFairytaleIdAndPage(FAIRYTALE_ID, PAGE);
    }

    @Test
    @DisplayName("paragraph 없으면 fallback 미발화")
    void noFallbackWhenParagraphMissing() {
        when(valueOps.get("vocab:10:3")).thenReturn(null).thenReturn(null);
        when(paragraphRepository.findByFairytaleIdAndPage(FAIRYTALE_ID, PAGE)).thenReturn(List.of());

        service.markImageDone(FAIRYTALE_ID, PAGE);

        verify(valueOps, never()).set(eq("vocab:10:3"), anyString());
    }

    @Test
    @DisplayName("paragraph age가 임계 미만이면 fallback 미발화")
    void noFallbackWhenAgeBelowThreshold() {
        Paragraph fresh = paragraphWithCreatedAt(LocalDateTime.now());
        when(valueOps.get("vocab:10:3")).thenReturn(null).thenReturn(null);
        when(paragraphRepository.findByFairytaleIdAndPage(FAIRYTALE_ID, PAGE)).thenReturn(List.of(fresh));

        service.markImageDone(FAIRYTALE_ID, PAGE);

        verify(valueOps, never()).set(eq("vocab:10:3"), anyString());
    }

    @Test
    @DisplayName("paragraph age가 임계 초과이면 빈 vocab 마커 강제 세팅")
    void fallbackFiresWhenAgeOverThreshold() {
        Paragraph stale = paragraphWithCreatedAt(LocalDateTime.now().minusSeconds(60));
        when(valueOps.get("vocab:10:3")).thenReturn(null).thenReturn(null);
        when(paragraphRepository.findByFairytaleIdAndPage(FAIRYTALE_ID, PAGE)).thenReturn(List.of(stale));

        service.markImageDone(FAIRYTALE_ID, PAGE);

        verify(valueOps).set(eq("vocab:10:3"), eq("done"));
    }

    private Paragraph paragraphWithCreatedAt(LocalDateTime createdAt) {
        Paragraph p = Paragraph.builder().page(PAGE).text("dummy").build();
        try {
            Field f = p.getClass().getSuperclass().getDeclaredField("createdAt");
            f.setAccessible(true);
            f.set(p, createdAt);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return p;
    }
}
