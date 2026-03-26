package ru.smc.smc.api.application.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.AvailablePlatform;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.domain.repository.BotUserRepository;
import ru.smc.smc.api.application.service.factory.BotUserFactory;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BotUserRepository botUserRepository;
    private final BotUserFactory botUserFactory;

    public BotUser findOrCreateBotUser(AvailablePlatform availablePlatform, String idOnPlatform) {
        Optional<BotUser> botUser = botUserRepository.findBotUserByPlatformAndIdOnPlatform(availablePlatform, idOnPlatform);

        return botUser.orElseGet(() -> botUserFactory.createNewUser(availablePlatform, idOnPlatform));
    }
}
