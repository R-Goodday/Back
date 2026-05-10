package com.capstone.kkumteul.domain.kafka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FairytaleCompletedMessage {
    private Long fairytaleId;
    @JsonProperty("total_pages")
    private int totalPages;
}