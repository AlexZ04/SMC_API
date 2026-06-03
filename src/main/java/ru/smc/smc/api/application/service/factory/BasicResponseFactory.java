package ru.smc.smc.api.application.service.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.constant.FeatureToggles;
import ru.smc.smc.api.application.common.enums.DistributionGroups;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.service.featuretoggle.FeatureToggleService;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.application.service.user.UserMessageToAdminsFormatter;
import ru.smc.smc.api.application.utilities.FileUtility;
import ru.smc.smc.api.domain.entity.BotUser;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicResponseFactory {

    private static final String UNKNOWN_MESSAGE_DISTRIBUTION_HEADER = "Пользователь отправил неопознанное сообщение";

    private final ResponseService responseService;
    private final FeatureToggleService featureToggleService;
    private final UserMessageToAdminsFormatter userMessageToAdminsFormatter;

    public UserResponseItem formErrorResponse(BotUser user) {
        return responseService.createUserResponse(user, user.getCurrentState(), FileUtility.getFileMessage("dont-understand-message"));
    }

    public UserResponseItem formErrorResponse(MessageRequestBody request, BotUser user) {
        if (!featureToggleService.isToggleActive(FeatureToggles.UNKNOWN_MESSAGE_DISTRIBUTION)) {
            return formErrorResponse(user);
        }

        return responseService.createUserResponseWithDistribution(user, user.getCurrentState(),
                FileUtility.getFileMessage("dont-understand-message"),
                List.of(),
                userMessageToAdminsFormatter.formMessage(UNKNOWN_MESSAGE_DISTRIBUTION_HEADER, request, user),
                DistributionGroups.ADMINS,
                false);
    }
}
