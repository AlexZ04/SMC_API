package ru.smc.smc.api.application.processor.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.application.service.user.UserService;
import ru.smc.smc.api.domain.entity.BotUser;

@Service
@RequiredArgsConstructor
public class GetAdminsMessageAdminProcessor implements MessageAdminProcessor {

    private final ResponseService responseService;
    private final UserService userService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        return responseService.createUserResponse(user, user.getCurrentState(), userService.getAdminsInfo());
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.GET_ADMINS;
    }
}
