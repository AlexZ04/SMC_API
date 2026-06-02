package ru.smc.smc.api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.smc.smc.api.application.common.enums.AvailablePlatform;
import ru.smc.smc.api.application.common.enums.UserRole;
import ru.smc.smc.api.domain.entity.BotUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BotUserRepository extends JpaRepository<BotUser, UUID> {
    Optional<BotUser> findBotUserByPlatformAndIdOnPlatform(AvailablePlatform platform, String idOnPlatform);
    List<BotUser> findByRoleNot(UserRole role); // все администраторы
    List<BotUser> findBySubscriptionSubscribedToEventDistributionTrue();
    List<BotUser> findBySubscriptionSubscribedToCompetitionDistributionTrue();
    List<BotUser> findBySubscriptionSubscribedToCompetitionDistributionTrueAndFacultyIdIn(List<Integer> facultyIds);
    List<BotUser> findBySubscriptionSubscribedToScheduleDistributionTrue();
}
