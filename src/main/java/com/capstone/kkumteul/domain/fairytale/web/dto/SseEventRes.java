package com.capstone.kkumteul.domain.fairytale.web.dto;

import java.util.List;

public record SseEventRes(
        Long fairytaleId,
        int pageNo,
        List<String> text,
        Vocabulary vocab,
        String imageUrl
) { public record Vocabulary(
        String word,
        String meaning
){}
}
