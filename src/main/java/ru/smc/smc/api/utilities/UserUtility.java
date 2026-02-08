package ru.smc.smc.api.utilities;

import lombok.experimental.UtilityClass;
import ru.smc.smc.api.domain.enums.UserRole;
import ru.smc.smc.api.entity.BotUser;

@UtilityClass
public class UserUtility {
    public boolean isUserAdmin(BotUser user) {
        return user.getRole() == UserRole.ADMIN ||  user.getRole() == UserRole.SUPER_ADMIN;
    }
}
