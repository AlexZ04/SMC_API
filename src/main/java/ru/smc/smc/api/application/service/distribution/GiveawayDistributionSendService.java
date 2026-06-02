package ru.smc.smc.api.application.service.distribution;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.AdminDistributionType;
import ru.smc.smc.api.application.common.model.response.PlatformReceiver;
import ru.smc.smc.api.application.utilities.FileUtility;
import ru.smc.smc.api.domain.entity.BotUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GiveawayDistributionSendService implements DistributionSendService {

    private final DistributionFileService distributionFileService;

    @Override
    public AdminDistributionType type() {
        return AdminDistributionType.GIVEAWAY;
    }

    @Override
    public String getDistributionText() {
        return FileUtility.getCustomizableFileMessage(type().getPathFile());
    }

    @Override
    public List<UUID> getDistributionFiles() {
        return distributionFileService.findFileIds(type());
    }

    @Override
    public List<PlatformReceiver> getReceivers(BotUser user) {
        return new ArrayList<>();
    }
}
