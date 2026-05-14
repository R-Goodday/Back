package com.capstone.kkumteul.domain.fairytale.validator;

import com.capstone.kkumteul.domain.fairytale.repository.ParagraphRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParagraphValidator {

    private final ParagraphRepository paragraphRepository;

    public boolean paragraphIdIsValid(Long paragraphId) {
        return paragraphRepository.existsById(paragraphId);
    }

    public boolean fairytaleIdIsValid(Long fairytaleId) {
        return paragraphRepository.existsByFairytaleId(fairytaleId);
    }
}
