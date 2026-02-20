package ru.smc.smc.api.service;

import ru.smc.smc.api.common.enums.MessageMeaningType;
import ru.smc.smc.api.common.model.request.MessageRequestBody;
import ru.smc.smc.api.common.model.response.UserResponseItem;
import ru.smc.smc.api.entity.BotUser;

public interface MessageAdminProcessor {
    UserResponseItem processMessage(MessageRequestBody request, BotUser user);
    MessageMeaningType meaning();
}
