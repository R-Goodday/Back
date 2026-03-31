package com.capstone.kkumteul.domain.game.web.dto;

import com.capstone.kkumteul.domain.game.service.GameSession.SessionNode;

public record NodeWithCategoryRes(Long id, String word, String category) {

    public static NodeWithCategoryRes from(SessionNode node) {
        return new NodeWithCategoryRes(node.getId(), node.getWord(), node.getCategory().getLabel());
    }
}
