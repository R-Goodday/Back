package com.capstone.kkumteul.domain.game.web.dto;

import com.capstone.kkumteul.domain.game.entity.EdgeChoice;

import java.util.Collections;
import java.util.List;

/**
 * POST /game/quiz 응답 DTO.
 * 유효한 노드 조합일 때 퀴즈 ID와 보기 3개(랜덤 셔플)를 반환.
 * quizId는 이후 POST /game/quiz/answer에서 정답 제출 시 필요.
 */
public record QuizRes(
        String quizId,
        String question,
        List<ChoiceRes> choices
) {

    public static QuizRes of(String quizId, String fromWord, String toWord, List<EdgeChoice> choices) {
        String question = fromWord + " → " + toWord + ", 어떤 관계일까요?";
        List<ChoiceRes> choiceResList = choices.stream()
                .map(c -> new ChoiceRes(c.getId(), c.getContent()))
                .toList();
        // 보기 순서 랜덤 셔플 (정답이 항상 같은 위치에 있으면 안 됨)
        List<ChoiceRes> shuffled = new java.util.ArrayList<>(choiceResList);
        Collections.shuffle(shuffled);
        return new QuizRes(quizId, question, shuffled);
    }

    public record ChoiceRes(Long choiceId, String content) {}
}
