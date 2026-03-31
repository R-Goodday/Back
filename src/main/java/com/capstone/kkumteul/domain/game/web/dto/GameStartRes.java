package com.capstone.kkumteul.domain.game.web.dto;

import com.capstone.kkumteul.domain.game.entity.NodeCategory;
import com.capstone.kkumteul.domain.game.service.GameSession.SessionNode;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * POST /game/start 응답 DTO.
 * 세션 ID, 현재 단계("CLASSIFY"), 1단계 문제 데이터(노드 목록 + 카테고리 바구니 목록)를 반환.
 */
public record GameStartRes(
        String sessionId,
        String stage,
        ClassifyQuestionRes question
) {

    public static GameStartRes of(String sessionId, Collection<SessionNode> nodes) {
        List<NodeRes> nodeResList = nodes.stream()
                .map(NodeRes::from)
                .toList();
        List<String> categories = Arrays.stream(NodeCategory.values())
                .map(NodeCategory::getLabel)
                .toList();
        return new GameStartRes(sessionId, "CLASSIFY", new ClassifyQuestionRes(nodeResList, categories));
    }

    public record ClassifyQuestionRes(List<NodeRes> nodes, List<String> categories) {}
}
