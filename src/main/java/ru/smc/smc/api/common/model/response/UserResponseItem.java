package ru.smc.smc.api.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.smc.smc.api.common.enums.AvailablePlatform;
import ru.smc.smc.api.common.enums.ResponseStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseItem {
    private ResponseStatus status;
    private AvailablePlatform platform;
    private String userIdOnPlatform;
    private MessageResponse responseToUser;
}
