package ru.smc.smc.api.service;

import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.enums.AvailablePlatform;
import ru.smc.smc.api.entity.BotUser;
import ru.smc.smc.api.repository.BotUserRepository;
import ru.smc.smc.api.service.factory.BotUserFactory;

import java.util.Optional;

@Service
public class UserService {

    private BotUserRepository botUserRepository;
    private BotUserFactory botUserFactory;

    public BotUser findOrCreateBotUser(AvailablePlatform availablePlatform, String idOnPlatform) {
        Optional<BotUser> botUser = botUserRepository.findBotUserByPlatformAndIdOnPlatform(availablePlatform, idOnPlatform);

        return botUser.orElseGet(() -> botUserFactory.createNewUser(availablePlatform, idOnPlatform));
    }
}
