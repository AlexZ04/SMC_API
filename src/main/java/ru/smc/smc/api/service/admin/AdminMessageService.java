package ru.smc.smc.api.service.admin;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.common.enums.MessageMeaningType;
import ru.smc.smc.api.common.enums.MessageRoleType;
import ru.smc.smc.api.common.exceptions.NotFoundException;
import ru.smc.smc.api.common.model.request.MessageRequestBody;
import ru.smc.smc.api.common.model.response.UserResponseItem;
import ru.smc.smc.api.entity.BotUser;
import ru.smc.smc.api.service.MessageProcessor;
import ru.smc.smc.api.utilities.MessageDescriptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMessageService {

    private final List<MessageProcessor> processors;
    private Map<MessageMeaningType, MessageProcessor> processorsMap = new HashMap<>();

    @PostConstruct
    private void init() {
        processorsMap = processors.stream()
                .collect(Collectors.toUnmodifiableMap(MessageProcessor::meaning, Function.identity()));
    }

    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        var messageMeaning = MessageDescriptor.defineMessageMeaning(
                request.getMessage(), MessageRoleType.ADMIN, user.getCurrentState()
        );

        var processor = processorsMap.get(messageMeaning);

        if (processor == null) {
            throw new NotFoundException("Не найден обработчик сообщений для типа: " + messageMeaning);
        }

        return processor.processMessage(request, user);
    }
}
