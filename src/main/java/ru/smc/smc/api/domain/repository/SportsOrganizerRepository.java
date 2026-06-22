package ru.smc.smc.api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.domain.entity.SportsOrganizer;

public interface SportsOrganizerRepository extends JpaRepository<SportsOrganizer, Long> {
    boolean existsByBotUser(BotUser botUser);
}
