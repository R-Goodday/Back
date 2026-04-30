package com.capstone.kkumteul.global.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VocabExtractResponse {

    /** LLM이 선택한 어려운 단어. 없으면 null 또는 빈 문자열. */
    private String word;

    /** 유아 눈높이 풀이. */
    private String meaning;
}
