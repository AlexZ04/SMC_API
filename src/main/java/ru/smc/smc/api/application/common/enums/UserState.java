package ru.smc.smc.api.application.common.enums;

import lombok.Getter;

public enum UserState {
    MAIN_MENU(0), // 0 - клавиатура главного меню
    QUESTION(1), // 1 - клавиатура "К боту"
    CHANGE_TEXTS(1), // 1 - клавиатура "К боту"
    CHANGE_EVENTS_DISTRIBUTION_TEXT(1), // 1 - клавиатура "К боту"
    CHANGE_COMPETITIONS_DISTRIBUTION_TEXT(1), // 1 - клавиатура "К боту"
    CHANGE_SCHEDULE_NEWS_DISTRIBUTION_TEXT(1), // 1 - клавиатура "К боту"
    CHANGE_GENERAL_DISTRIBUTION_TEXT(1), // 1 - клавиатура "К боту"
    CHANGE_GIVEAWAY_TEXT(1), // 1 - клавиатура "К боту"
    CHANGE_SPORTORG(1), // 1 - клавиатура "К боту"
    CHANGE_SPORTORG_INFO(1), // 1 - клавиатура "К боту"
    SEND_DISTRIBUTION(1), // 1 - клавиатура "К боту"
    SEND_DISTRIBUTION_COMPETITIONS_FACULTIES(1), // 1 - клавиатура "К боту"
    SEND_DISTRIBUTION_CONFIRMATION(1), // 1 - клавиатура "К боту"
    CHANGE_TOGGLE_STATE(1), // 1 - клавиатура "К боту"
    ADD_ADMIN(1), // 1 - клавиатура "К боту"
    ADD_SUPER_ADMIN(1), // 1 - клавиатура "К боту"
    REMOVE_ADMIN(1), // 1 - клавиатура "К боту"
    SET_DISTRIBUTION(1), // 1 - клавиатура "К боту"
    UNSUBSCRIBE_DISTRIBUTION(1); // 1 - клавиатура "К боту"

    @Getter
    private final int keyboardCode;

    UserState(int keyboardCode) {
        this.keyboardCode = keyboardCode;
    }
}
