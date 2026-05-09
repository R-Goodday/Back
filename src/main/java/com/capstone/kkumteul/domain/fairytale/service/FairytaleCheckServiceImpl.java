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

        if (wordEntry.isEmpty() || paragraphs.isEmpty()) {
            log.warn("SSE 발송 실패 - 데이터 없음 fairytaleId={}, page={}", fairytaleId, page);
            return;
        }

        WordEntry word = wordEntry.get();

        List<String> sentences = paragraphs.stream()
                .map(Paragraph::getText)
                .toList();

        SseEventRes event = new SseEventRes(
                fairytaleId,
                page,
                sentences,
                new SseEventRes.Vocabulary(word.getWord(), word.getMeaning()),
                paragraphs.getFirst().getImageUrl()
        );

        sseService.sendToClient(fairytaleId, "page_content", event);

        redisTemplate.delete(String.format(VOCAB_KEY, fairytaleId, page));
        redisTemplate.delete(String.format(IMAGE_KEY, fairytaleId, page));
    }
}