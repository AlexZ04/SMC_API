package ru.smc.smc.api.application.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.smc.smc.api.application.common.constant.AdminTextSettingsTexts;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum AdminDistributionType {
    GENERAL("Общая", "общая", AdminTextSettingsTexts.GENERAL_DISTRIBUTION_TEXT_PATHFILE),
    EVENTS("По мероприятиям", "по мероприятиям", AdminTextSettingsTexts.EVENTS_DISTRIBUTION_TEXT_PATHFILE),
    COMPETITIONS("По соревнованиям факультета", "по соревнованиям факультета",
            AdminTextSettingsTexts.COMPETITIONS_DISTRIBUTION_TEXT_PATHFILE),
    SCHEDULE("По новостям расписания", "по новостям расписания",
            AdminTextSettingsTexts.SCHEDULE_NEWS_DISTRIBUTION_TEXT_PATHFILE),
    GIVEAWAY("Для розыгрыша", "для розыгрыша", AdminTextSettingsTexts.GIVEAWAY_TEXT_PATHFILE);

    private final String buttonText;
    private final String resultText;
    private final String pathFile;

    public static Optional<AdminDistributionType> findByButtonText(String message) {
        return Arrays.stream(values())
                .filter(type -> type.getButtonText().equalsIgnoreCase(message.trim()))
                .findFirst();
    }
}
