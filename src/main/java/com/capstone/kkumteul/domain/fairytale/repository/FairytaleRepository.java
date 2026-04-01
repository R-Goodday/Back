package com.capstone.kkumteul.domain.fairytale.repository;

import com.capstone.kkumteul.domain.fairytale.entity.Background;
import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FairytaleRepository extends JpaRepository<Fairytale, Long> {

    @EntityGraph(attributePaths = "user")
    Page<Fairytale> findByUserIdAndBackgroundIn(Long userId, List<Background> backgrounds, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<Fairytale> findByUserIdNotAndBackgroundIn(Long userId, List<Background> backgrounds, Pageable pageable);

    @Query("SELECT f FROM Fairytale f JOIN FETCH f.user WHERE f.id = :id")
    Optional<Fairytale> findByIdWithUser(@Param("id") Long id);
}
