package ru.smc.smc.api.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.common.enums.MessageRoleType;
import ru.smc.smc.api.common.exceptions.UnauthorizedException;
import ru.smc.smc.api.common.model.request.MessageRequestBody;
import ru.smc.smc.api.common.model.response.UserResponseItem;
import ru.smc.smc.api.entity.BotUser;
import ru.smc.smc.api.entity.MessageHistory;
import ru.smc.smc.api.repository.BotUserRepository;
import ru.smc.smc.api.repository.MessageHistoryRepository;
import ru.smc.smc.api.service.admin.AdminMessageService;
import ru.smc.smc.api.service.response.ResponseService;
import ru.smc.smc.api.utilities.MessageDescriptor;
import ru.smc.smc.api.utilities.UserUtility;

import static ru.smc.smc.api.common.constant.ErrorsMessages.INVALID_API_KEY;

/*
Сервис для произведения операций, общей для обоих сервисов и перенаправления сообщения в нужный сервис 
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ExtensionMethod(MessageDescriptor.class)
public class MessageProcessorService {

    private final AdminMessageService adminMessageService;
    private final MessageHistoryRepository messageHistoryRepository;
    private final BotUserRepository botUserRepository;
    private final UserService userService;
    private final ResponseService responseService;

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
            return responseService.createForbiddenAccessMessage(user);
        }

        // проверка сообщения на сообщения-триггеры возвращения в главное меню
        if (request.getMessage().isReturnMessage()) {
            return responseService.createReturnToMainMenuMessage(user);
        }

        return messageRoleType == MessageRoleType.ADMIN ? adminMessageService.processMessage(request, user) : null;
    }

    private boolean isApiKeyValid(String apiKey) {
        return apiKey.equals(validApiKey);
    }

    // первичная обработка сообщения: логирование, сохранение в историческую таблицу и обновление статистики
    private void primaryProcessingMessage(MessageRequestBody request, MessageRoleType messageRoleType, BotUser user) {
        logIncomingMessage(request, messageRoleType);
        saveMessageToHistory(request, messageRoleType);
        updateStats(user);
    }

    private void updateStats(BotUser user) {
        Long userMessages = user.getMessageSent() + 1;
        user.setMessageSent(userMessages);
        botUserRepository.save(user);
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
