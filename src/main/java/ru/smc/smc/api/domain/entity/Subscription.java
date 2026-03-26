package ru.smc.smc.api.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    private BotUser user;

    private boolean subscribedToEventDistribution = false;
    private boolean subscribedToCompetitionDistribution = false;
    private boolean subscribedToScheduleDistribution = false;
}
