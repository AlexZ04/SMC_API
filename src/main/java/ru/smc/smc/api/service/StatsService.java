package ru.smc.smc.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.enums.UserState;
import ru.smc.smc.api.domain.model.response.UserResponseItem;
import ru.smc.smc.api.entity.BotUser;
import ru.smc.smc.api.repository.BotUserRepository;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final BotUserRepository botUserRepository;

    public void updateUserStats(BotUser user, UserState userState) {
        user.setCurrentState(userState);
        botUserRepository.save(user);
    }

    public void updateBotStats(UserResponseItem response) {

    }
}
