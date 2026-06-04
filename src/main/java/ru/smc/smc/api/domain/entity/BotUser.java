package ru.smc.smc.api.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.smc.smc.api.application.common.enums.AvailablePlatform;
import ru.smc.smc.api.application.common.enums.UserRole;
import ru.smc.smc.api.application.common.enums.UserState;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BotUser {

    private static final String ADMIN_CHANNEL = "admin-channel";

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_faculty_id")
    private Faculty selectedFaculty;

    private String selectedDistributionType;

    private String selectedDistributionFacultyIds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserState currentState = UserState.MAIN_MENU;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserState userCurrentState = UserState.MAIN_MENU;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserState adminCurrentState = UserState.MAIN_MENU;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Column(nullable = false)
    private String userChannel = "admin-channel";

    private Long messageSent = 0L;

    private Instant createTime =  Instant.now();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Subscription subscription;

    public void activateCurrentState(String userChannel) {
        currentState = defineCurrentState(userChannel);
    }

    public void updateCurrentState(String userChannel, UserState userState) {
        currentState = userState;

        if (ADMIN_CHANNEL.equals(userChannel)) {
            adminCurrentState = userState;
            return;
        }

        userCurrentState = userState;
    }

    private UserState defineCurrentState(String userChannel) {
        if (ADMIN_CHANNEL.equals(userChannel)) {
            return adminCurrentState;
        }

        return userCurrentState;
    }
}
