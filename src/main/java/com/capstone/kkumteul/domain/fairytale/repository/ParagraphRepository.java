package com.capstone.kkumteul.domain.fairytale.repository;

import com.capstone.kkumteul.domain.fairytale.entity.Paragraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParagraphRepository extends JpaRepository<Paragraph, Long> {

    List<Paragraph> findByFairytaleIdOrderByPageAsc(Long fairytaleId);

    /** 특정 페이지의 문장들 조회 — 단어장 추출 시 페이지 단위 본문 로드 */
    List<Paragraph> findByFairytaleIdAndPage(Long fairytaleId, int page);

    boolean existsById(Long paragraphId);

    boolean existsByFairytaleId(Long fairytaleId);
}
