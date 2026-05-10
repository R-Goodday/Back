package com.capstone.kkumteul.domain.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AI 서버가 발행하는 vocab_extracted 토픽의 Consumer-only 메시지.
 *
 * <p>설계상 {@code MessageInterface}를 구현하지 않는다 — Producer-side marker이고
 * 본 DTO는 Consumer 단에서만 사용한다. {@code userId}/{@code messageId}는 정보용 필드로,
 * 트레이싱/로그에만 쓰인다 (dedup 키로 사용 X).</p>
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VocabExtractedMessage {

    private Long fairytaleId;
    private int pageNo;
    private String word;
    private String meaning;
    private Long userId;
    private String messageId;
}
