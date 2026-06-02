package ru.smc.smc.api.application.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.smc.smc.api.application.common.enums.AvailablePlatform;
import ru.smc.smc.api.application.common.enums.ResponseStatus;
import ru.smc.smc.api.application.common.enums.UserRole;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseItem {
    private ResponseStatus status;
    private AvailablePlatform platform;
    private String userIdOnPlatform;
    private UserRole role;
    private MessageResponse responseToUser;
}
