package ru.smc.smc.api.application.service.processors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.constant.BotCommands;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.application.service.MessageAdminProcessor;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.application.utilities.FileUtility;

@Service
@RequiredArgsConstructor
public class HelpMessageAdminProcessor implements MessageAdminProcessor {
    private final ResponseService responseService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        if (request.getMessage().toLowerCase().contains(BotCommands.HELP_COMMAND)) {
            return responseService.createUserResponse(user, UserState.MAIN_MENU, FileUtility.getFileMessage("admin-help"));
        }

        return null;
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.HELP;
    }
}
