package ru.smc.smc.api.application.service.processors.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.ElementModel;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.properties.KeyboardsProperties;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.application.utilities.FileUtility;
import ru.smc.smc.api.domain.entity.BotUser;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChangeTextsMessageAdminProcessor implements MessageAdminProcessor {

    private static final String CHANGE_TEXTS_MENU_PATHFILE = "admin-change-texts";
    private static final String SPORT_ORG_TEXT = "Для спорторга";
    private static final String EVENTS_DISTRIBUTION_TEXT = "Для рассылки о мероприятиях";
    private static final String COMPETITIONS_DISTRIBUTION_TEXT = "Для рассылки о соревнованиях";
    private static final String SCHEDULE_NEWS_DISTRIBUTION_TEXT = "Для рассылки о новостях расписания";
    private static final String GENERAL_DISTRIBUTION_TEXT = "Для всеобщей рассылки";
    private static final String GIVEAWAY_TEXT = "Для розыгрыша";

    private final ResponseService responseService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        return responseService.createUserResponseWithInlineKeyboard(user, UserState.CHANGE_TEXTS,
                FileUtility.getFileMessage(CHANGE_TEXTS_MENU_PATHFILE), createInlineKeyboard());
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.CHANGE_TEXTS;
    }

    private List<List<ElementModel>> createInlineKeyboard() {
        return List.of(
                List.of(KeyboardsProperties.createInlineButton(SPORT_ORG_TEXT)),
                List.of(KeyboardsProperties.createInlineButton(EVENTS_DISTRIBUTION_TEXT)),
                List.of(KeyboardsProperties.createInlineButton(COMPETITIONS_DISTRIBUTION_TEXT)),
                List.of(KeyboardsProperties.createInlineButton(SCHEDULE_NEWS_DISTRIBUTION_TEXT)),
                List.of(KeyboardsProperties.createInlineButton(GENERAL_DISTRIBUTION_TEXT)),
                List.of(KeyboardsProperties.createInlineButton(GIVEAWAY_TEXT))
        );
    }
}
