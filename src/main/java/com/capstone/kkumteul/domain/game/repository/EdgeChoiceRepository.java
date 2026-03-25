package com.capstone.kkumteul.domain.game.repository;

import com.capstone.kkumteul.domain.game.entity.EdgeChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EdgeChoiceRepository extends JpaRepository<EdgeChoice, Long> {

    List<EdgeChoice> findByEdgeId(Long edgeId);
}
