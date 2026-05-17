package com.capstone.kkumteul.domain.fairytale.repository;

import com.capstone.kkumteul.domain.fairytale.entity.Background;
import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FairytaleRepository extends JpaRepository<Fairytale, Long> {

    @Query(value = "SELECT f FROM Fairytale f JOIN FETCH f.user WHERE f.user.id = :userId AND f.background IN :backgrounds",
            countQuery = "SELECT COUNT(f) FROM Fairytale f WHERE f.user.id = :userId AND f.background IN :backgrounds")
    Page<Fairytale> findByUserIdAndBackgroundIn(@Param("userId") Long userId, @Param("backgrounds") List<Background> backgrounds, Pageable pageable);

    @Query(value = "SELECT f FROM Fairytale f JOIN FETCH f.user WHERE f.user.id <> :userId ORDER BY f.createdAt DESC",
            countQuery = "SELECT COUNT(f) FROM Fairytale f WHERE f.user.id <> :userId")
    Page<Fairytale> findSharedFairytales(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT f FROM Fairytale f JOIN FETCH f.user WHERE f.id = :id")
    Optional<Fairytale> findByIdWithUser(@Param("id") Long id);
}
