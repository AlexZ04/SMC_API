package ru.smc.smc.api.application.common.constant;

import lombok.experimental.UtilityClass;

/*
Класс, отвечающий за строковые значения команд, доступных пользователям и администраторам
 */
@UtilityClass
public class BotCommands {
    public final String HELP_COMMAND = "/help";
    public final String CHANGE_TEXTS_COMMAND = "/changeTexts";
    public final String CHANGE_SPORTORG_COMMAND = "/changeSportorg";
}
