package ru.smc.smc.api.application.common.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AdminTextSettingsTexts {
    public String SPORT_ORG_TEXT = "Для спорторга";
    public String EVENTS_DISTRIBUTION_TEXT = "Для рассылки о мероприятиях";
    public String COMPETITIONS_DISTRIBUTION_TEXT = "Для рассылки о соревнованиях";
    public String SCHEDULE_NEWS_DISTRIBUTION_TEXT = "Для рассылки о новостях расписания";
    public String GENERAL_DISTRIBUTION_TEXT = "Для всеобщей рассылки";
    public String GIVEAWAY_TEXT = "Для розыгрыша";
    public String EVENTS_DISTRIBUTION_TEXT_PATHFILE = "events-distribution";
    public String COMPETITIONS_DISTRIBUTION_TEXT_PATHFILE = "competitions-distribution";
    public String SCHEDULE_NEWS_DISTRIBUTION_TEXT_PATHFILE = "schedule-news-distribution";
    public String GENERAL_DISTRIBUTION_TEXT_PATHFILE = "general-distribution";
    public String GIVEAWAY_TEXT_PATHFILE = "giveaway";

    public boolean isDistributionText(String message) {
        return message.equalsIgnoreCase(EVENTS_DISTRIBUTION_TEXT) ||
                message.equalsIgnoreCase(COMPETITIONS_DISTRIBUTION_TEXT) ||
                message.equalsIgnoreCase(SCHEDULE_NEWS_DISTRIBUTION_TEXT) ||
                message.equalsIgnoreCase(GENERAL_DISTRIBUTION_TEXT) ||
                message.equalsIgnoreCase(GIVEAWAY_TEXT);
    }
}
