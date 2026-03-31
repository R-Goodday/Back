package com.capstone.kkumteul.domain.game.repository;

import com.capstone.kkumteul.domain.game.entity.GraphEdge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GraphEdgeRepository extends JpaRepository<GraphEdge, Long> {

    /**
     * 동화별 전체 엣지 조회.
     * fromNode의 fairytale_id를 통해 간접 조회 (엣지 테이블에 fairytale_id 컬럼 없음).
     */
    @Query("SELECT e FROM GraphEdge e WHERE e.fromNode.fairytale.id = :fairytaleId")
    List<GraphEdge> findByFairytaleId(@Param("fairytaleId") Long fairytaleId);

    /**
     * 양방향 노드 쌍으로 엣지 조회 — 2단계에서 사용.
     * 아이가 A→B든 B→A든 동일한 엣지를 반환하도록 OR 조건 매칭.
     */
    @Query("SELECT e FROM GraphEdge e " +
            "WHERE (e.fromNode.id = :nodeA AND e.toNode.id = :nodeB) " +
            "OR (e.fromNode.id = :nodeB AND e.toNode.id = :nodeA)")
    Optional<GraphEdge> findByNodePair(@Param("nodeA") Long nodeA, @Param("nodeB") Long nodeB);
}
