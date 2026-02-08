package ru.smc.smc.api.service;

import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.model.request.MessageRequestBody;
import ru.smc.smc.api.domain.model.response.MessageResponse;
import ru.smc.smc.api.entity.BotUser;
import ru.smc.smc.api.utilities.UserUtility;

@Service
public class AdminMessageService {

    private UserService userService;

    public MessageResponse processMessage(MessageRequestBody request) {
        BotUser user = userService.findOrCreateBotUser(request.getPlatform(), request.getUserIdOnPlatform());

        if (!UserUtility.isUserAdmin(user)) {
            return formForbiddenResponse(user);
        }

        return new MessageResponse();
    }

    private MessageResponse formForbiddenResponse(BotUser user) {
        return new MessageResponse();
    }
}
