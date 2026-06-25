package ru.smc.smc.api.application.processor.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.constant.AdminTextSettingsTexts;
import ru.smc.smc.api.application.common.enums.AdminDistributionType;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.ElementModel;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.properties.KeyboardsProperties;
import ru.smc.smc.api.application.service.distribution.DistributionFileService;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.application.utility.FileUtility;
import ru.smc.smc.api.domain.entity.BotUser;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangeTextsMessageAdminProcessor implements MessageAdminProcessor {

    private static final String CHANGE_TEXTS_MENU_PATHFILE = "admin-change-texts";
    private static final String CHANGE_TEXTS_INPUT_MESSAGE_PATHFILE = "admin-change-texts-input-message";
    private static final String DISTRIBUTION_UPDATED_MESSAGE = "Сообщение для рассылки обновлено, спасибо!";

    private final ResponseService responseService;
    private final DistributionFileService distributionFileService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        if (isDistributionTextChangingState(user.getCurrentState())) {
            AdminDistributionType distributionType = defineDistributionType(user.getCurrentState());

            request.validateAttachments();
            FileUtility.writeCustomizableFileMessage(distributionType.getPathFile(), defineDistributionText(request));
            distributionFileService.replaceFiles(distributionType, request.getAttachments());
            log.info("Администратор ({}, {}) обновил сообщение рассылки {}. Количество вложений: {}",
                    user.getPlatform(), user.getIdOnPlatform(), distributionType, request.getAttachments().size());

            return responseService.createUserResponse(user, UserState.MAIN_MENU, DISTRIBUTION_UPDATED_MESSAGE);
        }

        if (user.getCurrentState() == UserState.CHANGE_TEXTS && AdminTextSettingsTexts.isDistributionText(request.getMessage().trim())) {
            return responseService.createUserResponse(user, defineNextState(request.getMessage().trim()),
                    String.format(FileUtility.getFileMessage(CHANGE_TEXTS_INPUT_MESSAGE_PATHFILE), request.getMessage().trim()));
        }

        return responseService.createUserResponseWithInlineKeyboard(user, UserState.CHANGE_TEXTS,
                FileUtility.getFileMessage(CHANGE_TEXTS_MENU_PATHFILE), createInlineKeyboard());
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.CHANGE_TEXTS;
    }

    private List<List<ElementModel>> createInlineKeyboard() {
        return List.of(
                List.of(KeyboardsProperties.createInlineButton(AdminTextSettingsTexts.EVENTS_DISTRIBUTION_TEXT)),
                List.of(KeyboardsProperties.createInlineButton(AdminTextSettingsTexts.COMPETITIONS_DISTRIBUTION_TEXT)),
                List.of(KeyboardsProperties.createInlineButton(AdminTextSettingsTexts.SCHEDULE_NEWS_DISTRIBUTION_TEXT)),
                List.of(KeyboardsProperties.createInlineButton(AdminTextSettingsTexts.GENERAL_DISTRIBUTION_TEXT)),
                List.of(KeyboardsProperties.createInlineButton(AdminTextSettingsTexts.GIVEAWAY_TEXT))
        );
    }

    private UserState defineNextState(String message) {
        if (message.equalsIgnoreCase(AdminTextSettingsTexts.EVENTS_DISTRIBUTION_TEXT)) {
            return UserState.CHANGE_EVENTS_DISTRIBUTION_TEXT;
        }

        if (message.equalsIgnoreCase(AdminTextSettingsTexts.COMPETITIONS_DISTRIBUTION_TEXT)) {
            return UserState.CHANGE_COMPETITIONS_DISTRIBUTION_TEXT;
        }

        if (message.equalsIgnoreCase(AdminTextSettingsTexts.SCHEDULE_NEWS_DISTRIBUTION_TEXT)) {
            return UserState.CHANGE_SCHEDULE_NEWS_DISTRIBUTION_TEXT;
        }

        if (message.equalsIgnoreCase(AdminTextSettingsTexts.GENERAL_DISTRIBUTION_TEXT)) {
            return UserState.CHANGE_GENERAL_DISTRIBUTION_TEXT;
        }

        return UserState.CHANGE_GIVEAWAY_TEXT;
    }

    private boolean isDistributionTextChangingState(UserState currentState) {
        return currentState == UserState.CHANGE_EVENTS_DISTRIBUTION_TEXT ||
                currentState == UserState.CHANGE_COMPETITIONS_DISTRIBUTION_TEXT ||
                currentState == UserState.CHANGE_SCHEDULE_NEWS_DISTRIBUTION_TEXT ||
                currentState == UserState.CHANGE_GENERAL_DISTRIBUTION_TEXT ||
                currentState == UserState.CHANGE_GIVEAWAY_TEXT;
    }

    private AdminDistributionType defineDistributionType(UserState currentState) {
        if (currentState == UserState.CHANGE_EVENTS_DISTRIBUTION_TEXT) {
            return AdminDistributionType.EVENTS;
        }

        if (currentState == UserState.CHANGE_COMPETITIONS_DISTRIBUTION_TEXT) {
            return AdminDistributionType.COMPETITIONS;
        }

        if (currentState == UserState.CHANGE_SCHEDULE_NEWS_DISTRIBUTION_TEXT) {
            return AdminDistributionType.SCHEDULE;
        }

        if (currentState == UserState.CHANGE_GENERAL_DISTRIBUTION_TEXT) {
            return AdminDistributionType.GENERAL;
        }

        return AdminDistributionType.GIVEAWAY;
    }

    private String defineDistributionText(MessageRequestBody request) {
        if (request.getMessage() == null) {
            return "";
        }

        return request.getMessage();
    }
}
