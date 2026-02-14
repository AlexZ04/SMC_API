package ru.smc.smc.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.enums.UserState;
import ru.smc.smc.api.domain.model.response.UserResponseItem;
import ru.smc.smc.api.entity.BotStats;
import ru.smc.smc.api.entity.BotUser;
import ru.smc.smc.api.repository.BotStatsRepository;
import ru.smc.smc.api.repository.BotUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final BotUserRepository botUserRepository;
    private final BotStatsRepository botStatsRepository;

    public void updateUserStats(BotUser user, UserState userState) {
        user.setCurrentState(userState);
        botUserRepository.save(user);
    }

    public void updateBotStats(UserResponseItem response) {
        List<BotStats> botStatsList = botStatsRepository.findAll();
        BotStats botStats;
        if (botStatsList.isEmpty()) {
            botStats = new BotStats();
        } else {
            botStats = botStatsRepository.findAll().getFirst();
        }
        Long sendMessages = botStats.getMessageBotSent() + response.getResponseToUser().getPreviewMessages().size() + 1;
        botStats.setMessageBotSent(sendMessages);
        botStatsRepository.save(botStats);
    }
}
