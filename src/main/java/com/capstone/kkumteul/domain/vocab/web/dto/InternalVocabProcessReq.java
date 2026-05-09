package com.capstone.kkumteul.domain.vocab.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class InternalVocabProcessReq {

    @NotNull
    private Long fairytaleId;

    @NotNull
    @Min(1)
    private Integer pageNo;
}
