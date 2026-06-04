package ru.smc.smc.api.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import ru.smc.smc.api.application.common.enums.AvailablePlatform;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class GiveawayParticipant {

    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private BotUser user;

    @Enumerated(EnumType.STRING)
    private AvailablePlatform platform;

    private String idOnPlatform;

    private String participantNumber;

    private Instant createTime = Instant.now();
}
