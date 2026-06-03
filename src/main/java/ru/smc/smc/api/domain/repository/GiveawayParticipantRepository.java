package ru.smc.smc.api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.domain.entity.GiveawayParticipant;

import java.util.Optional;
import java.util.UUID;

public interface GiveawayParticipantRepository extends JpaRepository<GiveawayParticipant, UUID> {
    Optional<GiveawayParticipant> findByUser(BotUser user);
}
