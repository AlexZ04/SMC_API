package ru.smc.smc.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.smc.smc.api.common.enums.MessageRoleType;
import ru.smc.smc.api.common.model.request.MessageRequestBody;
import ru.smc.smc.api.common.model.response.UserResponseItem;
import ru.smc.smc.api.service.MessageProcessorService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AdminController {

    private final MessageProcessorService messageProcessorService;
    private static final MessageRoleType MESSAGE_TYPE = MessageRoleType.ADMIN;

    @PostMapping("/admin")
    public UserResponseItem processMessage(@RequestBody MessageRequestBody request, @RequestHeader("api-key") String apiKey){
        return messageProcessorService.processMessage(request, MESSAGE_TYPE, apiKey);
    }
}
