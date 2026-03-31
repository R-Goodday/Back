package com.capstone.kkumteul.domain.game.repository;

import com.capstone.kkumteul.domain.game.entity.GraphNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GraphNodeRepository extends JpaRepository<GraphNode, Long> {

    /** 동화별 전체 노드 조회 — fetch join으로 fairytale 함께 로드 */
    @Query("SELECT n FROM GraphNode n JOIN FETCH n.fairytale WHERE n.fairytale.id = :fairytaleId")
    List<GraphNode> findByFairytaleId(@Param("fairytaleId") Long fairytaleId);

    /** 그래프 존재 여부 확인 — 게임 시작 전 그래프 추출 완료 여부 판단 */
    boolean existsByFairytaleId(Long fairytaleId);
}
