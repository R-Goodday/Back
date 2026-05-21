package com.capstone.kkumteul.domain.fairytale.service;

import com.capstone.kkumteul.domain.fairytale.entity.Paragraph;
import com.capstone.kkumteul.domain.fairytale.repository.ParagraphRepository;
import com.capstone.kkumteul.domain.fairytale.service.sse.SseService;
import com.capstone.kkumteul.domain.fairytale.web.dto.SseEventRes;
import com.capstone.kkumteul.domain.vocab.entity.WordEntry;
import com.capstone.kkumteul.domain.vocab.repository.WordEntryRepository;
import com.capstone.kkumteul.domain.voice.repository.TtsHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FairytaleCheckServiceImpl implements FairytaleCheckService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SseService sseService;
    private final WordEntryRepository wordEntryRepository;
    private final ParagraphRepository paragraphRepository;
    private final TtsHistoryRepository ttsHistoryRepository;

    @Value("${vocab.fallback-threshold-seconds:300}")
    private long vocabFallbackThresholdSeconds;

    private static final String VOCAB_KEY = "vocab:%d:%d";
    private static final String IMAGE_KEY = "image:%d:%d";
    private static final String TTS_KEY = "tts:%d:%d";
    private static final String TOTAL_KEY = "total:%d";
    private static final String SENT_KEY = "sent:%d";
    private static final String DONE = "done";

    @Override
    public void markVocabDone(Long fairytaleId, int page) {
        redisTemplate.opsForValue().set(String.format(VOCAB_KEY, fairytaleId, page), DONE);
        log.info("[VOCAB DONE] fairytaleId={}, page={}", fairytaleId, page);
        checkAndSend(fairytaleId, page);
    }

    @Override
    public void markImageDone(Long fairytaleId, int page) {
        redisTemplate.opsForValue().set(String.format(IMAGE_KEY, fairytaleId, page), DONE);
        log.info("[IMAGE DONE] fairytaleId={}, page={}", fairytaleId, page);
        forceVocabIfStale(fairytaleId, page);
        checkAndSend(fairytaleId, page);
    }

    @Override
    public void markTtsFileDone(Long fairytaleId, int page, String ttsUrl) {
        redisTemplate.opsForValue().set(String.format(TTS_KEY, fairytaleId, page), DONE.concat("@" + ttsUrl));
        log.info("[TTS DONE] fairytaleId={}, page={}", fairytaleId, page);
        checkAndSend(fairytaleId, page);
    }

    /**
     * image done 시점에 vocab 마커가 없고 paragraph 생성 후 임계 초과면 빈 vocab으로 강제 mark.
     * AI Producer가 vocab_extracted를 누락한 경우의 SSE hang을 방지한다.
     */
    private void forceVocabIfStale(Long fairytaleId, int page) {
        String vocabKey = String.format(VOCAB_KEY, fairytaleId, page);
        if (redisTemplate.opsForValue().get(vocabKey) != null) return;

        List<Paragraph> paragraphs = paragraphRepository.findByFairytaleIdAndPage(fairytaleId, page);
        if (paragraphs.isEmpty()) return;

        LocalDateTime created = paragraphs.getFirst().getCreatedAt();
        if (created == null) return;
        long ageSeconds = Duration.between(created, LocalDateTime.now()).getSeconds();
        if (ageSeconds < vocabFallbackThresholdSeconds) return;

        log.warn("vocab fallback fired fairytaleId={}, page={}, ageSeconds={}", fairytaleId, page, ageSeconds);
        redisTemplate.opsForValue().set(vocabKey, DONE);
    }

    // FIXME isBothDone -> isAllDone
    @Override
    public boolean isBothDone(Long fairytaleId, int page) {
        String vocabStatus = redisTemplate.opsForValue().get(String.format(VOCAB_KEY, fairytaleId, page));
        String imageStatus = redisTemplate.opsForValue().get(String.format(IMAGE_KEY, fairytaleId, page));

        String ttsStatus = redisTemplate.opsForValue().get(String.format(TTS_KEY, fairytaleId, page));
        return DONE.equals(vocabStatus) && DONE.equals(imageStatus) &&
                ttsStatus != null && DONE.equals(ttsStatus.split("@")[0]);
    }

    //sse전송
    private void checkAndSend(Long fairytaleId, int page) {
        boolean both = isBothDone(fairytaleId, page);
        log.info("[CHECK] fairytaleId={}, page={}, isBothDone={}", fairytaleId, page, both);
        if (!both) return;

        Optional<WordEntry> wordEntry = wordEntryRepository.findByFairytaleIdAndPageNo(fairytaleId, page);
        String ttsUrl = redisTemplate.opsForValue().get(String.format(TTS_KEY, fairytaleId, page)).split("@")[1];
        List<Paragraph> paragraphs = paragraphRepository.findByFairytaleIdAndPage(fairytaleId, page);

        if (paragraphs.isEmpty()) {
            sseService.sendToClient(fairytaleId, "error", "문단 데이터 없음");
            log.warn("SSE 발송 실패 - 문단 없음 fairytaleId={}, page={}", fairytaleId, page);
            return;
        }

        Paragraph paragraph = paragraphs.getFirst();
        List<String> sentences = List.of(paragraph.getText().split("\n"));
        SseEventRes.Vocabulary vocab = wordEntry
                .map(w -> new SseEventRes.Vocabulary(w.getWord(), w.getMeaning()))
                .orElse(null);

        SseEventRes event = new SseEventRes(
                fairytaleId,
                page,
                sentences,
                vocab,
                paragraph.getImageUrl(),
                ttsUrl
        );

        log.info("[PAGE_CONTENT SEND] fairytaleId={}, page={}", fairytaleId, page);
        sseService.sendToClient(fairytaleId, "page_content", event);

        redisTemplate.delete(String.format(VOCAB_KEY, fairytaleId, page));
        redisTemplate.delete(String.format(IMAGE_KEY, fairytaleId, page));
        redisTemplate.delete(String.format(TTS_KEY, fairytaleId, page));

        Long sent = redisTemplate.opsForValue().increment(String.format(SENT_KEY, fairytaleId));
        log.info("[SENT COUNT] fairytaleId={}, sent={}", fairytaleId, sent);
        checkAndSendDone(fairytaleId, sent);
    }

    @Override
    public void markTotalPages(Long fairytaleId, int totalPages) {
        redisTemplate.opsForValue().set(String.format(TOTAL_KEY, fairytaleId), String.valueOf(totalPages));
        log.info("[TOTAL SET] fairytaleId={}, totalPages={}", fairytaleId, totalPages);
        String sentStr = redisTemplate.opsForValue().get(String.format(SENT_KEY, fairytaleId));
        long sent = sentStr == null ? 0L : Long.parseLong(sentStr);
        checkAndSendDone(fairytaleId, sent);
    }

    private void checkAndSendDone(Long fairytaleId, Long sent) {
        String totalStr = redisTemplate.opsForValue().get(String.format(TOTAL_KEY, fairytaleId));
        if (totalStr == null) return;

        if (sent >= Long.parseLong(totalStr)) {
            sseService.sendToClient(fairytaleId, "done", String.valueOf(fairytaleId));
            redisTemplate.delete(String.format(TOTAL_KEY, fairytaleId));
            redisTemplate.delete(String.format(SENT_KEY, fairytaleId));
            log.info("SSE done 전송 fairytaleId={}", fairytaleId);
        }
    }
}