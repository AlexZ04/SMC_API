package ru.smc.smc.api.domain.enums;

import lombok.Getter;

public enum Colors {
    WHITE("white"),
    BLACK("black"),
    GREEN("green");

    @Getter
    private final String color;

    Colors(String color) {
        this.color = color;
    }
}
