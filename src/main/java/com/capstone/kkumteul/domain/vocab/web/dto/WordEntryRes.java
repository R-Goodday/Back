package com.capstone.kkumteul.domain.vocab.web.dto;

import com.capstone.kkumteul.domain.vocab.entity.WordEntry;

import java.util.List;

public record WordEntryRes(
        Long wordEntryId,
        int pageNo,
        String word,
        String meaning
) {
    public static WordEntryRes from(WordEntry entry) {
        return new WordEntryRes(entry.getId(), entry.getPageNo(), entry.getWord(), entry.getMeaning());
    }

    public static List<WordEntryRes> listOf(List<WordEntry> entries) {
        return entries.stream().map(WordEntryRes::from).toList();
    }
}
