package ru.smc.smc.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public MessageResponse processMessage(@RequestBody MessageRequestBody request){
        return messageProcessorService.processMessage(request, MESSAGE_TYPE);
    }
}
