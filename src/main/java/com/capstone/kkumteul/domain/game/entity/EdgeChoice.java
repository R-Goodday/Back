package com.capstone.kkumteul.domain.game.entity;

import com.capstone.kkumteul.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "edge_choices", indexes = {
        @Index(name = "idx_edge_id", columnList = "edge_id")
})
public class EdgeChoice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edge_id", nullable = false)
    private GraphEdge edge;

    @Column(nullable = false, length = 50)
    private String content;

    @Column(nullable = false)
    private boolean isAnswer;
}
