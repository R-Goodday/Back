package com.capstone.kkumteul.domain.fairytale.web.dto;

import com.capstone.kkumteul.domain.fairytale.entity.Paragraph;

public record ParagraphRes(
        int page,
        String text
) {
    public static ParagraphRes from(Paragraph paragraph) {
        return new ParagraphRes(
                paragraph.getPage(),
                paragraph.getText()
        );
    }
}
