package ru.smc.smc.api.application.service.distribution;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.AdminDistributionType;
import ru.smc.smc.api.application.common.enums.DistributionGroups;
import ru.smc.smc.api.application.common.model.response.PlatformReceiver;
import ru.smc.smc.api.application.service.user.UserService;
import ru.smc.smc.api.application.utility.FileUtility;
import ru.smc.smc.api.domain.entity.BotUser;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GeneralDistributionSendService implements DistributionSendService {

    private final UserService userService;
    private final DistributionFileService distributionFileService;

    @Override
    public AdminDistributionType type() {
        return AdminDistributionType.GENERAL;
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
        return userService.findBotUsersByGroup(DistributionGroups.ALL_USERS);
    }
}
