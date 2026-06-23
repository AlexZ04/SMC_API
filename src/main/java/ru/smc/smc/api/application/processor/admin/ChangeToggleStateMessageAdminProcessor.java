package ru.smc.smc.api.application.processor.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.service.featuretoggle.FeatureToggleService;
import ru.smc.smc.api.application.service.monitoring.MonitoringEventService;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.domain.entity.BotUser;

@Service
@RequiredArgsConstructor
public class ChangeToggleStateMessageAdminProcessor implements MessageAdminProcessor {

    private static final String CHANGE_TOGGLE_STATE_MESSAGE = "Введи наименование тоггла, состояние которого нужно изменить:\n";
    private static final String TOGGLE_STATE_CHANGED_MESSAGE_FORMAT = "Состояние тоггла %s изменено: %s";
    private static final String TOGGLE_NOT_FOUND_MESSAGE_FORMAT = "Тоггл %s не найден. Введи наименование тоггла из списка:\n";

    private final ResponseService responseService;
    private final FeatureToggleService featureToggleService;
    private final MonitoringEventService monitoringEventService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        if (user.getCurrentState() == UserState.CHANGE_TOGGLE_STATE) {
            String toggleName = request.getMessage().trim();
            return featureToggleService.changeToggleStatusToOppositeIfExists(toggleName)
                    .map(active -> processToggleChanged(user, toggleName, active))
                    .orElseGet(() -> responseService.createUserResponse(user, UserState.CHANGE_TOGGLE_STATE,
                            String.format(TOGGLE_NOT_FOUND_MESSAGE_FORMAT, toggleName) + featureToggleService.getSystemTogglesInfo()));
        }

        return responseService.createUserResponse(user, UserState.CHANGE_TOGGLE_STATE,
                CHANGE_TOGGLE_STATE_MESSAGE + featureToggleService.getSystemTogglesInfo());
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.CHANGE_TOGGLE_STATE;
    }

    private UserResponseItem processToggleChanged(BotUser user, String toggleName, boolean active) {
        monitoringEventService.sendInfo(user, "Переключён тоггл " + toggleName + " пользователем {getName(" +
                user.getIdOnPlatform() + ")}. Новое состояние: " + (active ? "Включен" : "Выключен"));

        return responseService.createUserResponse(user, UserState.MAIN_MENU,
                String.format(TOGGLE_STATE_CHANGED_MESSAGE_FORMAT, toggleName,
                        active ? "Включен" : "Выключен"));
    }
}
