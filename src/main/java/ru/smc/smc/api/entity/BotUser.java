package ru.smc.smc.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.smc.smc.api.domain.enums.AvailablePlatform;
import ru.smc.smc.api.domain.enums.UserRole;
import ru.smc.smc.api.domain.enums.UserState;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BotUser {
    @Id
    private UUID innerId = UUID.randomUUID();
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailablePlatform platform;
    @NotNull
    private String idOnPlatform;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserState currentState = UserState.MAIN_MENU;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;
    private Long messageSent = 0L;
    private Instant createTime =  Instant.now();
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Subscription subscription;
}
