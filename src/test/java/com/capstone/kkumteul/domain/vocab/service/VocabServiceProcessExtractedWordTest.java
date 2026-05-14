package com.capstone.kkumteul.domain.vocab.service;

import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;
import com.capstone.kkumteul.domain.fairytale.repository.FairytaleRepository;
import com.capstone.kkumteul.domain.fairytale.service.FairytaleCheckService;
import com.capstone.kkumteul.domain.kafka.dto.VocabExtractedMessage;
import com.capstone.kkumteul.domain.vocab.entity.WordEntry;
import com.capstone.kkumteul.domain.vocab.repository.WordEntryRepository;
import com.capstone.kkumteul.domain.vocab.service.dto.VocabExtractionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * VocabServiceImpl#processExtractedWord 단위 테스트.
 * 모든 종착 분기에서 markVocabDone이 호출되는지(SSE guarantee) 검증.
 */
@ExtendWith(MockitoExtension.class)
class VocabServiceProcessExtractedWordTest {

    @Mock private WordEntryRepository wordEntryRepository;
    @Mock private FairytaleRepository fairytaleRepository;
    @Mock private FairytaleCheckService fairytaleCheckService;
    @Mock private com.capstone.kkumteul.global.client.VocabExtractClient vocabExtractClient;

    @InjectMocks private VocabServiceImpl vocabService;

    private static final Long FAIRYTALE_ID = 10L;
    private static final int PAGE_NO = 1;

    @BeforeEach
    void setUp() {
    }

    private VocabExtractedMessage message(String word, String meaning) {
        return VocabExtractedMessage.builder()
                .fairytaleId(FAIRYTALE_ID)
                .pageNo(PAGE_NO)
                .word(word)
                .meaning(meaning)
                .userId(42L)
                .messageId("msg-1")
                .build();
    }

    @Test
    @DisplayName("word=null이면 NO_DIFFICULT_WORD 반환 + DB 저장 없음 + markVocabDone 호출")
    void noDifficultWordWhenWordNull() {
        VocabExtractionResult result = vocabService.processExtractedWord(message(null, null));

        assertThat(result.status()).isEqualTo(VocabExtractionResult.Status.NO_DIFFICULT_WORD);
        verify(wordEntryRepository, never()).save(any());
        verify(fairytaleCheckService, times(1)).markVocabDone(FAIRYTALE_ID, PAGE_NO);
    }

    @Test
    @DisplayName("word=blank이면 NO_DIFFICULT_WORD")
    void noDifficultWordWhenWordBlank() {
        VocabExtractionResult result = vocabService.processExtractedWord(message("   ", "meaning"));

        assertThat(result.status()).isEqualTo(VocabExtractionResult.Status.NO_DIFFICULT_WORD);
        verify(wordEntryRepository, never()).save(any());
        verify(fairytaleCheckService, times(1)).markVocabDone(FAIRYTALE_ID, PAGE_NO);
    }

    @Test
    @DisplayName("이미 같은 fairytaleId+word가 있으면 DUPLICATE + markVocabDone 호출")
    void duplicateWhenWordExists() {
        when(wordEntryRepository.existsByFairytaleIdAndWord(FAIRYTALE_ID, "용감")).thenReturn(true);

        VocabExtractionResult result = vocabService.processExtractedWord(message("용감", "..."));

        assertThat(result.status()).isEqualTo(VocabExtractionResult.Status.DUPLICATE);
        verify(wordEntryRepository, never()).save(any());
        verify(fairytaleCheckService, times(1)).markVocabDone(FAIRYTALE_ID, PAGE_NO);
    }

    @Test
    @DisplayName("정상 저장이면 SAVED + markVocabDone 호출")
    void savedWhenNew() {
        when(wordEntryRepository.existsByFairytaleIdAndWord(FAIRYTALE_ID, "용감")).thenReturn(false);
        when(fairytaleRepository.findById(FAIRYTALE_ID)).thenReturn(Optional.of(mockFairytale()));
        when(wordEntryRepository.save(any(WordEntry.class))).thenAnswer(inv -> inv.getArgument(0));

        VocabExtractionResult result = vocabService.processExtractedWord(message("용감", "씩씩한 마음"));

        assertThat(result.status()).isEqualTo(VocabExtractionResult.Status.SAVED);
        verify(wordEntryRepository, times(1)).save(any(WordEntry.class));
        verify(fairytaleCheckService, times(1)).markVocabDone(FAIRYTALE_ID, PAGE_NO);
    }

    @Test
    @DisplayName("save 시 DataIntegrityViolationException → RACE_SKIPPED + markVocabDone 호출 (Phase1과 의도적 divergence)")
    void raceSkippedWhenUniqueViolation() {
        when(wordEntryRepository.existsByFairytaleIdAndWord(FAIRYTALE_ID, "용감")).thenReturn(false);
        when(fairytaleRepository.findById(FAIRYTALE_ID)).thenReturn(Optional.of(mockFairytale()));
        when(wordEntryRepository.save(any(WordEntry.class)))
                .thenThrow(new DataIntegrityViolationException("UNIQUE violated"));

        VocabExtractionResult result = vocabService.processExtractedWord(message("용감", "..."));

        assertThat(result.status()).isEqualTo(VocabExtractionResult.Status.RACE_SKIPPED);
        verify(fairytaleCheckService, times(1)).markVocabDone(FAIRYTALE_ID, PAGE_NO);
    }

    private Fairytale mockFairytale() {
        return Fairytale.builder().id(FAIRYTALE_ID).build();
    }
}
