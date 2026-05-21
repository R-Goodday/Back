package com.capstone.kkumteul.domain.voice.repository;

import com.capstone.kkumteul.domain.voice.entity.TtsHistory;
import com.capstone.kkumteul.domain.voice.web.dto.TtsResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TtsHistoryRepository extends CrudRepository<TtsHistory, Long> {

    @Query("""
select  th
from TtsHistory th join fetch
    th.paragraph p
where p.id = :paragraphId
    and th.user.id = :userId
"""
    )
    Optional<TtsHistory> findByParagraphIdAndUserId(
            @Param("paragraphId") Long paragraphId,
            @Param("userId") Long userId);


    @Query("""
select new com.capstone.kkumteul.domain.voice.web.dto.TtsResponse(
    t.ttsUrl
)
from TtsHistory t join
    t.paragraph p
where p.fairytale.id = :fairytaleId
    and p.page = :pageNo
    and t.user.id = :userId
""")
    Optional<TtsResponse> findTtsUrlByFairytaleIdAndUserIdAndPageNo(
            @Param("fairytaleId") Long fairytaleId,
            @Param("userId") Long userId,
            @Param("pageNo") int pageNo
    );
}
