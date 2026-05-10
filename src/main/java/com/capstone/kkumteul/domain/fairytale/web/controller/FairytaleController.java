package com.capstone.kkumteul.domain.fairytale.web.controller;

import com.capstone.kkumteul.domain.fairytale.entity.Island;
import com.capstone.kkumteul.domain.fairytale.service.FairytaleService;
import com.capstone.kkumteul.domain.fairytale.web.dto.FairytaleDetailRes;
import com.capstone.kkumteul.domain.fairytale.web.dto.FairytaleGenerateReq;
import com.capstone.kkumteul.domain.fairytale.web.dto.FairytaleListRes;
import com.capstone.kkumteul.domain.kafka.service.EventService;
import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.global.response.SuccessResponse;
import com.capstone.kkumteul.global.security.AuthUser;

import com.capstone.kkumteul.domain.fairytale.service.sse.SseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fairytales")
public class FairytaleController {

    private final FairytaleService fairytaleService;
    private final SseService sseService;
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<SuccessResponse<?>> createFairytale(
            @AuthUser User user,
            @Valid @RequestBody FairytaleGenerateReq request
    ) {
        Long fairytaleId = eventService.createFairytaleMessageSend(user, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponse.created(fairytaleId));
    }
  
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

    @GetMapping(value="/{fairytaleId}/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long fairytaleId){
        return sseService.subscribe(fairytaleId);
    }
}
