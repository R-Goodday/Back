package com.capstone.kkumteul.domain.game.entity;

import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;
import com.capstone.kkumteul.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "graph_nodes", indexes = {
        @Index(name = "idx_fairytale_id", columnList = "fairytale_id")
})
public class GraphNode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "node_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fairytale_id", nullable = false)
    private Fairytale fairytale;

    @Column(nullable = false, length = 50)
    private String word;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NodeCategory category;
}
