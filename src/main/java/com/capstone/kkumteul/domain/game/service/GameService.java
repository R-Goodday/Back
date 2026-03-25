package com.capstone.kkumteul.domain.game.service;

import com.capstone.kkumteul.domain.game.web.dto.ClassifyRes;
import com.capstone.kkumteul.domain.game.web.dto.GameStartRes;

public interface GameService {

    GameStartRes startGame(Long userId, Long fairytaleId);

    ClassifyRes classify(String sessionId, Long nodeId, String category);
}
