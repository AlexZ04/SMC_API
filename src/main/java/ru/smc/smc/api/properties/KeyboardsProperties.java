package ru.smc.smc.api.properties;

import ru.smc.smc.api.domain.enums.Colors;
import ru.smc.smc.api.domain.enums.ElementType;
import ru.smc.smc.api.domain.model.response.ElementModel;

public class KeyboardsProperties {
    public static ElementModel ASK_QUESTION_BUTTON = new ElementModel(ElementType.BUTTON, null, "Задать вопрос",
            Colors.WHITE.getColor(), Colors.BLACK.getColor());
    public static ElementModel SET_UP_DISTRIBUTION_BUTTON = new ElementModel(ElementType.BUTTON, null, "Настроить рассылку",
            Colors.BLACK.getColor(), Colors.WHITE.getColor());
    public static ElementModel FEEDBACK_LINK = new ElementModel(ElementType.LINK, "https://vk.com/smk_tsu?ref=group_menu&w=app5619682_-158563071%2523694549",
            "Настроить рассылку", Colors.BLACK.getColor(), Colors.WHITE.getColor());
    public static ElementModel HELP_BUTTON = new ElementModel(ElementType.BUTTON, null,
            "Помощь", Colors.BLACK.getColor(), Colors.WHITE.getColor());
    public static ElementModel PARTICIPATE_IN_GIVEAWAY = new ElementModel(ElementType.BUTTON, null,
            "Участвовать в розыгрыше", Colors.GREEN.getColor(), Colors.WHITE.getColor());
    public static ElementModel BACK_TO_BOT_BUTTON = new ElementModel(ElementType.BUTTON, null,
            "К боту", Colors.BLACK.getColor(), Colors.WHITE.getColor());
}
