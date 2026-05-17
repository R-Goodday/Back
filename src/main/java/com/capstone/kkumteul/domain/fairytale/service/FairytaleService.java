package com.capstone.kkumteul.domain.fairytale.service;

import com.capstone.kkumteul.domain.fairytale.entity.Island;
import com.capstone.kkumteul.domain.fairytale.web.dto.FairytaleDetailRes;
import com.capstone.kkumteul.domain.fairytale.web.dto.FairytaleListRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FairytaleService {

    Page<FairytaleListRes> getMyFairytales(Long userId, Island island, Pageable pageable);

    Page<FairytaleListRes> getSharedFairytales(Long userId, Pageable pageable);

    FairytaleDetailRes getFairytaleDetail(Long fairytaleId);
}
