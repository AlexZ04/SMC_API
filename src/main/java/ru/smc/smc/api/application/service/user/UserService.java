package ru.smc.smc.api.application.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.AvailablePlatform;
import ru.smc.smc.api.application.common.enums.DistributionGroups;
import ru.smc.smc.api.application.common.enums.UserRole;
import ru.smc.smc.api.application.common.model.response.PlatformReceiver;
import ru.smc.smc.api.application.service.factory.BotUserFactory;
import ru.smc.smc.api.application.service.response.ResponseUIService;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.domain.repository.BotUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BotUserRepository botUserRepository;
    private final BotUserFactory botUserFactory;
    private final ResponseUIService responseUIService;

    public BotUser findOrCreateBotUser(AvailablePlatform availablePlatform, String idOnPlatform) {
        Optional<BotUser> botUser = botUserRepository.findBotUserByPlatformAndIdOnPlatform(availablePlatform, idOnPlatform);

        return botUser.orElseGet(() -> botUserFactory.createNewUser(availablePlatform, idOnPlatform));
    }

    public List<PlatformReceiver> findBotUsersByGroup(DistributionGroups distributionGroup) {
        List<BotUser> receivers = new ArrayList<>();

        if (distributionGroup == DistributionGroups.ADMINS) {
            receivers = botUserRepository.findByRoleNot(UserRole.USER);
        } else if (distributionGroup == DistributionGroups.ALL_USERS) {
            receivers = botUserRepository.findAll();
        } else if (distributionGroup == DistributionGroups.EVENTS_SUBSCRIBERS) {
            receivers = botUserRepository.findBySubscriptionSubscribedToEventDistributionTrue();
        } else if (distributionGroup == DistributionGroups.COMPETITION_SUBSCRIBERS) {
            receivers = botUserRepository.findBySubscriptionSubscribedToCompetitionDistributionTrue();
        } else if (distributionGroup == DistributionGroups.SCHEDULE_SUBSCRIBERS) {
            receivers = botUserRepository.findBySubscriptionSubscribedToScheduleDistributionTrue();
        }

        return receivers.stream()
                .map(this::mapUserToPlatformReceiver)
                .toList();
    }

    private PlatformReceiver mapUserToPlatformReceiver(BotUser botUser) {
        return new PlatformReceiver()
                .setPlatform(botUser.getPlatform())
                .setReceiverId(botUser.getIdOnPlatform())
                .setReplyElements(responseUIService.makeKeyboard(botUser.getCurrentState(), botUser.getRole()));
    }
}
