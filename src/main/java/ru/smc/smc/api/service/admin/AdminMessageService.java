package ru.smc.smc.api.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.model.request.MessageRequestBody;
import ru.smc.smc.api.domain.model.response.UserResponseItem;
import ru.smc.smc.api.entity.BotUser;
import ru.smc.smc.api.utilities.UserUtility;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMessageService {
    
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        if (!UserUtility.isUserAdmin(user)) {
            return formForbiddenResponse(user);
        }

        return new UserResponseItem();
    }

    private UserResponseItem formForbiddenResponse(BotUser user) {
        log.warn("Пользователь {} не имеет прав к пользованию функциями администратора (платформа - {})." +
                        "Внутренний id: {}",
                user.getIdOnPlatform(),
                user.getPlatform(),
                user.getInnerId());

        return new UserResponseItem();
    }
}
