package ru.smc.smc.api.application.service.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.constant.FeatureToggles;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.response.MessageResponse;
import ru.smc.smc.api.application.properties.KeyboardsProperties;
import ru.smc.smc.api.application.service.FeatureToggleService;

/*
Сервис для создания модели клавиатуры, возвращаемой пользователю
 */
@Service
@RequiredArgsConstructor
public class ResponseUIService {
    private final FeatureToggleService featureToggleService;

    public void createResponseKeyboard(UserState userState, MessageResponse.MessageResponseBuilder messageResponseBuilder) {
        switch (userState) {
            case MAIN_MENU -> {
                makeMainKeyboard(messageResponseBuilder);
            }
            default -> {
                makeBackToBotKeyboard(messageResponseBuilder);
            }
        }
    }

    private void makeMainKeyboard(MessageResponse.MessageResponseBuilder messageResponseBuilder) {
        if (featureToggleService.isToggleActive(FeatureToggles.GIVEAWAY)) {
            messageResponseBuilder.addReplyButton(KeyboardsProperties.PARTICIPATE_IN_GIVEAWAY);
            messageResponseBuilder.addReplyRow(KeyboardsProperties.ASK_QUESTION_BUTTON, KeyboardsProperties.SET_UP_DISTRIBUTION_BUTTON);
        } else {
            messageResponseBuilder.addReplyButton(KeyboardsProperties.ASK_QUESTION_BUTTON);
            messageResponseBuilder.addReplyButton(KeyboardsProperties.SET_UP_DISTRIBUTION_BUTTON);
        }

        messageResponseBuilder.addReplyRow(KeyboardsProperties.FEEDBACK_LINK, KeyboardsProperties.HELP_BUTTON);
    }

    private void makeBackToBotKeyboard(MessageResponse.MessageResponseBuilder messageResponseBuilder) {
        messageResponseBuilder.addReplyButton(KeyboardsProperties.BACK_TO_BOT_BUTTON);
    }
}
