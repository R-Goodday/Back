package com.capstone.kkumteul.domain.fairytale.voice.web.dto;

import com.capstone.kkumteul.domain.kafka.dto.MessageInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TtsModelingRequest implements MessageInterface {
    private final Long userId;
    private final String uploadedUrl;
}
