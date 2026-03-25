package com.capstone.kkumteul.domain.game.entity;

import com.capstone.kkumteul.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "graph_edges", indexes = {
        @Index(name = "idx_edge_nodes", columnList = "from_node_id, to_node_id")
})
public class GraphEdge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "edge_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_node_id", nullable = false)
    private GraphNode fromNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_node_id", nullable = false)
    private GraphNode toNode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "edge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EdgeChoice> choices = new ArrayList<>();
}
