package ru.smc.smc.api.application.common.enums;

public enum MessageMeaningType {
    HELP, // [все] получить помощь по функциональности
    CHANGE_FACULTY,
    ASK_QUESTION, // [пользователь] задать вопрос
    SET_DISTRIBUTION,
    CHANGE_TEXTS, // [администратор] смена текстов
    CHANGE_SPORTORG, // [администратор] смена спорторга факультета
    GET_ALL_FACULTIES, // [администратор] получить список факультетов
    UNDEFINED // [все] сообщение неопознано
}
