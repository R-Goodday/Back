package com.capstone.kkumteul.domain.game.service;

import com.capstone.kkumteul.domain.game.entity.GraphEdge;
import com.capstone.kkumteul.domain.game.entity.GraphNode;
import com.capstone.kkumteul.domain.game.entity.NodeCategory;
import com.capstone.kkumteul.domain.game.exception.GraphNotFoundException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameSession {

    private final String sessionId;
    private final Long userId;
    private final Long fairytaleId;
    private final Map<Long, SessionNode> nodeMap;
    private final List<SessionEdge> edges;
    private final Map<Long, SessionEdge> edgeMap;
    private final Set<Long> classifiedNodeIds;
    private final Set<Long> completedEdgeIds;
    private final Map<String, Long> quizEdgeMap;
    private LocalDateTime lastActivity;

    public GameSession(Long userId, Long fairytaleId, List<GraphNode> nodes, List<GraphEdge> edges) {
        if (nodes == null || nodes.isEmpty()) {
            throw new GraphNotFoundException();
        }
        this.sessionId = UUID.randomUUID().toString();
        this.userId = userId;
        this.fairytaleId = fairytaleId;
        this.nodeMap = new LinkedHashMap<>();
        for (GraphNode node : nodes) {
            this.nodeMap.put(node.getId(), SessionNode.from(node));
        }
        this.edges = edges.stream().map(SessionEdge::from).toList();
        this.edgeMap = new HashMap<>();
        for (SessionEdge edge : this.edges) {
            this.edgeMap.put(edge.getId(), edge);
        }
        this.classifiedNodeIds = ConcurrentHashMap.newKeySet();
        this.completedEdgeIds = ConcurrentHashMap.newKeySet();
        this.quizEdgeMap = new ConcurrentHashMap<>();
        this.lastActivity = LocalDateTime.now();
    }

    public String getSessionId() {
        return sessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getFairytaleId() {
        return fairytaleId;
    }

    public Collection<SessionNode> getNodes() {
        return Collections.unmodifiableCollection(nodeMap.values());
    }

    public List<SessionEdge> getEdges() {
        return edges;
    }

    public int getTotalEdges() {
        return edges.size();
    }

    public SessionEdge findEdge(Long edgeId) {
        return edgeMap.get(edgeId);
    }

    public void touch() {
        this.lastActivity = LocalDateTime.now();
    }

    public boolean isExpired(long ttlMinutes) {
        return lastActivity.plusMinutes(ttlMinutes).isBefore(LocalDateTime.now());
    }

    public boolean checkClassify(Long nodeId, NodeCategory category) {
        SessionNode node = nodeMap.get(nodeId);
        if (node == null) {
            return false;
        }
        if (node.getCategory() == category) {
            classifiedNodeIds.add(nodeId);
            return true;
        }
        return false;
    }

    public boolean isAlreadyClassified(Long nodeId) {
        return classifiedNodeIds.contains(nodeId);
    }

    public boolean isClassifyComplete() {
        return classifiedNodeIds.size() >= nodeMap.size();
    }

    public void markEdgeCompleted(Long edgeId) {
        completedEdgeIds.add(edgeId);
    }

    public boolean isEdgeCompleted(Long edgeId) {
        return completedEdgeIds.contains(edgeId);
    }

    public boolean isAssembleComplete() {
        return completedEdgeIds.size() >= edges.size();
    }

    public String registerQuiz(Long edgeId) {
        String quizId = UUID.randomUUID().toString().substring(0, 8);
        quizEdgeMap.put(quizId, edgeId);
        return quizId;
    }

    public Long getEdgeIdByQuizId(String quizId) {
        return quizEdgeMap.get(quizId);
    }

    @Getter
    public static class SessionNode {
        private final Long id;
        private final String word;
        private final NodeCategory category;

        private SessionNode(Long id, String word, NodeCategory category) {
            this.id = id;
            this.word = word;
            this.category = category;
        }

        public static SessionNode from(GraphNode node) {
            return new SessionNode(node.getId(), node.getWord(), node.getCategory());
        }
    }

    @Getter
    public static class SessionEdge {
        private final Long id;
        private final Long fromNodeId;
        private final Long toNodeId;
        private final String description;

        private SessionEdge(Long id, Long fromNodeId, Long toNodeId, String description) {
            this.id = id;
            this.fromNodeId = fromNodeId;
            this.toNodeId = toNodeId;
            this.description = description;
        }

        public static SessionEdge from(GraphEdge edge) {
            return new SessionEdge(
                    edge.getId(),
                    edge.getFromNode().getId(),
                    edge.getToNode().getId(),
                    edge.getDescription()
            );
        }
    }
}
