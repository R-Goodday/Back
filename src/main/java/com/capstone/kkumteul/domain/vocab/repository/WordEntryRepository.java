package com.capstone.kkumteul.domain.vocab.repository;

import com.capstone.kkumteul.domain.vocab.entity.WordEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordEntryRepository extends JpaRepository<WordEntry, Long> {

    /** 같은 동화에 같은 단어가 이미 등록되어 있는지 — first-occurrence-wins pre-check */
    boolean existsByFairytaleIdAndWord(Long fairytaleId, String word);

    /** 본인 동화 누적 단어장 조회 — 페이지 순서로 정렬 */
    List<WordEntry> findByFairytaleIdOrderByPageNoAsc(Long fairytaleId);
}
