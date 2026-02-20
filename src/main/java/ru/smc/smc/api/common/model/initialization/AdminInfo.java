package ru.smc.smc.api.common.model.initialization;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.smc.smc.api.common.enums.AvailablePlatform;
import ru.smc.smc.api.common.enums.UserRole;

@Data
@AllArgsConstructor
public class AdminInfo {
    private AvailablePlatform platform;
    private String idOnPlatform;
    private UserRole role;
}
