package ru.smc.smc.api.application.service.response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.ResponseStatus;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.response.MessageResponse;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.application.properties.ResponseMessagesProperties;
import ru.smc.smc.api.domain.repository.BotUserRepository;
import ru.smc.smc.api.application.service.stats.StatsService;

/*
Сервис для формирования ответа бота
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResponseService {
    private final ResponseMessagesProperties responseMessagesProperties;
    private final ResponseUIService responseUIService;
    private final StatsService statsService;
    private final BotUserRepository botUserRepository;

    public UserResponseItem createUserResponse(BotUser user, UserState nextState, String responseMessage) {
        updateUserState(user, nextState);

        var responseBuilder = formPrimaryResponseInfoBuilder(user);

        var messageResponseBuilder = MessageResponse.builder()
                .responseText(responseMessage);

        responseUIService.createResponseKeyboard(nextState, messageResponseBuilder, user.getRole());

        responseBuilder.responseToUser(messageResponseBuilder.build());
        UserResponseItem finalResponse = responseBuilder.build();
        statsService.updateBotStats(finalResponse);

        return finalResponse;
    }

    public UserResponseItem createReturnToMainMenuMessage(BotUser user) {
        updateUserState(user, UserState.MAIN_MENU);

        var responseBuilder = formPrimaryResponseInfoBuilder(user);

        var messageResponseBuilder = MessageResponse.builder()
                .responseText(responseMessagesProperties.getReturnToMainScreen());

        responseUIService.createResponseKeyboard(UserState.MAIN_MENU, messageResponseBuilder, user.getRole());

        responseBuilder.responseToUser(messageResponseBuilder.build());
        UserResponseItem finalResponse = responseBuilder.build();
        statsService.updateBotStats(finalResponse);

        return finalResponse;
    }

    public UserResponseItem createForbiddenAccessMessage(BotUser user) {
        updateUserState(user, UserState.MAIN_MENU);

        var responseBuilder = formPrimaryResponseInfoBuilder(user);

        var messageResponseBuilder = MessageResponse.builder()
                .responseText(responseMessagesProperties.getForbiddenAccess());

        responseUIService.createResponseKeyboard(UserState.MAIN_MENU, messageResponseBuilder, user.getRole());

        responseBuilder.responseToUser(messageResponseBuilder.build());
        UserResponseItem finalResponse = responseBuilder.build();
        statsService.updateBotStats(finalResponse);

        log.warn("Пользователь {} не имеет прав к пользованию функциями администратора (платформа - {})." +
                        "Внутренний id: {}",
                user.getIdOnPlatform(),
                user.getPlatform(),
                user.getInnerId());

        return finalResponse;
    }

    private UserResponseItem.UserResponseItemBuilder formPrimaryResponseInfoBuilder(BotUser user) {
        return UserResponseItem.builder()
                .status(ResponseStatus.OK)
                .platform(user.getPlatform())
                .userIdOnPlatform(user.getIdOnPlatform());
    }

    private void updateUserState(BotUser user, UserState userState) {
        user.setCurrentState(userState);
        botUserRepository.save(user);
    }
}
