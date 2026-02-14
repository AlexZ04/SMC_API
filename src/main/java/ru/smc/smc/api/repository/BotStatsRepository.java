package ru.smc.smc.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.smc.smc.api.entity.BotStats;

import java.util.UUID;

public interface BotStatsRepository extends JpaRepository<BotStats, UUID> {
}
