package ru.smc.smc.api.application.service.processors.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.ElementModel;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.properties.KeyboardsProperties;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.application.utilities.FileUtility;
import ru.smc.smc.api.domain.entity.BotUser;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HelpMessageUserProcessor implements MessageUserProcessor {

    private final ResponseService responseService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        List<List<ElementModel>> inlineElements = List.of(
                List.of(KeyboardsProperties.SET_MY_FACULTY_BUTTON)
        );

        return responseService.createUserResponseWithInlineKeyboard(user, UserState.MAIN_MENU, FileUtility.getFileMessage("user-help"),
                inlineElements);
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.HELP;
    }
}
