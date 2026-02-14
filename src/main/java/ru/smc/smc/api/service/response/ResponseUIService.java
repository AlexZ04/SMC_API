package ru.smc.smc.api.service.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.constant.FeatureToggles;
import ru.smc.smc.api.domain.enums.UserState;
import ru.smc.smc.api.domain.model.response.MessageResponse;
import ru.smc.smc.api.properties.KeyboardsProperties;
import ru.smc.smc.api.service.FeatureToggleService;

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

            }
        }
    }

    private void makeMainKeyboard(MessageResponse.MessageResponseBuilder messageResponseBuilder) {
        if (featureToggleService.isToggleActive(FeatureToggles.GIVEAWAY)){
            messageResponseBuilder.addReplyButton(KeyboardsProperties.PARTICIPATE_IN_GIVEAWAY);
            messageResponseBuilder.addReplyRow(KeyboardsProperties.ASK_QUESTION_BUTTON, KeyboardsProperties.SET_UP_DISTRIBUTION_BUTTON);
        } else {
            messageResponseBuilder.addReplyButton(KeyboardsProperties.ASK_QUESTION_BUTTON);
            messageResponseBuilder.addReplyButton(KeyboardsProperties.SET_UP_DISTRIBUTION_BUTTON);
        }

        messageResponseBuilder.addReplyRow(KeyboardsProperties.FEEDBACK_LINK, KeyboardsProperties.HELP_BUTTON);
    }
}
