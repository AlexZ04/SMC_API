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

    public String getBotStatsInfo() {
        BotStats botStats = getBotStats();

        return "Статистика системы:\n" +
                "Сообщений отправлено ботом: " + botStats.getMessageBotSent() + "\n" +
                "Интерактивных использований: " + botStats.getInteractiveUses();
    }

    public void updateBotStats(UserResponseItem response) {
        BotStats botStats = getBotStats();

        long sendMessages = botStats.getMessageBotSent() + response.getResponseToUser().getPreviewMessages().size() + 1;

        if (response.getResponseToUser() instanceof DistributionResponse) {
            DistributionResponse distributionResponse = (DistributionResponse) response.getResponseToUser();
            if (distributionResponse.getDistribution() != null) {
                sendMessages += distributionResponse.getDistribution().getReceivers().size();
            }
        }

        botStats.setMessageBotSent(sendMessages);
        botStatsRepository.save(botStats);
    }

    private BotStats getBotStats() {
        List<BotStats> botStatsList = botStatsRepository.findAll();

        if (botStatsList.isEmpty()) {
            return new BotStats();
        }

        return botStatsList.getFirst();
    }
}
