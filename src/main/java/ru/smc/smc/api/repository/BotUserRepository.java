package ru.smc.smc.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.smc.smc.api.common.enums.AvailablePlatform;
import ru.smc.smc.api.entity.BotUser;

import java.util.Optional;
import java.util.UUID;

public interface BotUserRepository extends JpaRepository<BotUser, UUID> {
    Optional<BotUser> findBotUserByPlatformAndIdOnPlatform(AvailablePlatform platform, String idOnPlatform);
}
