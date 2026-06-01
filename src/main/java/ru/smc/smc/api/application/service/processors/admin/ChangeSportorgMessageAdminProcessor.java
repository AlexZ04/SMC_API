package ru.smc.smc.api.application.service.processors.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.service.faculty.FacultyService;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.domain.entity.BotUser;

@Service
@RequiredArgsConstructor
public class ChangeSportorgMessageAdminProcessor implements MessageAdminProcessor {

    private final ResponseService responseService;
    private final FacultyService facultyService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        return responseService.createUserResponse(user, UserState.CHANGE_SPORTORG,
                facultyService.getFacultiesChoiceMessage());
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.CHANGE_SPORTORG;
    }
}
