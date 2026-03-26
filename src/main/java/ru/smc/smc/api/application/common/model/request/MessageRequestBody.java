package ru.smc.smc.api.application.common.model.request;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import ru.smc.smc.api.application.common.enums.AvailablePlatform;
import ru.smc.smc.api.application.common.enums.MessageRoleType;
import ru.smc.smc.api.domain.entity.MessageHistory;

@Data
public class MessageRequestBody {
    private String message;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailablePlatform platform;
    private String userIdOnPlatform;
    private int attachmentsAmount = 0;

    // todo: сделать поле для картинок и стикеров

    public MessageHistory createHistoryMessage(MessageRoleType messageRoleType) {
        MessageHistory messageHistory = new MessageHistory();

        messageHistory.setMessage(message);
        messageHistory.setPlatform(platform);
        messageHistory.setUserIdOnPlatform(userIdOnPlatform);
        messageHistory.setAttachmentsAmount(attachmentsAmount);
        messageHistory.setMessageType(messageRoleType);

        return messageHistory;
    }
}
