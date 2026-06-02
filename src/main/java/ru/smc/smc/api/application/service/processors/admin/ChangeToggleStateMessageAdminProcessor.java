package ru.smc.smc.api.application.service.processors.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.service.featuretoggle.FeatureToggleService;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.domain.entity.BotUser;

@Service
@RequiredArgsConstructor
public class ChangeToggleStateMessageAdminProcessor implements MessageAdminProcessor {

    private static final String CHANGE_TOGGLE_STATE_MESSAGE = "Введите наименование тоггла для изменения состояния:\n";
    private static final String TOGGLE_STATE_CHANGED_MESSAGE_FORMAT = "Состояние тоггла %s изменено на: %s";

    private final ResponseService responseService;
    private final FeatureToggleService featureToggleService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        if (user.getCurrentState() == UserState.CHANGE_TOGGLE_STATE) {
            boolean active = featureToggleService.changeToggleStatusToOpposite(request.getMessage().trim());

            return responseService.createUserResponse(user, UserState.MAIN_MENU,
                    String.format(TOGGLE_STATE_CHANGED_MESSAGE_FORMAT, request.getMessage().trim(),
                            active ? "Включен" : "Выключен"));
        }

        return responseService.createUserResponse(user, UserState.CHANGE_TOGGLE_STATE,
                CHANGE_TOGGLE_STATE_MESSAGE + featureToggleService.getSystemTogglesInfo());
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.CHANGE_TOGGLE_STATE;
    }
}
