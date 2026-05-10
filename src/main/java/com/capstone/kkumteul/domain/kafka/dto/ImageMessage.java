package com.capstone.kkumteul.domain.kafka.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ImageMessage {
    private Long fairytaleId;
    private int pageNo;
    private String imageurl;
}