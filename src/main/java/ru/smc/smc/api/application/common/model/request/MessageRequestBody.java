package ru.smc.smc.api.application.common.model.request;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import ru.smc.smc.api.application.common.enums.AvailablePlatform;
import ru.smc.smc.api.application.common.enums.MessageRoleType;
import ru.smc.smc.api.domain.entity.MessageHistory;

import java.util.ArrayList;
import java.util.List;

@Data
public class MessageRequestBody {
    private String message;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailablePlatform platform;
    private String userIdOnPlatform;
    private int attachmentsAmount = 0;
    private List<MessageFileRequest> attachments = new ArrayList<>();

    // todo: сделать поле для картинок и стикеров

    public MessageHistory createHistoryMessage(MessageRoleType messageRoleType) {
        MessageHistory messageHistory = new MessageHistory();

        messageHistory.setMessage(message);
        messageHistory.setPlatform(platform);
        messageHistory.setUserIdOnPlatform(userIdOnPlatform);
        messageHistory.setAttachmentsAmount(defineAttachmentsAmount());
        messageHistory.setMessageType(messageRoleType);

        return messageHistory;
    }

    private int defineAttachmentsAmount() {
        if (attachments == null || attachments.isEmpty()) {
            return attachmentsAmount;
        }

        return attachments.size();
    }
}
