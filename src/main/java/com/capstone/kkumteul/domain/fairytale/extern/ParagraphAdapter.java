package com.capstone.kkumteul.domain.fairytale.extern;

import com.capstone.kkumteul.domain.fairytale.exception.ParagraphNotFoundException;
import com.capstone.kkumteul.domain.fairytale.repository.ParagraphRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParagraphAdapter implements ParagraphPort {

    private final ParagraphRepository paragraphRepository;

    @Override
    public int getPageNoByParagraphId(Long paragraphId) {
        return paragraphRepository.findById(paragraphId)
                .orElseThrow(ParagraphNotFoundException::new).getPage();
    }
}
