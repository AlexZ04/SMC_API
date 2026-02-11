package ru.smc.smc.api.utilities;

import lombok.experimental.UtilityClass;

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
}
