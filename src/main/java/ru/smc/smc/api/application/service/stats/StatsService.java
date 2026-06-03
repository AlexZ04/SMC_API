package ru.smc.smc.api.application.service.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.smc.smc.api.application.common.model.response.DistributionResponse;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.domain.entity.BotStats;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.domain.entity.Subscription;
import ru.smc.smc.api.domain.repository.BotStatsRepository;
import ru.smc.smc.api.domain.repository.BotUserRepository;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private static final String WITHOUT_FACULTY = "Не установлен";
    private static final String WITHOUT_CHANNEL = "не указан";

    private final BotStatsRepository botStatsRepository;
    private final BotUserRepository botUserRepository;

    @Transactional(readOnly = true)
    public String getBotStatsInfo() {
        BotStats botStats = getBotStats();
        List<BotUser> users = botUserRepository.findAll();

        return "Статистика системы:\n" +
                "Сообщений отправлено ботом: " + botStats.getMessageBotSent() + "\n" +
                "Сообщений отправлено боту: " + botStats.getMessageUserSent() + "\n\n" +
                "Подписки на рассылки:\n" +
                formSubscriptionsStats(users) + "\n\n" +
                "Пользователи по факультетам:\n" +
                formFacultyStats(users) + "\n\n" +
                "Пользователи по платформам и каналам:\n" +
                formPlatformAndChannelStats(users);
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

    public void updateIncomingMessageStats() {
        BotStats botStats = getBotStats();

        botStats.setMessageUserSent(botStats.getMessageUserSent() + 1);
        botStatsRepository.save(botStats);
    }

    private BotStats getBotStats() {
        List<BotStats> botStatsList = botStatsRepository.findAll();

        if (botStatsList.isEmpty()) {
            return botStatsRepository.save(new BotStats());
        }

        return botStatsList.getFirst();
    }

    private String formSubscriptionsStats(List<BotUser> users) {
        long eventsSubscribers = users.stream()
                .filter(user -> isSubscribedToEvents(user.getSubscription()))
                .count();
        long competitionsSubscribers = users.stream()
                .filter(user -> isSubscribedToCompetitions(user.getSubscription()))
                .count();
        long scheduleSubscribers = users.stream()
                .filter(user -> isSubscribedToSchedule(user.getSubscription()))
                .count();

        return "Мероприятия СМК: " + eventsSubscribers + "\n" +
                "Соревнования сборной факультета: " + competitionsSubscribers + "\n" +
                "Обновление расписания занятий: " + scheduleSubscribers;
    }

    private String formFacultyStats(List<BotUser> users) {
        Map<String, Long> usersByFaculty = users.stream()
                .collect(Collectors.groupingBy(this::defineFacultyName, TreeMap::new, Collectors.counting()));

        return formMapStats(usersByFaculty);
    }

    private String formPlatformAndChannelStats(List<BotUser> users) {
        Map<String, Long> usersByPlatformAndChannel = users.stream()
                .collect(Collectors.groupingBy(this::definePlatformAndChannel, TreeMap::new, Collectors.counting()));

        return formMapStats(usersByPlatformAndChannel);
    }

    private String formMapStats(Map<String, Long> stats) {
        if (stats.isEmpty()) {
            return "Нет данных";
        }

        return stats.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

    private boolean isSubscribedToEvents(Subscription subscription) {
        return subscription != null && subscription.isSubscribedToEventDistribution();
    }

    private boolean isSubscribedToCompetitions(Subscription subscription) {
        return subscription != null && subscription.isSubscribedToCompetitionDistribution();
    }

    private boolean isSubscribedToSchedule(Subscription subscription) {
        return subscription != null && subscription.isSubscribedToScheduleDistribution();
    }

    private String defineFacultyName(BotUser user) {
        if (user.getFaculty() == null) {
            return WITHOUT_FACULTY;
        }

        return user.getFaculty().getNameRu();
    }

    private String definePlatformAndChannel(BotUser user) {
        String channel = user.getUserChannel() == null ? WITHOUT_CHANNEL : user.getUserChannel();

        return user.getPlatform() + " / " + channel;
    }
}
