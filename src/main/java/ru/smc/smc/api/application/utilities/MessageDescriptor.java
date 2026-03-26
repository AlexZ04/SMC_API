package ru.smc.smc.api.application.utilities;

import lombok.experimental.UtilityClass;
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
        var messageMeaning = checkIfHelpMessage(message, messageRoleType, currentUserState);
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
    private static MessageMeaningType checkIfHelpMessage(String message, MessageRoleType messageRoleType, UserState currentUserState) {
        return message.length() < 10 && (message.toLowerCase().contains("помощь")) ?
                MessageMeaningType.HELP : MessageMeaningType.UNDEFINED;
    }
}
