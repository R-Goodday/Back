package com.capstone.kkumteul.domain.fairytale.web.dto;

import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;
import com.capstone.kkumteul.domain.vocab.web.dto.WordEntryRes;

import java.util.List;

public record FairytaleDetailRes(
        Long fairytaleId,
        String title,
        String authorName,
        String morality,
        String charSpecies,
        String background,
        List<ParagraphRes> paragraphs,
        List<WordEntryRes> vocab
) {
    public static FairytaleDetailRes of(Fairytale fairytale, List<ParagraphRes> paragraphs, List<WordEntryRes> vocab) {
        return new FairytaleDetailRes(
                fairytale.getId(),
                fairytale.getTitle(),
                fairytale.getUser().getUsername(),
                fairytale.getMorality().name(),
                fairytale.getCharSpecies().name(),
                fairytale.getBackground().name(),
                paragraphs,
                vocab
        );
    }
}
