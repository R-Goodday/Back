package com.capstone.kkumteul.domain.vocab.entity;

import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;
import com.capstone.kkumteul.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 동화 페이지에서 추출된 어려운 단어 항목.
 *
 * <p>중복 정책: <b>first-occurrence-wins</b> — 같은 동화 안에서 같은 단어가
 * 여러 페이지에 등장해도 최초로 추출된 페이지 1개 row만 저장한다.
 * UNIQUE(fairytale_id, word)로 강제하며, race condition은
 * {@code DataIntegrityViolationException} catch로 처리한다.</p>
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "word_entry", uniqueConstraints = {
        @UniqueConstraint(name = "uk_word_entry_fairytale_word", columnNames = {"fairytale_id", "word"})
}, indexes = {
        @Index(name = "idx_word_entry_fairytale", columnList = "fairytale_id")
})
public class WordEntry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_entry_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fairytale_id", nullable = false)
    private Fairytale fairytale;

    @Column(name = "page_no", nullable = false)
    private int pageNo;

    @Column(nullable = false, length = 100)
    private String word;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String meaning;
}
