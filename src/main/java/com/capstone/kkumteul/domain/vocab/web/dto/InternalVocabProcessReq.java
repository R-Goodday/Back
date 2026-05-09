package com.capstone.kkumteul.domain.vocab.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class InternalVocabProcessReq {

    @NotNull
    private Long fairytaleId;

    @NotNull
    private Integer pageNo;
}
