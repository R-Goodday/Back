package com.capstone.kkumteul.domain.game.web.dto;

import com.capstone.kkumteul.domain.game.service.GameSession.SessionNode;

/**
 * 노드 + 카테고리 응답 DTO — 1단계 완료 후 2단계 진입 시 사용.
 * category는 한글 라벨("등장인물", "장소" 등)로 변환되어 전달됨.
 */
public record NodeWithCategoryRes(Long id, String word, String category) {

    public static NodeWithCategoryRes from(SessionNode node) {
        return new NodeWithCategoryRes(node.getId(), node.getWord(), node.getCategory().getLabel());
    }
}
