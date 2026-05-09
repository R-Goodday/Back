package com.capstone.kkumteul.domain.vocab.service;

import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;
import com.capstone.kkumteul.domain.fairytale.exception.FairytaleNotFoundException;
import com.capstone.kkumteul.domain.fairytale.service.FairytaleCheckService;
import com.capstone.kkumteul.domain.vocab.entity.WordEntry;
import com.capstone.kkumteul.domain.vocab.exception.VocabForbiddenException;
import com.capstone.kkumteul.domain.vocab.repository.WordEntryRepository;
import com.capstone.kkumteul.domain.vocab.service.dto.VocabExtractionResult;
import com.capstone.kkumteul.global.client.VocabExtractClient;
import com.capstone.kkumteul.global.client.dto.VocabExtractResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VocabServiceImpl implements VocabService {

    private final WordEntryRepository wordEntryRepository;
    private final VocabExtractClient vocabExtractClient;
    private final EntityManager entityManager;
    private final FairytaleCheckService fairytaleCheckService;

    /**
     * 페이지 3문장 → LLM으로 단어 추출 → 풀이 생성 → DB 저장.
     *
     * <p>처리 흐름:</p>
     * <ol>
     *   <li>FastAPI 호출 → 단어/풀이 1회에 추출</li>
     *   <li>응답 비어있으면 NO_DIFFICULT_WORD</li>
     *   <li>실패하면 EXTRACTION_FAILED (예외 전파 X, fail-open)</li>
     *   <li>이미 단어장에 있으면 DUPLICATE (first-occurrence-wins)</li>
     *   <li>저장 시도 → race condition으로 UNIQUE 위반이면 RACE_SKIPPED</li>
     *   <li>정상 저장이면 SAVED</li>
     * </ol>
     */
    @Override
    @Transactional
    public VocabExtractionResult processSentences(Long fairytaleId, int pageNo, List<String> sentences) {
        Optional<VocabExtractResponse> extracted = vocabExtractClient.extract(sentences);
        if (extracted.isEmpty()) {
            return VocabExtractionResult.extractionFailed();
        }

        VocabExtractResponse response = extracted.get();
        String word = response.getWord();
        String meaning = response.getMeaning();
        if (word == null || word.isBlank() || meaning == null || meaning.isBlank()) {
            return VocabExtractionResult.noDifficultWord();
        }

        if (wordEntryRepository.existsByFairytaleIdAndWord(fairytaleId, word)) {
            return VocabExtractionResult.duplicate();
        }

        Fairytale fairytale = entityManager.getReference(Fairytale.class, fairytaleId);
        WordEntry entry = WordEntry.builder()
                .fairytale(fairytale)
                .pageNo(pageNo)
                .word(word)
                .meaning(meaning)
                .build();

        try {
            WordEntry saved = wordEntryRepository.save(entry);
            fairytaleCheckService.markVocabDone(fairytaleId, pageNo);
            return VocabExtractionResult.saved(saved);
        } catch (DataIntegrityViolationException e) {
            log.info("vocab race condition fairytaleId={}, word={}", fairytaleId, word);
            return VocabExtractionResult.raceSkipped();
        }
    }

    /**
     * 본인 동화 누적 단어장 조회.
     * 동화 소유권 검증 후 페이지 순서로 반환.
     */
    @Override
    public List<WordEntry> getVocab(Long userId, Long fairytaleId) {
        Fairytale fairytale = entityManager.find(Fairytale.class, fairytaleId);
        if (fairytale == null) {
            throw new FairytaleNotFoundException();
        }
        Objects.requireNonNull(fairytale.getUser(), "Fairytale.user는 null이 될 수 없음");
        if (!fairytale.getUser().getId().equals(userId)) {
            throw new VocabForbiddenException();
        }
        return wordEntryRepository.findByFairytaleIdOrderByPageNoAsc(fairytaleId);
    }
}
