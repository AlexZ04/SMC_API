package ru.smc.smc.api.application.common.constant;

import lombok.experimental.UtilityClass;

/*
Класс, отвечающий за строковые значения команд, доступных пользователям и администраторам
 */
@UtilityClass
public class BotCommands {
    public final String HELP_COMMAND = "/help";
    public final String CHANGE_TEXTS_COMMAND = "/changeDistribution";
    public final String CHANGE_SPORTORG_COMMAND = "/changeSportorg";
    public final String GET_ALL_FACULTIES_COMMAND = "/getAllFaculties";
    public final String SEND_DISTRIBUTION_COMMAND = "/sendDistribution";
    public final String GET_FEATURE_TOGGLES_COMMAND = "/getFeatureToggles";
    public final String STATS_COMMAND = "/stats";
    public final String CHANGE_TOGGLE_STATE_COMMAND = "/changeToggleState";
    public final String GET_ADMINS_COMMAND = "/getAdmins";
    public final String ADD_ADMIN_COMMAND = "/addAdmin";
    public final String ADD_SUPER_ADMIN_COMMAND = "/addSuperAdmin";
    public final String REMOVE_ADMIN_COMMAND = "/removeAdmin";
}
