package ru.smc.smc.api.application.service.processors.user;

import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.domain.entity.BotUser;

public interface MessageUserProcessor {
    UserResponseItem processMessage(MessageRequestBody request, BotUser user);
    MessageMeaningType meaning();
}
