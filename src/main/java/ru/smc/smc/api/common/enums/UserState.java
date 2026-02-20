package ru.smc.smc.api.common.enums;

import lombok.Getter;

public enum UserState {
    MAIN_MENU(0); // 0 - клавиатура главного меню

    @Getter
    private final int keyboardCode;

    UserState(int keyboardCode) {
        this.keyboardCode = keyboardCode;
    }
}
