package ru.smc.smc.api.application.processor.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.domain.entity.BotUser;

@Service
@RequiredArgsConstructor
public class StartMessageUserProcessor implements MessageUserProcessor {

    private static final String RESPONSE_TEXT = "Привет! Это пользовательская панель";

    private final ResponseService responseService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        return responseService.createUserResponse(user, UserState.MAIN_MENU, RESPONSE_TEXT);
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.START;
    }
}
