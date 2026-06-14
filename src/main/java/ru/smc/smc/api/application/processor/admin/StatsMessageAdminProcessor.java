package ru.smc.smc.api.application.processor.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.application.service.stats.StatsService;
import ru.smc.smc.api.domain.entity.BotUser;

@Service
@RequiredArgsConstructor
public class StatsMessageAdminProcessor implements MessageAdminProcessor {

    private final ResponseService responseService;
    private final StatsService statsService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        return responseService.createUserResponse(user, user.getCurrentState(), statsService.getBotStatsInfo());
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.STATS;
    }
}
