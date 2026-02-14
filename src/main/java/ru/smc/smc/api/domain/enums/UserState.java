package ru.smc.smc.api.domain.enums;

import lombok.Getter;

public enum UserState {
    MAIN_MENU(0);

    @Getter
    private final int keyboardCode;

    UserState(int keyboardCode) {
        this.keyboardCode = keyboardCode;
    }
}
