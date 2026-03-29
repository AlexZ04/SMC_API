package ru.smc.smc.api.application.service.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.model.response.DistributionResponse;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.domain.entity.BotStats;
import ru.smc.smc.api.domain.repository.BotStatsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final BotStatsRepository botStatsRepository;

    public void updateBotStats(UserResponseItem response) {
        List<BotStats> botStatsList = botStatsRepository.findAll();

        BotStats botStats;
        if (botStatsList.isEmpty()) {
            botStats = new BotStats();
        } else {
            botStats = botStatsRepository.findAll().getFirst();
        }

        long sendMessages = botStats.getMessageBotSent() + response.getResponseToUser().getPreviewMessages().size() + 1;

        if (response.getResponseToUser() instanceof DistributionResponse) {
            sendMessages += ((DistributionResponse) response.getResponseToUser()).getReceivers().size();
        }

        botStats.setMessageBotSent(sendMessages);
        botStatsRepository.save(botStats);
    }
}
