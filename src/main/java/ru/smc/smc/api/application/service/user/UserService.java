package ru.smc.smc.api.application.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.AvailablePlatform;
import ru.smc.smc.api.application.common.enums.DistributionGroups;
import ru.smc.smc.api.application.common.enums.UserRole;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.response.PlatformReceiver;
import ru.smc.smc.api.application.service.factory.BotUserFactory;
import ru.smc.smc.api.application.service.response.ResponseUIService;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.domain.repository.BotUserRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

        return mapUsersToPlatformReceivers(receivers);
    }

    public List<PlatformReceiver> findCompetitionSubscribersByFacultyIds(List<Integer> facultyIds) {
        return mapUsersToPlatformReceivers(botUserRepository.findBySubscriptionSubscribedToCompetitionDistributionTrueAndFacultyIdIn(facultyIds));
    }

    public String getAdminsInfo() {
        StringBuilder adminsInfo = new StringBuilder("Администраторы системы:");

        botUserRepository.findByRoleNot(UserRole.USER).forEach(admin -> adminsInfo.append("\n")
                .append(admin.getIdOnPlatform())
                .append(" - ")
                .append(admin.getPlatform())
                .append(" - ")
                .append(admin.getRole()));

        return adminsInfo.toString();
    }

    public String addOrUpdateAdminRole(AvailablePlatform platform, String idOnPlatform, UserRole targetRole) {
        Optional<BotUser> existingUserOptional = botUserRepository.findBotUserByPlatformAndIdOnPlatform(platform, idOnPlatform);

        if (existingUserOptional.isEmpty()) {
            BotUser newUser = botUserFactory.createNewUser(platform, idOnPlatform);
            newUser.setRole(targetRole);
            botUserRepository.save(newUser);

            return "Пользователь " + idOnPlatform + " на платформе " + platform + " создан с ролью " + targetRole;
        }

        BotUser existingUser = existingUserOptional.get();

        if (existingUser.getRole() == targetRole) {
            return "Пользователь " + idOnPlatform + " на платформе " + platform + " уже имеет роль " + targetRole;
        }

        if (getRolePriority(existingUser.getRole()) > getRolePriority(targetRole)) {
            return "Пользователь " + idOnPlatform + " на платформе " + platform +
                    " уже имеет роль выше выдаваемой: " + existingUser.getRole();
        }

        existingUser.setRole(targetRole);
        botUserRepository.save(existingUser);

        return "Пользователю " + idOnPlatform + " на платформе " + platform + " выдана роль " + targetRole;
    }

    public String removeAdminRole(AvailablePlatform platform, String idOnPlatform) {
        Optional<BotUser> existingUserOptional = botUserRepository.findBotUserByPlatformAndIdOnPlatform(platform, idOnPlatform);

        if (existingUserOptional.isEmpty()) {
            return "Пользователь " + idOnPlatform + " на платформе " + platform + " не найден";
        }

        BotUser existingUser = existingUserOptional.get();

        if (existingUser.getRole() == UserRole.USER) {
            return "Пользователь " + idOnPlatform + " на платформе " + platform + " не является администратором";
        }

        if (existingUser.getRole() == UserRole.SUPER_ADMIN) {
            return "Нельзя удалить роль суперадминистратора через эту команду";
        }

        existingUser.setRole(UserRole.USER);
        botUserRepository.save(existingUser);

        return "Пользователь " + idOnPlatform + " на платформе " + platform + " понижен до роли " + UserRole.USER;
    }

    private List<PlatformReceiver> mapUsersToPlatformReceivers(List<BotUser> botUsers) {
        Map<ReceiverGroupKey, List<String>> receiversIdsByGroup = new LinkedHashMap<>();

        botUsers.forEach(botUser -> {
            ReceiverGroupKey groupKey = new ReceiverGroupKey(botUser.getPlatform(), normalizeDistributionRole(botUser.getRole()));
            receiversIdsByGroup.computeIfAbsent(groupKey, key -> new ArrayList<>()).add(botUser.getIdOnPlatform());
        });

        return receiversIdsByGroup.entrySet().stream()
                .map(entry -> mapReceiverGroupToPlatformReceiver(entry.getKey(), entry.getValue()))
                .toList();
    }

    private PlatformReceiver mapReceiverGroupToPlatformReceiver(ReceiverGroupKey receiverGroupKey, List<String> receiversIds) {
        return new PlatformReceiver()
                .setPlatform(receiverGroupKey.platform())
                .setRole(receiverGroupKey.role())
                .setReceiversId(receiversIds)
                .setReplyElements(responseUIService.makeKeyboard(UserState.MAIN_MENU, receiverGroupKey.role()));
    }

    private UserRole normalizeDistributionRole(UserRole role) {
        if (role == UserRole.SUPER_ADMIN) {
            return UserRole.ADMIN;
        }

        return role;
    }

    private int getRolePriority(UserRole role) {
        return switch (role) {
            case USER -> 0;
            case ADMIN -> 1;
            case SUPER_ADMIN -> 2;
        };
    }

    private record ReceiverGroupKey(AvailablePlatform platform, UserRole role) {
    }
}
