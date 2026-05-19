package com.capstone.kkumteul.domain.kafka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TtsFileDoneMessage {
    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("fairytaleId")
    private Long fairytaleId;

    @JsonProperty("paragraphId")
    private Long paragraphId;

    @JsonProperty("ttsHistoryId")
    private Long ttsHistoryId;

    @JsonProperty("ttsUrl")
    private String ttsUrl;
}
