package com.capstone.kkumteul.global.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GraphExtractResponse {

    @JsonProperty("fairytale_id")
    private Long fairytaleId;

    private List<NodeDto> nodes;
    private List<EdgeDto> edges;

    @Getter
    @NoArgsConstructor
    public static class NodeDto {
        @JsonProperty("temp_id")
        private Integer tempId;
        private String word;
        private String category;
    }

    @Getter
    @NoArgsConstructor
    public static class EdgeDto {
        @JsonProperty("from_temp_id")
        private Integer fromTempId;
        @JsonProperty("to_temp_id")
        private Integer toTempId;
        private String description;
        private List<ChoiceDto> choices;
    }

    @Getter
    @NoArgsConstructor
    public static class ChoiceDto {
        private String content;
        @JsonProperty("is_answer")
        private Boolean isAnswer;
    }
}
