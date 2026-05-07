package com.capstone.kkumteul.domain.fairytale.web.dto;

import com.capstone.kkumteul.domain.fairytale.entity.Background;
import com.capstone.kkumteul.domain.fairytale.entity.CharSpecies;
import com.capstone.kkumteul.domain.fairytale.entity.Morality;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FairytaleGenerateReq {

    @NotNull(message = "배경은 비어있을 수 없습니다.")
    private final Background background;

    @NotNull(message = "등장인물 종류는 비어있을 수 없습니다.")
    private final CharSpecies charSpecies;

    @NotNull(message = "교훈은 비어있을 수 없습니다.")
    private final Morality morality;

}
