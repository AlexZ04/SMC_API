package ru.smc.smc.api.utilities;

import lombok.experimental.UtilityClass;
import ru.smc.smc.api.domain.enums.MessageMeaningType;
import ru.smc.smc.api.domain.enums.MessageRoleType;
import ru.smc.smc.api.domain.enums.UserState;

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

    public static MessageMeaningType defineMessageMeaning(String message, MessageRoleType messageRoleType, UserState currentState) {
        return MessageMeaningType.HELP;
    }
}
