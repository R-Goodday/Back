package com.capstone.kkumteul.domain.game.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.capstone.kkumteul.domain.game.exception.InvalidCategoryException;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum NodeCategory {
    CHARACTER("등장인물"),
    PLACE("장소"),
    EVENT("사건"),
    MORAL("교훈");

    private final String label;

    /** 한글 라벨 → enum 변환. 매칭 실패 시 400 (INVALID_CATEGORY) */
    public static NodeCategory fromLabel(String label) {
        return Arrays.stream(values())
                .filter(c -> c.label.equals(label))
                .findFirst()
                .orElseThrow(InvalidCategoryException::new);
    }
}
