package com.capstone.kkumteul.domain.fairytale.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FairytaleCheckServiceImpl implements FairytaleCheckService{

    private final RedisTemplate<String, String> redisTemplate;

    private static final String VOCAB_KEY="vocab:%d:%d";
    private static final String IMAGE_KEY="image:%d:%d";
    private static final String DONE = "done";

    @Override
    public void markVocabDone(Long fairytaleId, int page) {
        redisTemplate.opsForValue().set(String.format(VOCAB_KEY, fairytaleId, page), DONE);
        if (isBothDone(fairytaleId, page)) {
            // SSE 이벤트
        }
    }

    @Override
    public void markImageDone(Long fairytaleId, int page) {
        redisTemplate.opsForValue().set(String.format(IMAGE_KEY, fairytaleId, page), DONE);
        if (isBothDone(fairytaleId, page)) {
            // SSE 이벤트
        }
    }

    @Override
    public boolean isBothDone(Long fairytaleId, int page) {
        String vocabStatus = redisTemplate.opsForValue().get(String.format(VOCAB_KEY, fairytaleId, page));
        String imageStatus = redisTemplate.opsForValue().get(String.format(IMAGE_KEY, fairytaleId, page));
        return DONE.equals(vocabStatus) && DONE.equals(imageStatus);
    }
}
