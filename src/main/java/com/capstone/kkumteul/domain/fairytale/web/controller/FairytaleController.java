package com.capstone.kkumteul.domain.fairytale.web.controller;

import com.capstone.kkumteul.domain.fairytale.entity.Island;
import com.capstone.kkumteul.domain.fairytale.service.FairytaleService;
import com.capstone.kkumteul.domain.fairytale.web.dto.FairytaleDetailRes;
import com.capstone.kkumteul.domain.fairytale.web.dto.FairytaleListRes;
import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.global.response.SuccessResponse;
import com.capstone.kkumteul.global.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fairytales")
public class FairytaleController {

    private final FairytaleService fairytaleService;

    @GetMapping("/my")
    public ResponseEntity<SuccessResponse<Page<FairytaleListRes>>> getMyFairytales(
            @AuthUser User user,
            @RequestParam Island island,
            @PageableDefault(size = 6) Pageable pageable
    ) {
        Page<FairytaleListRes> res = fairytaleService.getMyFairytales(user.getId(), island, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.ok(res));
    }

    @GetMapping("/shared")
    public ResponseEntity<SuccessResponse<Page<FairytaleListRes>>> getSharedFairytales(
            @AuthUser User user,
            @RequestParam Island island,
            @PageableDefault(size = 6) Pageable pageable
    ) {
        Page<FairytaleListRes> res = fairytaleService.getSharedFairytales(user.getId(), island, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.ok(res));
    }

    @GetMapping("/{fairytaleId}")
    public ResponseEntity<SuccessResponse<FairytaleDetailRes>> getFairytaleDetail(
            @PathVariable Long fairytaleId
    ) {
        FairytaleDetailRes res = fairytaleService.getFairytaleDetail(fairytaleId);
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.ok(res));
    }
}
