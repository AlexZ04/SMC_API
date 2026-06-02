package ru.smc.smc.api.application.service.response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.DistributionGroups;
import ru.smc.smc.api.application.common.enums.ResponseStatus;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.response.*;
import ru.smc.smc.api.application.service.user.UserService;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.application.properties.ResponseMessagesProperties;
import ru.smc.smc.api.domain.repository.BotUserRepository;
import ru.smc.smc.api.application.service.stats.StatsService;

import java.util.List;

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
    private final UserService userService;

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

    public UserResponseItem createUserResponseWithInlineKeyboard(BotUser user, UserState nextState,
                                                                 String responseMessage, List<List<ElementModel>> inlineElements) {
        updateUserState(user, nextState);

        var responseBuilder = formPrimaryResponseInfoBuilder(user);

        var messageResponseBuilder = MessageResponse.builder()
                .responseText(responseMessage);

        responseUIService.createResponseKeyboard(nextState, messageResponseBuilder, user.getRole());
        responseUIService.createInlineKeyboard(messageResponseBuilder, inlineElements);

        responseBuilder.responseToUser(messageResponseBuilder.build());
        UserResponseItem finalResponse = responseBuilder.build();
        statsService.updateBotStats(finalResponse);

        return finalResponse;
    }

    public UserResponseItem createUserResponseWithDistribution(BotUser user, UserState nextState, String responseMessage,
                                                               List<List<ElementModel>> inlineElements,
                                                               String distributionText,
                                                               DistributionGroups group, boolean sendToHimself,
                                                               List<List<ElementModel>> inlineDistributionElements) {
        updateUserState(user, nextState);

        var responseBuilder = formPrimaryResponseInfoBuilder(user);

        var messageResponseBuilder = MessageResponse.builder()
                .responseText(responseMessage);

        responseUIService.createResponseKeyboard(nextState, messageResponseBuilder, user.getRole());
        responseUIService.createInlineKeyboard(messageResponseBuilder, inlineElements);

        MessageResponse messageResponse = messageResponseBuilder.build();

        DistributionResponse distributionResponse = new DistributionResponse();
        distributionResponse.setPreviewMessages(messageResponse.getPreviewMessages());
        distributionResponse.setInlineElements(messageResponse.getInlineElements());
        distributionResponse.setReplyElements(messageResponse.getReplyElements());
        distributionResponse.setResponseText(messageResponse.getResponseText());
        distributionResponse.setDistribution(new DistributionModel());
        distributionResponse.getDistribution().setDistributionText(distributionText);
        distributionResponse.getDistribution().setSendToHimself(sendToHimself);
        distributionResponse.getDistribution().setReceivers(userService.findBotUsersByGroup(group));
        distributionResponse.getDistribution().setDistributionInlineElements(inlineDistributionElements);

        responseBuilder.responseToUser(distributionResponse);

        UserResponseItem finalResponse = responseBuilder.build();
        statsService.updateBotStats(finalResponse);

        return finalResponse;
    }

    public UserResponseItem createUserResponseWithPreviewMessages(BotUser user, UserState nextState, String responseMessage,
                                                                  List<String> previewMessages) {
        updateUserState(user, nextState);

        var responseBuilder = formPrimaryResponseInfoBuilder(user);

        var messageResponseBuilder = MessageResponse.builder()
                .responseText(responseMessage);

        previewMessages.forEach(messageResponseBuilder::addPreviewMessage);
        responseUIService.createResponseKeyboard(nextState, messageResponseBuilder, user.getRole());

        responseBuilder.responseToUser(messageResponseBuilder.build());
        UserResponseItem finalResponse = responseBuilder.build();
        statsService.updateBotStats(finalResponse);

        return finalResponse;
    }

    public UserResponseItem createUserResponseWithDistributionReceivers(BotUser user, UserState nextState, String responseMessage,
                                                                        String distributionText, boolean sendToHimself,
                                                                        List<PlatformReceiver> receivers) {
        updateUserState(user, nextState);

        var responseBuilder = formPrimaryResponseInfoBuilder(user);

        var messageResponseBuilder = MessageResponse.builder()
                .responseText(responseMessage);

        responseUIService.createResponseKeyboard(nextState, messageResponseBuilder, user.getRole());

        MessageResponse messageResponse = messageResponseBuilder.build();

        DistributionResponse distributionResponse = new DistributionResponse();
        distributionResponse.setPreviewMessages(messageResponse.getPreviewMessages());
        distributionResponse.setInlineElements(messageResponse.getInlineElements());
        distributionResponse.setReplyElements(messageResponse.getReplyElements());
        distributionResponse.setResponseText(messageResponse.getResponseText());
        distributionResponse.setDistribution(new DistributionModel());
        distributionResponse.getDistribution().setDistributionText(distributionText);
        distributionResponse.getDistribution().setSendToHimself(sendToHimself);
        distributionResponse.getDistribution().setReceivers(receivers);

        responseBuilder.responseToUser(distributionResponse);

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
