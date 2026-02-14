package ru.smc.smc.api.service.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.enums.ResponseStatus;
import ru.smc.smc.api.domain.enums.UserState;
import ru.smc.smc.api.domain.model.response.MessageResponse;
import ru.smc.smc.api.domain.model.response.UserResponseItem;
import ru.smc.smc.api.entity.BotUser;
import ru.smc.smc.api.properties.ResponseMessagesProperties;
import ru.smc.smc.api.service.StatsService;

/*
Сервис для формирования ответа бота
 */
@Service
@RequiredArgsConstructor
public class ResponseService {
    private final ResponseMessagesProperties responseMessagesProperties;
    private final ResponseUIService responseUIService;
    private final StatsService statsService;

    public UserResponseItem createUserResponse(BotUser user, UserState nextState, String responseMessage) {
        statsService.updateUserStats(user, nextState);

        var responseBuilder = formPrimaryResponseInfoBuilder(user);

        var messageResponseBuilder = MessageResponse.builder()
                .responseText(responseMessage);

        responseUIService.createResponseKeyboard(UserState.MAIN_MENU, messageResponseBuilder);

        responseBuilder.responseToUser(messageResponseBuilder.build());
        UserResponseItem finalResponse = responseBuilder.build();
        statsService.updateBotStats(finalResponse);

        return finalResponse;
    }

    public UserResponseItem createReturnToMainMenuMessage(BotUser user) {
        statsService.updateUserStats(user, UserState.MAIN_MENU);

        var responseBuilder = formPrimaryResponseInfoBuilder(user);

        var messageResponseBuilder = MessageResponse.builder()
                .responseText(responseMessagesProperties.getReturnToMainScreen());

        responseUIService.createResponseKeyboard(UserState.MAIN_MENU, messageResponseBuilder);

        responseBuilder.responseToUser(messageResponseBuilder.build());
        UserResponseItem finalResponse = responseBuilder.build();
        statsService.updateBotStats(finalResponse);

        return finalResponse;
    }

    public UserResponseItem createForbiddenAccessMessage(BotUser user) {
        statsService.updateUserStats(user, UserState.MAIN_MENU);

        var responseBuilder = formPrimaryResponseInfoBuilder(user);

        var messageResponseBuilder = MessageResponse.builder()
                .responseText(responseMessagesProperties.getForbiddenAccess());

        responseUIService.createResponseKeyboard(UserState.MAIN_MENU, messageResponseBuilder);

        responseBuilder.responseToUser(messageResponseBuilder.build());
        UserResponseItem finalResponse = responseBuilder.build();
        statsService.updateBotStats(finalResponse);

        return finalResponse;
    }

    private UserResponseItem.UserResponseItemBuilder formPrimaryResponseInfoBuilder(BotUser user) {
        return UserResponseItem.builder()
                .status(ResponseStatus.OK)
                .platform(user.getPlatform())
                .userIdOnPlatform(user.getIdOnPlatform());
    }
}
