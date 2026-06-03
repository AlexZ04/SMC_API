package ru.smc.smc.api.application.common.enums;

import ru.smc.smc.api.domain.entity.Subscription;

import java.util.List;
import java.util.Optional;

public enum UserDistributionType {
    EVENTS(
            "Мероприятия СМК",
            "Выполнено! Ты подписан(-а) на рассылку о мероприятиях СМК\n" +
                    "Теперь тебе будут приходить уведомления о предстоящих мероприятиях.",
            "Ты больше не подписан(-а) на рассылку о мероприятиях СМК."
    ) {
        @Override
        public void subscribe(Subscription subscription) {
            subscription.setSubscribedToEventDistribution(true);
        }

        @Override
        public void unsubscribe(Subscription subscription) {
            subscription.setSubscribedToEventDistribution(false);
        }

        @Override
        public boolean isSubscribed(Subscription subscription) {
            return subscription.isSubscribedToEventDistribution();
        }
    },
    COMPETITIONS(
            "Соревнования сборной факультета",
            "Выполнено! Ты подписан(-а) на рассылку о сборной своего структурного подразделения\n" +
                    "Теперь тебе будут приходить уведомления о предстоящих играх и результаты игр сборной вашего факультета.",
            "Ты больше не подписан(-а) на рассылку о соревнованиях сборной факультета."
    ) {
        @Override
        public void subscribe(Subscription subscription) {
            subscription.setSubscribedToCompetitionDistribution(true);
        }

        @Override
        public void unsubscribe(Subscription subscription) {
            subscription.setSubscribedToCompetitionDistribution(false);
        }

        @Override
        public boolean isSubscribed(Subscription subscription) {
            return subscription.isSubscribedToCompetitionDistribution();
        }
    },
    SCHEDULE(
            "Обновление расписания занятий",
            "Выполнено! Ты подписан(-а) на рассылку об обновлении расписания занятий\n" +
                    "Теперь тебе будут приходить уведомления об изменениях расписания занятий.",
            "Ты больше не подписан(-а) на рассылку об обновлении расписания занятий."
    ) {
        @Override
        public void subscribe(Subscription subscription) {
            subscription.setSubscribedToScheduleDistribution(true);
        }

        @Override
        public void unsubscribe(Subscription subscription) {
            subscription.setSubscribedToScheduleDistribution(false);
        }

        @Override
        public boolean isSubscribed(Subscription subscription) {
            return subscription.isSubscribedToScheduleDistribution();
        }
    };

    private final String buttonText;
    private final String subscribeMessage;
    private final String unsubscribeMessage;

    UserDistributionType(String buttonText, String subscribeMessage, String unsubscribeMessage) {
        this.buttonText = buttonText;
        this.subscribeMessage = subscribeMessage;
        this.unsubscribeMessage = unsubscribeMessage;
    }

    public static Optional<UserDistributionType> findByButtonText(String message) {
        return valuesAsList().stream()
                .filter(distributionType -> distributionType.getButtonText().equalsIgnoreCase(message.trim()))
                .findFirst();
    }

    public static List<UserDistributionType> valuesAsList() {
        return List.of(values());
    }

    public String getButtonText() {
        return buttonText;
    }

    public String getSubscribeMessage() {
        return subscribeMessage;
    }

    public String getUnsubscribeMessage() {
        return unsubscribeMessage;
    }

    public abstract void subscribe(Subscription subscription);

    public abstract void unsubscribe(Subscription subscription);

    public abstract boolean isSubscribed(Subscription subscription);
}
