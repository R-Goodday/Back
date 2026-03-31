package com.capstone.kkumteul.domain.game.web.dto;

import com.capstone.kkumteul.domain.game.entity.GraphEdge;
import com.capstone.kkumteul.domain.game.entity.GraphNode;

import java.util.List;

public record GraphDetailRes(
        Long fairytaleId,
        List<NodeWithCategoryRes> nodes,
        List<EdgeRes> edges
) {

    public static GraphDetailRes of(Long fairytaleId, List<GraphNode> nodes, List<GraphEdge> edges) {
        List<NodeWithCategoryRes> nodeResList = nodes.stream()
                .map(n -> new NodeWithCategoryRes(n.getId(), n.getWord(), n.getCategory().getLabel()))
                .toList();
        List<EdgeRes> edgeResList = edges.stream()
                .map(e -> new EdgeRes(e.getId(), e.getFromNode().getId(), e.getToNode().getId()))
                .toList();
        return new GraphDetailRes(fairytaleId, nodeResList, edgeResList);
    }

    public record EdgeRes(Long edgeId, Long from, Long to) {}
}
