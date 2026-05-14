package com.capstone.kkumteul.domain.vocab.service.dto;

import com.capstone.kkumteul.domain.vocab.entity.WordEntry;

/**
 * VocabService 처리 결과를 나타내는 service-layer 값 객체.
 *
 * <p>web/dto, kafka/message 패키지에 의존하지 않는다.
 * Controller나 KafkaListener는 이 결과를 자기 layer DTO로 변환해 사용한다.</p>
 *
 * @param status 처리 결과 상태
 * @param entry  성공 시 저장된 엔티티, 그 외엔 null
 */
public record VocabExtractionResult(Status status, WordEntry entry) {

    public enum Status {
        /** 새 단어가 추출되어 정상 저장됨 */
        SAVED,
        /** 추출됐지만 같은 단어가 이미 단어장에 존재함 (first-occurrence-wins) */
        DUPLICATE,
        /** LLM이 어려운 단어를 찾지 못함 (해당 페이지에 어려운 단어 없음) */
        NO_DIFFICULT_WORD,
        /** LLM 호출 실패 또는 응답 파싱 실패 */
        EXTRACTION_FAILED,
        /** 동시 INSERT race condition으로 인해 다른 트랜잭션이 먼저 저장 */
        RACE_SKIPPED
    }

    public static VocabExtractionResult saved(WordEntry entry) {
        return new VocabExtractionResult(Status.SAVED, entry);
    }

    public static VocabExtractionResult duplicate() {
        return new VocabExtractionResult(Status.DUPLICATE, null);
    }

    public static VocabExtractionResult noDifficultWord() {
        return new VocabExtractionResult(Status.NO_DIFFICULT_WORD, null);
    }

    public static VocabExtractionResult extractionFailed() {
        return new VocabExtractionResult(Status.EXTRACTION_FAILED, null);
    }

    public static VocabExtractionResult raceSkipped() {
        return new VocabExtractionResult(Status.RACE_SKIPPED, null);
    }
}
