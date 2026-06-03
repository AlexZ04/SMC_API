package ru.smc.smc.api.application.service.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.smc.smc.api.application.common.enums.AvailablePlatform;
import ru.smc.smc.api.application.common.enums.UserDistributionType;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.domain.repository.BotUserRepository;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserStatsService {

    private static final String USER_NOT_FOUND_MESSAGE = "Пользователь на платформе %s с идентификатором %s не найден";
    private static final String USER_INFO_HEADER = "Информация о пользователе %s на платформе %s:";
    private static final String USER_INFO_FORMAT = """

            Платформа: %s
            Идентификатор: %s
            Сообщений отправлено пользователем: %s
            Подписки: %s
            Факультет: %s
            Первое использование бота: %s
            Канал пользования: %s""";
    private static final String NO_SUBSCRIPTIONS = "нет";
    private static final String WITHOUT_FACULTY = "Не установлен";
    private static final String WITHOUT_CHANNEL = "не указан";
    private static final DateTimeFormatter USER_INFO_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private final BotUserRepository botUserRepository;

    @Transactional(readOnly = true)
    public String getUserInfo(AvailablePlatform platform, String idOnPlatform) {
        Optional<BotUser> requestedUserOptional = botUserRepository.findBotUserByPlatformAndIdOnPlatform(platform, idOnPlatform);

        if (requestedUserOptional.isEmpty()) {
            return String.format(USER_NOT_FOUND_MESSAGE, platform, idOnPlatform);
        }

        List<BotUser> users = botUserRepository.findByIdOnPlatform(idOnPlatform).stream()
                .sorted(Comparator.comparing(BotUser::getPlatform))
                .toList();

        StringBuilder userInfo = new StringBuilder(String.format(USER_INFO_HEADER, idOnPlatform, platform));
        users.forEach(user -> userInfo.append(formUserInfo(user)));

        return userInfo.toString();
    }

    private String formUserInfo(BotUser user) {
        return String.format(USER_INFO_FORMAT,
                user.getPlatform(),
                user.getIdOnPlatform(),
                user.getMessageSent(),
                formSubscriptionsInfo(user),
                defineFacultyName(user),
                formatCreateTime(user),
                defineUserChannel(user));
    }

    private String formSubscriptionsInfo(BotUser user) {
        if (user.getSubscription() == null) {
            return NO_SUBSCRIPTIONS;
        }

        List<String> subscriptions = UserDistributionType.valuesAsList().stream()
                .filter(distributionType -> distributionType.isSubscribed(user.getSubscription()))
                .map(UserDistributionType::getButtonText)
                .toList();

        if (subscriptions.isEmpty()) {
            return NO_SUBSCRIPTIONS;
        }

        return subscriptions.stream()
                .collect(Collectors.joining(", "));
    }

    private String defineFacultyName(BotUser user) {
        if (user.getFaculty() == null) {
            return WITHOUT_FACULTY;
        }

        return user.getFaculty().getNameRu();
    }

    private String formatCreateTime(BotUser user) {
        return USER_INFO_DATE_FORMATTER.format(user.getCreateTime().atZone(ZoneId.systemDefault()));
    }

    private String defineUserChannel(BotUser user) {
        return user.getUserChannel() == null ? WITHOUT_CHANNEL : user.getUserChannel();
    }
}
