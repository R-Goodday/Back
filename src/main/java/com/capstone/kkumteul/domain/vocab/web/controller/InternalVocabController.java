package com.capstone.kkumteul.domain.vocab.web.controller;

import com.capstone.kkumteul.domain.fairytale.entity.Paragraph;
import com.capstone.kkumteul.domain.fairytale.repository.ParagraphRepository;
import com.capstone.kkumteul.domain.vocab.exception.ParagraphNotFoundForVocabException;
import com.capstone.kkumteul.domain.vocab.service.VocabService;
import com.capstone.kkumteul.domain.vocab.service.dto.VocabExtractionResult;
import com.capstone.kkumteul.domain.vocab.web.dto.InternalVocabProcessReq;
import com.capstone.kkumteul.domain.vocab.web.dto.WordEntryRes;
import com.capstone.kkumteul.global.response.SuccessResponse;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * dev 프로필 한정 — Kafka 없이 단어장 추출 비즈니스 로직을 단독 호출하기 위한 시연/테스트용 API.
 *
 * <p>운영 환경에서는 {@link InternalApiSecurityConfig}와 함께 등록되지 않으므로 노출되지 않는다.</p>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/vocab")
@Profile("dev")
public class InternalVocabController {

    private final ParagraphRepository paragraphRepository;
    private final VocabService vocabService;

    @PostConstruct
    public void announce() {
        log.info("[DEV] /internal/vocab/process registered (dev profile active)");
    }

    @PostMapping("/process")
    public ResponseEntity<SuccessResponse<ProcessRes>> process(
            @Valid @RequestBody InternalVocabProcessReq req
    ) {
        List<Paragraph> paragraphs = paragraphRepository.findByFairytaleIdAndPage(
                req.getFairytaleId(), req.getPageNo()
        );
        if (paragraphs.isEmpty()) {
            throw new ParagraphNotFoundForVocabException();
        }
        List<String> sentences = paragraphs.stream().map(Paragraph::getText).toList();

        VocabExtractionResult result = vocabService.processSentences(
                req.getFairytaleId(), req.getPageNo(), sentences
        );

        WordEntryRes wordRes = result.entry() == null ? null : WordEntryRes.from(result.entry());
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.ok(new ProcessRes(result.status().name(), wordRes)));
    }

    public record ProcessRes(String status, WordEntryRes word) {
    }
}
