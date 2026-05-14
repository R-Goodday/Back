package com.capstone.kkumteul.domain.game.exception;

import com.capstone.kkumteul.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.capstone.kkumteul.global.constant.StaticValue.*;

@Getter
@AllArgsConstructor
public enum GameErrorCode implements BaseResponseCode {

    SESSION_NOT_FOUND("GAME_404_1", NOT_FOUND, "세션이 만료되었습니다. 게임을 다시 시작해주세요."),
    GRAPH_NOT_FOUND("GAME_404_3", NOT_FOUND, "지식그래프가 아직 생성되지 않았습니다."),
    GAME_ALREADY_COMPLETED("GAME_409_1", CONFLICT, "이미 완료한 게임입니다. 관계도를 조회해주세요."),
    INVALID_EDGE("GAME_400_1", BAD_REQUEST, "연결할 수 없는 노드 조합입니다."),
    INVALID_CATEGORY("GAME_400_2", BAD_REQUEST, "유효하지 않은 카테고리입니다."),
    ALREADY_ANSWERED("GAME_409_2", CONFLICT, "이미 완료한 엣지입니다."),
    QUIZ_NOT_FOUND("GAME_404_4", NOT_FOUND, "퀴즈를 찾을 수 없습니다."),
    EDGE_NOT_FOUND("GAME_404_5", NOT_FOUND, "해당 관계를 찾을 수 없습니다."),
    GAME_NOT_COMPLETED("GAME_404_6", NOT_FOUND, "완료된 게임이 없습니다."),
    GAME_FORBIDDEN("GAME_403_1", FORBIDDEN, "본인 동화가 아닙니다."),
    GRAPH_EXTRACT_FAILED("GAME_502_1", BAD_GATEWAY, "지식그래프 추출에 실패했습니다."),
    INVALID_GRAPH_PAYLOAD("GAME_500_1", INTERNAL_SERVER_ERROR, "지식그래프 응답이 유효하지 않습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
