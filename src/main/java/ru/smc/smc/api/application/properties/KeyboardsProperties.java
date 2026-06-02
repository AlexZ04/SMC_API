package ru.smc.smc.api.application.properties;

import lombok.experimental.UtilityClass;
import ru.smc.smc.api.application.common.constant.BotCommands;
import ru.smc.smc.api.application.common.enums.Colors;
import ru.smc.smc.api.application.common.enums.ElementType;
import ru.smc.smc.api.application.common.model.response.ElementModel;

@UtilityClass
public class KeyboardsProperties {
    public ElementModel ASK_QUESTION_BUTTON = new ElementModel(ElementType.BUTTON, null, "Задать вопрос",
            Colors.WHITE.getColor(), Colors.BLACK.getColor());
    public ElementModel SET_UP_DISTRIBUTION_BUTTON = new ElementModel(ElementType.BUTTON, null, "Настроить рассылку",
            Colors.BLACK.getColor(), Colors.WHITE.getColor());
    public ElementModel FEEDBACK_LINK = new ElementModel(ElementType.LINK, "https://vk.com/smk_tsu?ref=group_menu&w=app5619682_-158563071%2523694549",
            "Обратная связь", Colors.BLACK.getColor(), Colors.WHITE.getColor());
    public ElementModel HELP_BUTTON = new ElementModel(ElementType.BUTTON, null,
            "Помощь", Colors.BLACK.getColor(), Colors.WHITE.getColor());
    public ElementModel PARTICIPATE_IN_GIVEAWAY = new ElementModel(ElementType.BUTTON, null,
            "Участвовать в розыгрыше", Colors.GREEN.getColor(), Colors.WHITE.getColor());
    public ElementModel BACK_TO_BOT_BUTTON = new ElementModel(ElementType.BUTTON, null,
            "К боту", Colors.BLACK.getColor(), Colors.WHITE.getColor());
    public ElementModel SET_MY_FACULTY_BUTTON = new ElementModel(ElementType.BUTTON, null,
            "Настроить свой факультет", Colors.BLACK.getColor(), Colors.WHITE.getColor());
    public ElementModel ADMIN_STATS_BUTTON = new ElementModel(ElementType.BUTTON, null,
            BotCommands.STATS_COMMAND, Colors.BLACK.getColor(), Colors.WHITE.getColor());
    public ElementModel ADMIN_CHANGE_SPORTORG_BUTTON = new ElementModel(ElementType.BUTTON, null,
            BotCommands.CHANGE_SPORTORG_COMMAND, Colors.BLACK.getColor(), Colors.WHITE.getColor());
    public ElementModel ADMIN_CHANGE_TEXTS_BUTTON = new ElementModel(ElementType.BUTTON, null,
            BotCommands.CHANGE_TEXTS_COMMAND, Colors.BLACK.getColor(), Colors.WHITE.getColor());
    public ElementModel ADMIN_SEND_DISTRIBUTION_BUTTON = new ElementModel(ElementType.BUTTON, null,
            BotCommands.SEND_DISTRIBUTION_COMMAND, Colors.BLACK.getColor(), Colors.WHITE.getColor());
    public ElementModel ADMIN_CHANGE_TOGGLE_STATE_BUTTON = new ElementModel(ElementType.BUTTON, null,
            BotCommands.CHANGE_TOGGLE_STATE_COMMAND, Colors.BLACK.getColor(), Colors.WHITE.getColor());
    public ElementModel ADMIN_HELP_BUTTON = new ElementModel(ElementType.BUTTON, null,
            BotCommands.HELP_COMMAND, Colors.BLACK.getColor(), Colors.WHITE.getColor());

    public ElementModel createInlineButton(String message) {
        return new ElementModel(ElementType.BUTTON, null,
                message, Colors.BLACK.getColor(), Colors.WHITE.getColor());
    }
}
