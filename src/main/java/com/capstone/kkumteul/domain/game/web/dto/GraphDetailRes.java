package com.capstone.kkumteul.domain.game.web.dto;

import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;
import com.capstone.kkumteul.domain.game.entity.GraphEdge;
import com.capstone.kkumteul.domain.game.entity.GraphNode;

import java.util.List;

/**
 * GET /game/graph 응답 DTO.
 * 동화 모음집에서 완성된 관계도 전체(노드+엣지)를 조회할 때 사용.
 */
public record GraphDetailRes(
        Long fairytaleId,
        String selectedCharSpecies,
        String selectedBackground,
        List<NodeWithCategoryRes> nodes,
        List<EdgeRes> edges
) {

    public static GraphDetailRes of(Fairytale fairytale, List<GraphNode> nodes, List<GraphEdge> edges) {
        List<NodeWithCategoryRes> nodeResList = nodes.stream()
                .map(n -> new NodeWithCategoryRes(n.getId(), n.getWord(), n.getCategory().getLabel()))
                .toList();
        List<EdgeRes> edgeResList = edges.stream()
                .map(e -> new EdgeRes(e.getId(), e.getFromNode().getId(), e.getToNode().getId()))
                .toList();
        return new GraphDetailRes(
                fairytale.getId(),
                fairytale.getCharSpecies().name(),
                fairytale.getBackground().name(),
                nodeResList,
                edgeResList
        );
    }

    public record EdgeRes(Long edgeId, Long from, Long to) {}
}
