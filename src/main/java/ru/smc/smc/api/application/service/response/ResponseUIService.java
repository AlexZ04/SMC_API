package ru.smc.smc.api.application.service.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.constant.FeatureToggles;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.response.ElementModel;
import ru.smc.smc.api.application.common.model.response.MessageResponse;
import ru.smc.smc.api.application.properties.KeyboardsProperties;
import ru.smc.smc.api.application.service.featuretoggle.FeatureToggleService;
import ru.smc.smc.api.application.utilities.UserUtility;
import ru.smc.smc.api.domain.entity.BotUser;

import java.util.ArrayList;
import java.util.List;

/*
Сервис для создания модели клавиатуры, возвращаемой пользователю
 */
@Service
@RequiredArgsConstructor
public class ResponseUIService {

    private static final String ADMIN_CHANNEL = "admin-channel";

    private final FeatureToggleService featureToggleService;

    public void createResponseKeyboard(UserState userState, MessageResponse.MessageResponseBuilder messageResponseBuilder, BotUser user) {
        // возврат клавиатуры для пользователя
        if (!isAdminKeyboardAvailable(user)) {
            switch (userState.getKeyboardCode()) {
                case 0 -> {
                    makeMainKeyboard(messageResponseBuilder);
                }
                default -> {
                    makeBackToBotKeyboard(messageResponseBuilder);
                }
            }

            return;
        }

        // возврат клавиатуры для администратора
        switch (userState.getKeyboardCode()) {
            case 0 -> {
                makeAdminMainKeyboard(messageResponseBuilder);
            }
            default -> {
                makeBackToBotKeyboard(messageResponseBuilder);
            }
        }
    }

    public void createInlineKeyboard(MessageResponse.MessageResponseBuilder messageResponseBuilder, List<List<ElementModel>> inlineElements) {
        for (List<ElementModel> inlineElement : inlineElements) {
            messageResponseBuilder.addInlineRow(inlineElement);
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

    private void makeAdminMainKeyboard(MessageResponse.MessageResponseBuilder messageResponseBuilder) {
        messageResponseBuilder.addReplyRow(KeyboardsProperties.ADMIN_CHANGE_SPORTORG_BUTTON,
                KeyboardsProperties.ADMIN_CHANGE_TEXTS_BUTTON);
        messageResponseBuilder.addReplyRow(KeyboardsProperties.ADMIN_SEND_DISTRIBUTION_BUTTON,
                KeyboardsProperties.ADMIN_CHANGE_TOGGLE_STATE_BUTTON);
        messageResponseBuilder.addReplyRow(KeyboardsProperties.ADMIN_STATS_BUTTON,
                KeyboardsProperties.ADMIN_HELP_BUTTON);
    }

    public List<List<ElementModel>> makeKeyboard(UserState userState, BotUser user) {
        // возврат клавиатуры для пользователя
        if (!isAdminKeyboardAvailable(user)) {
            switch (userState.getKeyboardCode()) {
                case 0 -> {
                    return makeMainKeyboard();
                }
                default -> {
                    return makeBackToBotKeyboard();
                }
            }
        }

        // возврат клавиатуры для администратора
        switch (userState.getKeyboardCode()) {
            case 0 -> {
                return makeAdminMainKeyboard();
            }
            default -> {
                return makeBackToBotKeyboard();
            }
        }
    }

    private boolean isAdminKeyboardAvailable(BotUser user) {
        return UserUtility.isUserAdmin(user) && ADMIN_CHANNEL.equals(user.getUserChannel());
    }

    private List<List<ElementModel>> makeMainKeyboard() {
        List<List<ElementModel>> keyboard = new ArrayList<>();

        if (featureToggleService.isToggleActive(FeatureToggles.GIVEAWAY)) {
            keyboard.add(List.of(KeyboardsProperties.PARTICIPATE_IN_GIVEAWAY));
            keyboard.add(List.of(
                    KeyboardsProperties.ASK_QUESTION_BUTTON,
                    KeyboardsProperties.SET_UP_DISTRIBUTION_BUTTON
            ));
        } else {
            keyboard.add(List.of(KeyboardsProperties.ASK_QUESTION_BUTTON));
            keyboard.add(List.of(KeyboardsProperties.SET_UP_DISTRIBUTION_BUTTON));
        }

        keyboard.add(List.of(
                KeyboardsProperties.FEEDBACK_LINK,
                KeyboardsProperties.HELP_BUTTON
        ));

        return keyboard;
    }

    private List<List<ElementModel>> makeBackToBotKeyboard() {
        return List.of(
                List.of(KeyboardsProperties.BACK_TO_BOT_BUTTON)
        );
    }

    private List<List<ElementModel>> makeAdminMainKeyboard() {
        return List.of(
                List.of(KeyboardsProperties.ADMIN_CHANGE_SPORTORG_BUTTON, KeyboardsProperties.ADMIN_CHANGE_TEXTS_BUTTON),
                List.of(KeyboardsProperties.ADMIN_SEND_DISTRIBUTION_BUTTON, KeyboardsProperties.ADMIN_CHANGE_TOGGLE_STATE_BUTTON),
                List.of(KeyboardsProperties.ADMIN_STATS_BUTTON, KeyboardsProperties.ADMIN_HELP_BUTTON)
        );
    }
}
