package ru.smc.smc.api.service.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.constant.ResponseMessagesProperties;
import ru.smc.smc.api.domain.enums.ResponseStatus;
import ru.smc.smc.api.domain.enums.UserState;
import ru.smc.smc.api.domain.model.response.MessageResponse;
import ru.smc.smc.api.domain.model.response.UserResponseItem;
import ru.smc.smc.api.entity.BotUser;
import ru.smc.smc.api.repository.BotUserRepository;

/*
Сервис для формирования ответа бота
 */
@Service
@RequiredArgsConstructor
public class ResponseService {
    private final BotUserRepository botUserRepository;
    private final ResponseMessagesProperties responseMessagesProperties;

    public UserResponseItem createReturnToMainMenuMessage(BotUser user) {
        updateUserStaus(user, UserState.MAIN_MENU);

        var responseBuilder = formPrimaryResponseInfoBuilder(user);

        var messageResponse = MessageResponse.builder()
                .responseText(responseMessagesProperties.getReturnToMainScreen())
                .build();

        responseBuilder.responseToUser(messageResponse);

        return responseBuilder.build();
    }

    public UserResponseItem createForbiddenAccessMessage(BotUser user) {
        updateUserStaus(user, UserState.MAIN_MENU);

        var responseBuilder = formPrimaryResponseInfoBuilder(user);

        var messageResponse = MessageResponse.builder()
                .responseText(responseMessagesProperties.getForbiddenAccess())
                .build();

        responseBuilder.responseToUser(messageResponse);

        return responseBuilder.build();
    }

    private void updateUserStaus(BotUser user, UserState userState) {
        user.setCurrentState(userState);
        botUserRepository.save(user);
    }

    private UserResponseItem.UserResponseItemBuilder formPrimaryResponseInfoBuilder(BotUser user) {
        return UserResponseItem.builder()
                .status(ResponseStatus.OK)
                .platform(user.getPlatform())
                .userIdOnPlatform(user.getIdOnPlatform());
    }
}
