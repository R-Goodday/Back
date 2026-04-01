package com.capstone.kkumteul.domain.fairytale.repository;

import com.capstone.kkumteul.domain.fairytale.entity.Background;
import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FairytaleRepository extends JpaRepository<Fairytale, Long> {

    Page<Fairytale> findByUserIdAndBackgroundIn(Long userId, List<Background> backgrounds, Pageable pageable);

    Page<Fairytale> findByUserIdNotAndBackgroundIn(Long userId, List<Background> backgrounds, Pageable pageable);
}
