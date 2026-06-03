package ru.smc.smc.api.application.common.enums;

public enum MessageMeaningType {
    START, // [все] приветственное сообщение
    HELP, // [все] получить помощь по функциональности
    CHANGE_FACULTY,
    ASK_QUESTION, // [пользователь] задать вопрос
    SET_DISTRIBUTION, // [пользователь] настроить подписки на рассылки
    CHANGE_TEXTS, // [администратор] смена текстов
    CHANGE_SPORTORG, // [администратор] смена спорторга факультета
    GET_ALL_FACULTIES, // [администратор] получить список факультетов
    GET_ALL_SPORTORGS, // [администратор] получить список спорторгов
    SEND_DISTRIBUTION, // [администратор] отправить рассылку
    GET_FEATURE_TOGGLES, // [администратор] получить список тогглов
    STATS, // [администратор] получить статистику
    CHANGE_TOGGLE_STATE, // [администратор] изменить состояние тоггла
    GET_ADMINS, // [администратор] получить список администраторов
    MANAGE_ADMINS, // [администратор] управление администраторами
    GIVEAWAY, // [все] розыгрыш
    UNDEFINED // [все] сообщение неопознано
}
