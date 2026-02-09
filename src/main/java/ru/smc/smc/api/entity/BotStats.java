package ru.smc.smc.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class BotStats {
    @Id
    private UUID id = UUID.randomUUID();
    private Long messageBotSent = 0L;
    private Long interactiveUses = 0L;
}
