package ru.smc.smc.api.application.processor.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.application.service.sportorg.SportorgService;
import ru.smc.smc.api.domain.entity.BotUser;

@Service
@RequiredArgsConstructor
public class GetAllSportorgsMessageUserProcessor implements MessageUserProcessor {

    private final ResponseService responseService;
    private final SportorgService sportorgService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        return responseService.createUserResponse(user, UserState.MAIN_MENU, sportorgService.getAllSportorgsMessage());
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.GET_ALL_SPORTORGS;
    }
}
