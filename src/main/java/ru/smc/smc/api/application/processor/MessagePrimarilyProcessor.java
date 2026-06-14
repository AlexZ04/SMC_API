package ru.smc.smc.api.application.processor;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.MessageRoleType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.exceptions.UnauthorizedException;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.service.monitoring.MonitoringEventService;
import ru.smc.smc.api.application.service.user.UserService;
import ru.smc.smc.api.application.service.stats.StatsService;
import ru.smc.smc.api.application.service.thanks.ThanksMessageService;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.domain.entity.MessageHistory;
import ru.smc.smc.api.domain.repository.BotUserRepository;
import ru.smc.smc.api.domain.repository.MessageHistoryRepository;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.application.utilities.MessageDescriptor;
import ru.smc.smc.api.application.utilities.UserUtility;

import java.time.Instant;

import static ru.smc.smc.api.application.common.constant.ErrorsMessages.INVALID_API_KEY;

/*
Сервис для произведения операций, общей для обоих сервисов и перенаправления сообщения в нужный сервис 
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ExtensionMethod(MessageDescriptor.class)
public class MessagePrimarilyProcessor {
    private static final String ADMIN_CHANNEL = "admin-channel";
    private static final String USER_CHANNEL = "user-channel";
    private static final String BACK_BUTTON = "Назад";

    private final AdminMessageService adminMessageService;
    private final UserMessageService userMessageService;
    private final MessageHistoryRepository messageHistoryRepository;
    private final BotUserRepository botUserRepository;
    private final UserService userService;
    private final ResponseService responseService;
    private final StatsService statsService;
    private final ThanksMessageService thanksMessageService;
    private final MonitoringEventService monitoringEventService;

    @Value("${api-config.key}")
    private String validApiKey;

    public UserResponseItem processMessage(MessageRequestBody request, MessageRoleType messageRoleType, String apiKey) {
        if (!isApiKeyValid(apiKey)) {
            throw new UnauthorizedException(INVALID_API_KEY);
        }

        BotUser user = userService.findOrCreateBotUser(request.getPlatform(), request.getUserIdOnPlatform());

        primaryProcessingMessage(request, messageRoleType, user);

        // проверка на наличие прав у пользователя
        if (messageRoleType == MessageRoleType.ADMIN && !UserUtility.isUserAdmin(user)) {
            monitoringEventService.sendWarn(user, "Пользователь без прав администратора обратился к админскому эндпоинту. " +
                    "Платформа: " + user.getPlatform() + ". Идентификатор: " + user.getIdOnPlatform());
            return responseService.createForbiddenAccessMessage(user);
        }

        // проверка сообщения на сообщения-триггеры возвращения в главное меню
        if (request.getMessage().isReturnMessage() && !isUnsubscribeDistributionBackMessage(request, user)) {
            return responseService.createReturnToMainMenuMessage(user);
        }

        if (thanksMessageService.isThanksMessage(request.getMessage())) {
            return thanksMessageService.processThanksMessage(user);
        }

        return messageRoleType == MessageRoleType.ADMIN ? adminMessageService.processMessage(request, user) :
                userMessageService.processMessage(request, user);
    }

    private boolean isApiKeyValid(String apiKey) {
        return apiKey.equals(validApiKey);
    }

    private boolean isUnsubscribeDistributionBackMessage(MessageRequestBody request, BotUser user) {
        return user.getCurrentState() == UserState.UNSUBSCRIBE_DISTRIBUTION &&
                request.getMessage().equalsIgnoreCase(BACK_BUTTON);
    }

    // первичная обработка сообщения: логирование, сохранение в историческую таблицу и обновление статистики
    private void primaryProcessingMessage(MessageRequestBody request, MessageRoleType messageRoleType, BotUser user) {
        logIncomingMessage(request, messageRoleType);
        saveMessageToHistory(request, messageRoleType);
        statsService.updateIncomingMessageStats();
        updateUserInfo(user, messageRoleType);
    }

    private void updateUserInfo(BotUser user, MessageRoleType messageRoleType) {
        Long userMessages = user.getMessageSent() + 1;
        user.setMessageSent(userMessages);
        String userChannel = defineMessageChannel(messageRoleType);
        user.setUserChannel(userChannel);
        user.setUpdateTime(Instant.now());
        user.activateCurrentState(userChannel);
        botUserRepository.save(user);
    }

    private String defineMessageChannel(MessageRoleType messageRoleType) {
        if (messageRoleType == MessageRoleType.ADMIN) {
            return ADMIN_CHANNEL;
        }

        return USER_CHANNEL;
    }

    private void saveMessageToHistory(MessageRequestBody request, MessageRoleType messageRoleType) {
        MessageHistory messageHistory = request.createHistoryMessage(messageRoleType);

        messageHistoryRepository.save(messageHistory);
    }

    private void logIncomingMessage(MessageRequestBody request, MessageRoleType messageRoleType) {
        log.info("Получено новое сообщение типа '{}': {}. Платформа: {}, id пользователя на платформе: '{}', количество вложений: {}",
                messageRoleType, request.getMessage(), request.getPlatform(), request.getUserIdOnPlatform(), request.getAttachmentsAmount());
    }
}
