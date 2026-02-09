package ru.smc.smc.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.enums.MessageType;
import ru.smc.smc.api.domain.model.request.MessageRequestBody;
import ru.smc.smc.api.domain.model.response.MessageResponse;
import ru.smc.smc.api.entity.MessageHistory;
import ru.smc.smc.api.repository.MessageHistoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProcessorService {

    private final AdminMessageService adminMessageService;
    private final MessageHistoryRepository messageHistoryRepository;

    public MessageResponse processMessage(MessageRequestBody request, MessageType messageType) {
        primaryProcessingMessage(request, messageType);

        return messageType == MessageType.ADMIN ? adminMessageService.processMessage(request) : null;
    }

    private void primaryProcessingMessage(MessageRequestBody request, MessageType messageType) {
        logIncomingMessage(request, messageType);
        saveMessageToHistory(request, messageType);
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
