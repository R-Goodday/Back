package com.capstone.kkumteul.domain.game.web.dto;

import com.capstone.kkumteul.domain.game.service.GameSession.SessionNode;

public record NodeRes(Long id, String word) {

    public static NodeRes from(SessionNode node) {
        return new NodeRes(node.getId(), node.getWord());
    }
}
