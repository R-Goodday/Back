package com.capstone.kkumteul.domain.kafka.dto;

import com.capstone.kkumteul.domain.fairytale.entity.Background;
import com.capstone.kkumteul.domain.fairytale.entity.CharSpecies;
import com.capstone.kkumteul.domain.fairytale.entity.Morality;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FairytaleGenerateMessage implements MessageInterface {

     private final Long userId;

     private final Background background;
     private final CharSpecies charSpecies;
     private final Morality morality;
}
