package com.capstone.kkumteul.domain.fairytale.entity;

import java.util.Arrays;
import java.util.List;

public enum Island {

    TRADITIONAL(List.of(Background.KOREAN_TRADITIONAL)),
    MEDIEVAL(List.of(Background.EUROPEAN_MEDIEVAL)),
    FANTASY(List.of(
            Background.FOREST_NATURE,
            Background.MIXED,
            Background.FANTASY_WORLD,
            Background.MODERN_FANTASY,
            Background.UNDERWATER,
            Background.SKY_HEAVEN
    ));

    private final List<Background> backgrounds;

    Island(List<Background> backgrounds) {
        this.backgrounds = backgrounds;
    }

    public List<Background> getBackgrounds() {
        return backgrounds;
    }

    public static Island fromBackground(Background background) {
        return Arrays.stream(values())
                .filter(island -> island.backgrounds.contains(background))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("매핑되지 않은 배경: " + background));
    }
}
