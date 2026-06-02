package ru.smc.smc.api.application.common.enums;

public enum MessageMeaningType {
    HELP, // [все] получить помощь по функциональности
    CHANGE_FACULTY,
    ASK_QUESTION, // [пользователь] задать вопрос
    SET_DISTRIBUTION,
    CHANGE_TEXTS, // [администратор] смена текстов
    CHANGE_SPORTORG, // [администратор] смена спорторга факультета
    GET_ALL_FACULTIES, // [администратор] получить список факультетов
    SEND_DISTRIBUTION, // [администратор] отправить рассылку
    GET_FEATURE_TOGGLES, // [администратор] получить список тогглов
    STATS, // [администратор] получить статистику
    CHANGE_TOGGLE_STATE, // [администратор] изменить состояние тоггла
    UNDEFINED // [все] сообщение неопознано
}
