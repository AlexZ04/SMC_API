package ru.smc.smc.api.domain.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.smc.smc.api.domain.enums.AvailablePlatform;
import ru.smc.smc.api.domain.enums.ResponseStatus;

@Data
@AllArgsConstructor
public class UserResponseItem {
    private ResponseStatus status;
    private AvailablePlatform platform;
    private String userIdOnPlatform;
    private MessageResponse responseToUser;
}
