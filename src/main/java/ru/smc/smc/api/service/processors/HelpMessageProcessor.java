package ru.smc.smc.api.service.processors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.constant.BotCommands;
import ru.smc.smc.api.domain.enums.MessageMeaningType;
import ru.smc.smc.api.domain.enums.UserState;
import ru.smc.smc.api.domain.model.request.MessageRequestBody;
import ru.smc.smc.api.domain.model.response.UserResponseItem;
import ru.smc.smc.api.entity.BotUser;
import ru.smc.smc.api.service.MessageProcessor;
import ru.smc.smc.api.service.response.ResponseService;
import ru.smc.smc.api.utilities.FileUtility;

@Service
@RequiredArgsConstructor
public class HelpMessageProcessor implements MessageProcessor {
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
