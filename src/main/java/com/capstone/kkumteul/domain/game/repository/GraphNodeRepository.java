package com.capstone.kkumteul.domain.game.repository;

import com.capstone.kkumteul.domain.game.entity.GraphNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GraphNodeRepository extends JpaRepository<GraphNode, Long> {

    /** 동화별 전체 노드 조회 — 게임 시작 시 세션에 캐싱할 데이터 로드 */
    List<GraphNode> findByFairytaleId(Long fairytaleId);

    /** 그래프 존재 여부 확인 — 게임 시작 전 그래프 추출 완료 여부 판단 */
    boolean existsByFairytaleId(Long fairytaleId);
}
