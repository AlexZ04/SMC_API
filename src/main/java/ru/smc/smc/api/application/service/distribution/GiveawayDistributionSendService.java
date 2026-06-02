package ru.smc.smc.api.application.service.distribution;

import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.AdminDistributionType;
import ru.smc.smc.api.application.common.model.response.PlatformReceiver;
import ru.smc.smc.api.application.utilities.FileUtility;
import ru.smc.smc.api.domain.entity.BotUser;

import java.util.ArrayList;
import java.util.List;

@Service
public class GiveawayDistributionSendService implements DistributionSendService {

    @Override
    public AdminDistributionType type() {
        return AdminDistributionType.GIVEAWAY;
    }

    @Override
    public String getDistributionText() {
        return FileUtility.getCustomizableFileMessage(type().getPathFile());
    }

    @Override
    public List<PlatformReceiver> getReceivers(BotUser user) {
        return new ArrayList<>();
    }
}
