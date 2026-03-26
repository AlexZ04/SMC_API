package ru.smc.smc.api.application.service.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.application.utilities.FileUtility;
import ru.smc.smc.api.domain.entity.BotUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorResponseFactory {

    private final ResponseService responseService;

    public UserResponseItem formErrorResponse(BotUser user) {
        return responseService.createUserResponse(user, user.getCurrentState(), FileUtility.getFileMessage("dont-understand-message"));
    }
}
