package ru.smc.smc.api.domain.enums;

public enum UserState {
    MAIN_MENU(0);

    private final int keyboardCode;

    UserState(int keyboardCode) {
        this.keyboardCode = keyboardCode;
    }

    public int getCode() {
        return keyboardCode;
    }
}
