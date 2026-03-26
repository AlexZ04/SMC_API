package ru.smc.smc.api.application.utilities;

import lombok.experimental.UtilityClass;
import ru.smc.smc.api.application.common.enums.UserRole;
import ru.smc.smc.api.domain.entity.BotUser;

@UtilityClass
public class UserUtility {
    public boolean isUserAdmin(BotUser user) {
        return user.getRole() == UserRole.ADMIN ||  user.getRole() == UserRole.SUPER_ADMIN;
    }
}
