package com.capstone.kkumteul.domain.fairytale.service;

import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;
import com.capstone.kkumteul.domain.fairytale.entity.Island;
import com.capstone.kkumteul.domain.fairytale.exception.FairytaleNotFoundException;
import com.capstone.kkumteul.domain.fairytale.repository.FairytaleRepository;
import com.capstone.kkumteul.domain.fairytale.repository.ParagraphRepository;
import com.capstone.kkumteul.domain.fairytale.web.dto.FairytaleDetailRes;
import com.capstone.kkumteul.domain.fairytale.web.dto.FairytaleListRes;
import com.capstone.kkumteul.domain.fairytale.web.dto.ParagraphRes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FairytaleServiceImpl implements FairytaleService {

    private final FairytaleRepository fairytaleRepository;
    private final ParagraphRepository paragraphRepository;

    @Override
    public Page<FairytaleListRes> getMyFairytales(Long userId, Island island, Pageable pageable) {
        return fairytaleRepository.findByUserIdAndBackgroundIn(userId, island.getBackgrounds(), pageable)
                .map(FairytaleListRes::from);
    }

    @Override
    public Page<FairytaleListRes> getSharedFairytales(Long userId, Island island, Pageable pageable) {
        return fairytaleRepository.findByUserIdNotAndBackgroundIn(userId, island.getBackgrounds(), pageable)
                .map(FairytaleListRes::from);
    }

    @Override
    public FairytaleDetailRes getFairytaleDetail(Long fairytaleId) {
        Fairytale fairytale = fairytaleRepository.findByIdWithUser(fairytaleId)
                .orElseThrow(FairytaleNotFoundException::new);

        List<ParagraphRes> paragraphs = paragraphRepository.findByFairytaleIdOrderByPageAsc(fairytaleId)
                .stream()
                .map(ParagraphRes::from)
                .toList();

        return FairytaleDetailRes.of(fairytale, paragraphs);
    }
}
