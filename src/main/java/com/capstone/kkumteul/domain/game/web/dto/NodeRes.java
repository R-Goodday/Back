package com.capstone.kkumteul.domain.game.web.dto;

import com.capstone.kkumteul.domain.game.service.GameSession.SessionNode;

/**
 * 노드 기본 응답 DTO — 카테고리 없이 id와 단어만 반환.
 * 1단계(바구니 분류)에서 노드 목록 표시용. 카테고리는 정답이므로 노출하지 않음.
 */
public record NodeRes(Long id, String word) {

    public static NodeRes from(SessionNode node) {
        return new NodeRes(node.getId(), node.getWord());
    }
}
