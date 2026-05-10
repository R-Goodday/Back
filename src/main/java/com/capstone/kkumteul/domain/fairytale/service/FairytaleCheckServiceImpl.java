package com.capstone.kkumteul.domain.fairytale.service;

import com.capstone.kkumteul.domain.fairytale.entity.Paragraph;
import com.capstone.kkumteul.domain.fairytale.repository.ParagraphRepository;
import com.capstone.kkumteul.domain.fairytale.service.sse.SseService;
import com.capstone.kkumteul.domain.fairytale.web.dto.SseEventRes;
import com.capstone.kkumteul.domain.vocab.entity.WordEntry;
import com.capstone.kkumteul.domain.vocab.repository.WordEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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

    private static final String VOCAB_KEY = "vocab:%d:%d";
    private static final String IMAGE_KEY = "image:%d:%d";
    private static final String DONE = "done";

    @Override
    public void markVocabDone(Long fairytaleId, int page) {
        redisTemplate.opsForValue().set(String.format(VOCAB_KEY, fairytaleId, page), DONE);
        checkAndSend(fairytaleId, page);
    }

    @Override
    public void markImageDone(Long fairytaleId, int page) {
        redisTemplate.opsForValue().set(String.format(IMAGE_KEY, fairytaleId, page), DONE);
        checkAndSend(fairytaleId, page);
    }

    @Override
    public boolean isBothDone(Long fairytaleId, int page) {
        String vocabStatus = redisTemplate.opsForValue().get(String.format(VOCAB_KEY, fairytaleId, page));
        String imageStatus = redisTemplate.opsForValue().get(String.format(IMAGE_KEY, fairytaleId, page));
        return DONE.equals(vocabStatus) && DONE.equals(imageStatus);
    }

    //sse전송
    private void checkAndSend(Long fairytaleId, int page) {
        if (!isBothDone(fairytaleId, page)) return;

        Optional<WordEntry> wordEntry = wordEntryRepository.findByFairytaleIdAndPageNo(fairytaleId, page);
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
                paragraph.getImageUrl()
        );

        sseService.sendToClient(fairytaleId, "page_content", event);

        redisTemplate.delete(String.format(VOCAB_KEY, fairytaleId, page));
        redisTemplate.delete(String.format(IMAGE_KEY, fairytaleId, page));
    }
}