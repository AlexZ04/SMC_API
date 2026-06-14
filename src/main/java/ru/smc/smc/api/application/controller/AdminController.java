package ru.smc.smc.api.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.smc.smc.api.application.common.enums.MessageRoleType;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.processor.MessagePrimarilyProcessor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AdminController {

    private final MessagePrimarilyProcessor messagePrimarilyProcessor;
    private static final MessageRoleType MESSAGE_TYPE = MessageRoleType.ADMIN;

    @PostMapping("/admin")
    public UserResponseItem processMessage(@RequestBody MessageRequestBody request, @RequestHeader("api-key") String apiKey){
        return messagePrimarilyProcessor.processMessage(request, MESSAGE_TYPE, apiKey);
    }
}
