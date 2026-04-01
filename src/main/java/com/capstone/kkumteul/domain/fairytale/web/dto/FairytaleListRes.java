package com.capstone.kkumteul.domain.fairytale.web.dto;

import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;

public record FairytaleListRes(
        Long fairytaleId,
        String title,
        String authorName
) {
    public static FairytaleListRes from(Fairytale fairytale) {
        return new FairytaleListRes(
                fairytale.getId(),
                fairytale.getTitle(),
                fairytale.getUser().getUsername()
        );
    }
}
