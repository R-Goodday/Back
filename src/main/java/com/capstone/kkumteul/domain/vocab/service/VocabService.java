package com.capstone.kkumteul.domain.vocab.service;

import com.capstone.kkumteul.domain.kafka.dto.VocabExtractedMessage;
import com.capstone.kkumteul.domain.vocab.service.dto.VocabExtractionResult;

import java.util.List;

/**
 * 단어장 추출 서비스. 조회는 동화 상세(FairytaleService)에 포함됨.
 */
public interface VocabService {

    /**
     * 페이지(3문장 단위)에서 어려운 단어 1개를 추출하고 누적 단어장에 저장.
     *
     * @param fairytaleId 동화 ID
     * @param pageNo      페이지 번호 (1-base)
     * @param sentences   해당 페이지의 문장들 (보통 3개)
     * @return 처리 결과 (저장됨 / 중복 / 단어 없음 / 추출 실패 / race skip)
     */
    VocabExtractionResult processSentences(Long fairytaleId, int pageNo, List<String> sentences);

    /**
     * AI 서버가 vocab_extracted 토픽으로 발행한 메시지를 처리.
     * word가 null/blank이면 NO_DIFFICULT_WORD 처리. 모든 종착 분기에서 markVocabDone 호출 (SSE guarantee).
     */
    VocabExtractionResult processExtractedWord(VocabExtractedMessage message);
}
