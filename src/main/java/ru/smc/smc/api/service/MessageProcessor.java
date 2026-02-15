package ru.smc.smc.api.service;

import ru.smc.smc.api.domain.enums.MessageMeaningType;
import ru.smc.smc.api.domain.model.request.MessageRequestBody;
import ru.smc.smc.api.domain.model.response.UserResponseItem;
import ru.smc.smc.api.entity.BotUser;

public interface MessageProcessor {
    UserResponseItem processMessage(MessageRequestBody request, BotUser user);
    MessageMeaningType meaning();
}
