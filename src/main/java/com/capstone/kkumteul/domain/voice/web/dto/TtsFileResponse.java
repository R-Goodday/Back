package com.capstone.kkumteul.domain.voice.web.dto;

import com.capstone.kkumteul.domain.kafka.dto.MessageInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TtsFileResponse implements MessageInterface {

    private final Long paragraphId;
    private final String ttsUrl;
}
