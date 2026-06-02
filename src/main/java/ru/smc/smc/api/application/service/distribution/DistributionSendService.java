package ru.smc.smc.api.application.service.distribution;

import ru.smc.smc.api.application.common.enums.AdminDistributionType;
import ru.smc.smc.api.application.common.model.response.PlatformReceiver;
import ru.smc.smc.api.domain.entity.BotUser;

import java.util.List;

public interface DistributionSendService {
    AdminDistributionType type();
    String getDistributionText();
    List<PlatformReceiver> getReceivers(BotUser user);
}
