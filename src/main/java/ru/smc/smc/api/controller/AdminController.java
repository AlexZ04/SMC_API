package ru.smc.smc.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.smc.smc.api.domain.enums.MessageType;
import ru.smc.smc.api.domain.model.request.MessageRequestBody;
import ru.smc.smc.api.domain.model.response.MessageResponse;
import ru.smc.smc.api.service.MessageProcessorService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AdminController {

    private final MessageProcessorService messageProcessorService;
    private static final MessageType MESSAGE_TYPE = MessageType.ADMIN;

    @PostMapping("/admin")
    public MessageResponse processMessage(@RequestBody MessageRequestBody request, @RequestHeader String apiKey){
        return messageProcessorService.processMessage(request, MESSAGE_TYPE, apiKey);
    }
}
