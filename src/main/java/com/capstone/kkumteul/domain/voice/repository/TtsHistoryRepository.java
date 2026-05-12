package com.capstone.kkumteul.domain.voice.repository;

import com.capstone.kkumteul.domain.voice.entity.TtsHistory;
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
}
