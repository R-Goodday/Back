package com.capstone.kkumteul.domain.vocab.web.controller;

import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.domain.vocab.entity.WordEntry;
import com.capstone.kkumteul.domain.vocab.service.VocabService;
import com.capstone.kkumteul.domain.vocab.web.dto.WordEntryRes;
import com.capstone.kkumteul.global.response.SuccessResponse;
import com.capstone.kkumteul.global.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fairytales")
public class VocabController {

    private final VocabService vocabService;

    /** 본인 동화의 누적 단어장 조회 (페이지 순). */
    @GetMapping("/{fairytaleId}/vocab")
    public ResponseEntity<SuccessResponse<List<WordEntryRes>>> getVocab(
            @AuthUser User user,
            @PathVariable Long fairytaleId
    ) {
        List<WordEntry> entries = vocabService.getVocab(user.getId(), fairytaleId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.ok(WordEntryRes.listOf(entries)));
    }
}
