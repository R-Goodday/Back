package com.capstone.kkumteul.domain.fairytale.repository;

import com.capstone.kkumteul.domain.fairytale.entity.Paragraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParagraphRepository extends JpaRepository<Paragraph, Long> {

    List<Paragraph> findByFairytaleIdOrderByPageAsc(Long fairytaleId);
}
