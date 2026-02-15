package ru.smc.smc.api.domain.model.initialization;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.smc.smc.api.domain.enums.AvailablePlatform;
import ru.smc.smc.api.domain.enums.UserRole;

@Data
@AllArgsConstructor
public class AdminInfo {
    private AvailablePlatform platform;
    private String idOnPlatform;
    private UserRole role;
}
