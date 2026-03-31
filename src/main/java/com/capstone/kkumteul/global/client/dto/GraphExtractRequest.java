package com.capstone.kkumteul.global.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GraphExtractRequest {

    @JsonProperty("fairytale_id")
    private Long fairytaleId;

    private String content;
}
