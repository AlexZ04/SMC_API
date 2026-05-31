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
        var messageMeaning = checkIfHelpMessage(message);
        if (messageMeaning != MessageMeaningType.UNDEFINED) {
            return messageMeaning;
        }

        messageMeaning = checkIfChangeTextsMessage(message, messageRoleType, currentUserState);
        if (messageMeaning != MessageMeaningType.UNDEFINED) {
            return messageMeaning;
        }

        messageMeaning = checkIfAskQuestionMessage(message, messageRoleType, currentUserState);
        if (messageMeaning != MessageMeaningType.UNDEFINED) {
            return messageMeaning;
        }

        return MessageMeaningType.UNDEFINED;
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
