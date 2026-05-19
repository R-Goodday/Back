package com.capstone.kkumteul.domain.fairytale.web.dto;

import com.capstone.kkumteul.domain.fairytale.entity.Paragraph;

import java.util.List;

public record ParagraphRes(
        int page,
        List<String> sentences,
        String imageUrl,
        String ttsUrl
) {
    public static ParagraphRes from(Paragraph paragraph, String ttsUrl) {
        return new ParagraphRes(
                paragraph.getPage(),
                List.of(paragraph.getText().split("\n")),
                paragraph.getImageUrl(),
                ttsUrl
        );
    }
}
