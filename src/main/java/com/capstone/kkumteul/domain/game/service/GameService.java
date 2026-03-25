package com.capstone.kkumteul.domain.game.service;

import com.capstone.kkumteul.domain.game.web.dto.*;

public interface GameService {

    GameStartRes startGame(Long userId, Long fairytaleId);

    ClassifyRes classify(String sessionId, Long nodeId, String category);

    QuizRes requestQuiz(String sessionId, Long fromNodeId, Long toNodeId);

    QuizAnswerRes answerQuiz(String sessionId, String quizId, Long selectedChoiceId);

    EdgeDetailRes getEdgeDetail(Long userId, Long edgeId);

    GraphDetailRes getGraph(Long userId, Long fairytaleId);
}
