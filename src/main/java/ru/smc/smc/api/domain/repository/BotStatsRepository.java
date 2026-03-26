package ru.smc.smc.api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.smc.smc.api.domain.entity.BotStats;

import java.util.UUID;

public interface BotStatsRepository extends JpaRepository<BotStats, UUID> {
}
