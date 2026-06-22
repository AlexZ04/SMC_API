package ru.smc.smc.api.application.service.user;

import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.domain.entity.BotUser;

@Service
public class UserMessageToAdminsFormatter {

    private static final String MESSAGE_FORMAT = """
            %s
            Платформа: %s
            Канал: %s
            Имя: {getName()}
            Идентификатор: {getId()}
            Ссылка: {getLink()}
            Сообщение пользователя: %s""";

    public String formMessage(String header, MessageRequestBody request, BotUser user) {
        return String.format(MESSAGE_FORMAT,
                header,
                request.getPlatform(),
                user.getUserChannel(),
                request.getMessage());
    }
}
