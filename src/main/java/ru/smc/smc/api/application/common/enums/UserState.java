package ru.smc.smc.api.application.common.enums;

import lombok.Getter;

public enum UserState {
    MAIN_MENU(0), // 0 - клавиатура главного меню
    QUESTION(1), // 1 - клавиатура "К боту"
    CHANGE_TEXTS(1); // 1 - клавиатура "К боту"

    @Getter
    private final int keyboardCode;

    UserState(int keyboardCode) {
        this.keyboardCode = keyboardCode;
    }
}
