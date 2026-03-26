package ru.smc.smc.api.application.common.model.initialization;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.smc.smc.api.application.common.enums.AvailablePlatform;
import ru.smc.smc.api.application.common.enums.UserRole;

@Data
@AllArgsConstructor
public class AdminInfo {
    private AvailablePlatform platform;
    private String idOnPlatform;
    private UserRole role;
}
