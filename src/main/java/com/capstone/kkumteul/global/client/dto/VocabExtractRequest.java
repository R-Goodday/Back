package com.capstone.kkumteul.global.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class VocabExtractRequest {

    private List<String> sentences;
}
