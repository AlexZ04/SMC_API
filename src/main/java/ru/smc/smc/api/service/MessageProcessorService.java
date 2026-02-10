package ru.smc.smc.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.enums.MessageType;
import ru.smc.smc.api.domain.exceptions.UnauthorizedException;
import ru.smc.smc.api.domain.model.request.MessageRequestBody;
import ru.smc.smc.api.domain.model.response.MessageResponse;
import ru.smc.smc.api.entity.BotUser;
import ru.smc.smc.api.entity.MessageHistory;
import ru.smc.smc.api.repository.BotUserRepository;
import ru.smc.smc.api.repository.MessageHistoryRepository;

import static ru.smc.smc.api.domain.constant.ErrorsMessages.INVALID_API_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProcessorService {

    private final AdminMessageService adminMessageService;
    private final MessageHistoryRepository messageHistoryRepository;
    private final BotUserRepository botUserRepository;
    private final UserService userService;

    @Value("${api-config.key}")
    private String validApiKey;

    public MessageResponse processMessage(MessageRequestBody request, MessageType messageType, String apiKey) {
        if (!isApiKeyValid(apiKey)) {
            throw new UnauthorizedException(INVALID_API_KEY);
        }

        BotUser user = userService.findOrCreateBotUser(request.getPlatform(), request.getUserIdOnPlatform());

        primaryProcessingMessage(request, messageType, user);

        return messageType == MessageType.ADMIN ? adminMessageService.processMessage(request, user) : null;
    }

    private boolean isApiKeyValid(String apiKey) {
        return apiKey.equals(validApiKey);
    }

    private void primaryProcessingMessage(MessageRequestBody request, MessageType messageType, BotUser user) {
        logIncomingMessage(request, messageType);
        saveMessageToHistory(request, messageType);
        updateStats(user);
    }

    private void updateStats(BotUser user) {
        Long userMessages = user.getMessageSent() + 1;
        user.setMessageSent(userMessages);
        botUserRepository.save(user);
    }

    private void saveMessageToHistory(MessageRequestBody request, MessageType messageType) {
        MessageHistory messageHistory = request.createHistoryMessage(messageType);

        messageHistoryRepository.save(messageHistory);
    }

    private void logIncomingMessage(MessageRequestBody request, MessageType messageType) {
        log.info("Получено новое сообщение типа {}: {}. Платформа: {}, id пользователя на платформе: {}, количество вложений: {}",
                messageType, request.getMessage(), request.getPlatform(), request.getUserIdOnPlatform(), request.getAttachmentsType());
    }
}
