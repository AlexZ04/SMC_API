package ru.smc.smc.api.application.utilities;

import lombok.experimental.UtilityClass;
import ru.smc.smc.api.application.common.constant.AdminTextSettingsTexts;
import ru.smc.smc.api.application.common.constant.BotCommands;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.MessageRoleType;
import ru.smc.smc.api.application.common.enums.UserState;

/*
Дескриптор сообщений пользователя.
Определяет принадлежность сообщений к определённому типу.
Проверка сообщений не регистрозависима
 */
@UtilityClass
public class MessageDescriptor {
    /*
    Сообщение-триггер возвращения пользователя в главное меню.
    Условия:
    1) Длина сообщения менее 10 символов
    2) Сообщение содержит в себе фразы "к боту" или "назад"
     */
    public boolean isReturnMessage(String message) {
        return message.length() < 10 && message.toLowerCase().contains("к боту") || message.toLowerCase().contains("назад");
    }

    /*
    Метод для определения типа отправленного пользователем сообщения
     */
    public static MessageMeaningType defineMessageMeaning(String message, MessageRoleType messageRoleType, UserState currentUserState) {
        var messageMeaning = checkIfStartMessage(message);
        if (messageMeaning != MessageMeaningType.UNDEFINED) {
            return messageMeaning;
        }

        messageMeaning = checkIfHelpMessage(message);
        if (messageMeaning != MessageMeaningType.UNDEFINED) {
            return messageMeaning;
        }

        messageMeaning = checkIfChangeTextsMessage(message, messageRoleType, currentUserState);
        if (messageMeaning != MessageMeaningType.UNDEFINED) {
            return messageMeaning;
        }

        messageMeaning = checkIfChangeSportorgMessage(message, messageRoleType, currentUserState);
        if (messageMeaning != MessageMeaningType.UNDEFINED) {
            return messageMeaning;
        }

        messageMeaning = checkIfGetAllFacultiesMessage(message, messageRoleType, currentUserState);
        if (messageMeaning != MessageMeaningType.UNDEFINED) {
            return messageMeaning;
        }

        messageMeaning = checkIfGetAllSportorgsMessage(message, messageRoleType, currentUserState);
        if (messageMeaning != MessageMeaningType.UNDEFINED) {
            return messageMeaning;
        }

        messageMeaning = checkIfSendDistributionMessage(message, messageRoleType, currentUserState);
        if (messageMeaning != MessageMeaningType.UNDEFINED) {
            return messageMeaning;
        }

        messageMeaning = checkIfGetFeatureTogglesMessage(message, messageRoleType, currentUserState);
        if (messageMeaning != MessageMeaningType.UNDEFINED) {
            return messageMeaning;
        }

        messageMeaning = checkIfStatsMessage(message, messageRoleType, currentUserState);
        if (messageMeaning != MessageMeaningType.UNDEFINED) {
            return messageMeaning;
        }

        messageMeaning = checkIfChangeToggleStateMessage(message, messageRoleType, currentUserState);
        if (messageMeaning != MessageMeaningType.UNDEFINED) {
            return messageMeaning;
        }

        messageMeaning = checkIfGetAdminsMessage(message, messageRoleType, currentUserState);
        if (messageMeaning != MessageMeaningType.UNDEFINED) {
            return messageMeaning;
        }

        messageMeaning = checkIfManageAdminsMessage(message, messageRoleType, currentUserState);
        if (messageMeaning != MessageMeaningType.UNDEFINED) {
            return messageMeaning;
        }

        messageMeaning = checkIfGiveawayMessage(message, messageRoleType, currentUserState);
        if (messageMeaning != MessageMeaningType.UNDEFINED) {
            return messageMeaning;
        }

        messageMeaning = checkIfChangeFacultyMessage(message, messageRoleType, currentUserState);
        if (messageMeaning != MessageMeaningType.UNDEFINED) {
            return messageMeaning;
        }

        messageMeaning = checkIfSetDistributionMessage(message, messageRoleType, currentUserState);
        if (messageMeaning != MessageMeaningType.UNDEFINED) {
            return messageMeaning;
        }

        messageMeaning = checkIfAskQuestionMessage(message, messageRoleType, currentUserState);
        return messageMeaning;
    }

    private static MessageMeaningType checkIfStartMessage(String message) {
        return message.equalsIgnoreCase(BotCommands.START_COMMAND) ||
                message.equalsIgnoreCase(BotCommands.START_MESSAGE) ?
                MessageMeaningType.START : MessageMeaningType.UNDEFINED;
    }

    /*
    Проверка на сообщение - запрос помощи
    Условия:
    1) Длина менее 10 символов
    2) Сообщение содержит в себе слово "помощь"
     */
    private static MessageMeaningType checkIfHelpMessage(String message) {
        return message.length() < 10 && (message.toLowerCase().contains("помощь") || message.toLowerCase().contains("help")) ?
                MessageMeaningType.HELP : MessageMeaningType.UNDEFINED;
    }

    /*
    Проверка на сообщение - запрос настройки текстов
     */
    private static MessageMeaningType checkIfChangeTextsMessage(String message, MessageRoleType messageRoleType,
                                                                UserState currentUserState) {
        return messageRoleType == MessageRoleType.ADMIN &&
                ((currentUserState == UserState.MAIN_MENU && message.equalsIgnoreCase(BotCommands.CHANGE_TEXTS_COMMAND)) ||
                        (currentUserState == UserState.CHANGE_TEXTS && AdminTextSettingsTexts.isDistributionText(message.trim())) ||
                        isDistributionTextChangingState(currentUserState)) ?
                MessageMeaningType.CHANGE_TEXTS : MessageMeaningType.UNDEFINED;
    }

    private static boolean isDistributionTextChangingState(UserState currentUserState) {
        return currentUserState == UserState.CHANGE_EVENTS_DISTRIBUTION_TEXT ||
                currentUserState == UserState.CHANGE_COMPETITIONS_DISTRIBUTION_TEXT ||
                currentUserState == UserState.CHANGE_SCHEDULE_NEWS_DISTRIBUTION_TEXT ||
                currentUserState == UserState.CHANGE_GENERAL_DISTRIBUTION_TEXT ||
                currentUserState == UserState.CHANGE_GIVEAWAY_TEXT;
    }

    /*
    Проверка на сообщение - запрос изменения спорторга факультета
     */
    private static MessageMeaningType checkIfChangeSportorgMessage(String message, MessageRoleType messageRoleType,
                                                                   UserState currentUserState) {
        return messageRoleType == MessageRoleType.ADMIN &&
                ((currentUserState == UserState.MAIN_MENU && message.equalsIgnoreCase(BotCommands.CHANGE_SPORTORG_COMMAND)) ||
                        currentUserState == UserState.CHANGE_SPORTORG ||
                        currentUserState == UserState.CHANGE_SPORTORG_INFO) ?
                MessageMeaningType.CHANGE_SPORTORG : MessageMeaningType.UNDEFINED;
    }

    /*
    Проверка на сообщение - запрос списка факультетов
     */
    private static MessageMeaningType checkIfGetAllFacultiesMessage(String message, MessageRoleType messageRoleType,
                                                                    UserState currentUserState) {
        return messageRoleType == MessageRoleType.ADMIN && currentUserState == UserState.MAIN_MENU &&
                message.equalsIgnoreCase(BotCommands.GET_ALL_FACULTIES_COMMAND) ?
                MessageMeaningType.GET_ALL_FACULTIES : MessageMeaningType.UNDEFINED;
    }

    private static MessageMeaningType checkIfGetAllSportorgsMessage(String message, MessageRoleType messageRoleType,
                                                                    UserState currentUserState) {
        return messageRoleType == MessageRoleType.ADMIN && currentUserState == UserState.MAIN_MENU &&
                message.equalsIgnoreCase(BotCommands.GET_ALL_SPORTORGS_COMMAND) ?
                MessageMeaningType.GET_ALL_SPORTORGS : MessageMeaningType.UNDEFINED;
    }

    /*
    Проверка на сообщение - отправка рассылки
     */
    private static MessageMeaningType checkIfSendDistributionMessage(String message, MessageRoleType messageRoleType,
                                                                     UserState currentUserState) {
        return messageRoleType == MessageRoleType.ADMIN &&
                ((currentUserState == UserState.MAIN_MENU && message.equalsIgnoreCase(BotCommands.SEND_DISTRIBUTION_COMMAND)) ||
                        currentUserState == UserState.SEND_DISTRIBUTION ||
                        currentUserState == UserState.SEND_DISTRIBUTION_COMPETITIONS_FACULTIES ||
                        currentUserState == UserState.SEND_DISTRIBUTION_CONFIRMATION) ?
                MessageMeaningType.SEND_DISTRIBUTION : MessageMeaningType.UNDEFINED;
    }

    /*
    Проверка на сообщение - запрос списка тогглов
     */
    private static MessageMeaningType checkIfGetFeatureTogglesMessage(String message, MessageRoleType messageRoleType,
                                                                      UserState currentUserState) {
        return messageRoleType == MessageRoleType.ADMIN && currentUserState == UserState.MAIN_MENU &&
                message.equalsIgnoreCase(BotCommands.GET_FEATURE_TOGGLES_COMMAND) ?
                MessageMeaningType.GET_FEATURE_TOGGLES : MessageMeaningType.UNDEFINED;
    }

    /*
    Проверка на сообщение - запрос статистики
     */
    private static MessageMeaningType checkIfStatsMessage(String message, MessageRoleType messageRoleType,
                                                          UserState currentUserState) {
        return messageRoleType == MessageRoleType.ADMIN && currentUserState == UserState.MAIN_MENU &&
                message.equalsIgnoreCase(BotCommands.STATS_COMMAND) ?
                MessageMeaningType.STATS : MessageMeaningType.UNDEFINED;
    }

    /*
    Проверка на сообщение - изменение состояния тоггла
     */
    private static MessageMeaningType checkIfChangeToggleStateMessage(String message, MessageRoleType messageRoleType,
                                                                      UserState currentUserState) {
        return messageRoleType == MessageRoleType.ADMIN &&
                ((currentUserState == UserState.MAIN_MENU &&
                        message.equalsIgnoreCase(BotCommands.CHANGE_TOGGLE_STATE_COMMAND)) ||
                        currentUserState == UserState.CHANGE_TOGGLE_STATE) ?
                MessageMeaningType.CHANGE_TOGGLE_STATE : MessageMeaningType.UNDEFINED;
    }

    /*
    Проверка на сообщение - запрос списка администраторов
     */
    private static MessageMeaningType checkIfGetAdminsMessage(String message, MessageRoleType messageRoleType,
                                                              UserState currentUserState) {
        return messageRoleType == MessageRoleType.ADMIN && currentUserState == UserState.MAIN_MENU &&
                message.equalsIgnoreCase(BotCommands.GET_ADMINS_COMMAND) ?
                MessageMeaningType.GET_ADMINS : MessageMeaningType.UNDEFINED;
    }

    /*
    Проверка на сообщение - управление администраторами
     */
    private static MessageMeaningType checkIfManageAdminsMessage(String message, MessageRoleType messageRoleType,
                                                                 UserState currentUserState) {
        return messageRoleType == MessageRoleType.ADMIN &&
                ((currentUserState == UserState.MAIN_MENU &&
                        (message.equalsIgnoreCase(BotCommands.ADD_ADMIN_COMMAND) ||
                                message.equalsIgnoreCase(BotCommands.ADD_SUPER_ADMIN_COMMAND) ||
                                message.equalsIgnoreCase(BotCommands.REMOVE_ADMIN_COMMAND))) ||
                        currentUserState == UserState.ADD_ADMIN ||
                        currentUserState == UserState.ADD_SUPER_ADMIN ||
                        currentUserState == UserState.REMOVE_ADMIN) ?
                MessageMeaningType.MANAGE_ADMINS : MessageMeaningType.UNDEFINED;
    }

    private static MessageMeaningType checkIfGiveawayMessage(String message, MessageRoleType messageRoleType,
                                                             UserState currentUserState) {
        if (messageRoleType == MessageRoleType.USER && message.equalsIgnoreCase("Участвовать в розыгрыше")) {
            return MessageMeaningType.GIVEAWAY;
        }

        return messageRoleType == MessageRoleType.ADMIN &&
                ((currentUserState == UserState.MAIN_MENU &&
                        (message.equalsIgnoreCase(BotCommands.GET_GIVEAWAY_PARTICIPANTS_COMMAND) ||
                                message.equalsIgnoreCase(BotCommands.CLEAR_GIVEAWAY_PARTICIPANTS_COMMAND) ||
                                message.equalsIgnoreCase(BotCommands.GET_GIVEAWAY_WINNERS_COMMAND))) ||
                        currentUserState == UserState.GET_GIVEAWAY_WINNERS) ?
                MessageMeaningType.GIVEAWAY : MessageMeaningType.UNDEFINED;
    }

    private static MessageMeaningType checkIfSetDistributionMessage(String message, MessageRoleType messageRoleType,
                                                                    UserState currentUserState) {
        return messageRoleType == MessageRoleType.USER &&
                ((currentUserState == UserState.MAIN_MENU && message.equalsIgnoreCase("Настроить рассылку")) ||
                        currentUserState == UserState.SET_DISTRIBUTION ||
                        currentUserState == UserState.UNSUBSCRIBE_DISTRIBUTION) ?
                MessageMeaningType.SET_DISTRIBUTION : MessageMeaningType.UNDEFINED;
    }

    private static MessageMeaningType checkIfChangeFacultyMessage(String message, MessageRoleType messageRoleType,
                                                                  UserState currentUserState) {
        return messageRoleType == MessageRoleType.USER &&
                (message.equalsIgnoreCase("Настроить свой факультет") ||
                        currentUserState == UserState.CHANGE_FACULTY) ?
                MessageMeaningType.CHANGE_FACULTY : MessageMeaningType.UNDEFINED;
    }

    /*
    Проверка на сообщение - "Задать вопрос" (переход на соответствующих экран)
    Условия:
    0) Сообщение пришло в часть пользователей
    1) Длина менее 15 символов
    2) Сообщение содержит словосочетание "задать вопрос"
     */
    private static MessageMeaningType checkIfAskQuestionMessage(String message, MessageRoleType messageRoleType, UserState currentUserState) {
        return messageRoleType == MessageRoleType.USER && (currentUserState == UserState.QUESTION || (message.length() < 15 &&
                (message.toLowerCase().contains("задать вопрос")))) ?
                MessageMeaningType.ASK_QUESTION : MessageMeaningType.UNDEFINED;
    }
}
