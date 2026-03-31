package com.capstone.kkumteul.domain.game.repository;

import com.capstone.kkumteul.domain.game.entity.GameResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameResultRepository extends JpaRepository<GameResult, Long> {

    /** (userId, fairytaleId) 복합 조건 조회 — 1회 플레이 제한 및 완료 여부 확인 */
    Optional<GameResult> findByUserIdAndFairytaleId(Long userId, Long fairytaleId);

    /** 게임 결과 존재 여부 확인 */
    boolean existsByUserIdAndFairytaleId(Long userId, Long fairytaleId);
}
