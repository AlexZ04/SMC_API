package ru.smc.smc.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.smc.smc.api.domain.enums.AvailablePlatform;
import ru.smc.smc.api.domain.enums.MessageType;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageHistory {
    @Id
    private UUID id = UUID.randomUUID();
    private String message;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailablePlatform platform;
    private String userIdOnPlatform;
    private int attachmentsAmount = 0;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;
    private Instant messageTime = Instant.now();
}
