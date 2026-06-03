package ru.smc.smc.api.application.service.processors.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.Colors;
import ru.smc.smc.api.application.common.enums.ElementType;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserDistributionType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.ElementModel;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.domain.entity.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SetDistributionMessageUserProcessor implements MessageUserProcessor {

    private static final String MENU_MESSAGE = "Ты подписан(-а) на следующие рассылки:";
    private static final String EMPTY_SUBSCRIPTIONS_MESSAGE = "Пока ты не подписан(-а) ни на одну рассылку";
    private static final String UNSUBSCRIBE_BUTTON = "Отписаться от уведомлений";
    private static final String BACK_BUTTON = "Назад";
    private static final String UNSUBSCRIBE_WARNING = "При отписке от рассылки ты сможешь снова подписаться на них позже";
    private static final String UNSUBSCRIBE_CHOICE_MESSAGE = "Выберите тип рассылки, от которой хочешь отписаться:";
    private static final String NO_SUBSCRIPTIONS_MESSAGE = "Сейчас нет рассылок, от которых можно отписаться";
    private static final String INCORRECT_MESSAGE = "Выберите действие из списка";

    private final ResponseService responseService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        Subscription subscription = getOrCreateSubscription(user);

        if (user.getCurrentState() == UserState.UNSUBSCRIBE_DISTRIBUTION) {
            return processUnsubscribeChoice(request, user, subscription);
        }

        Optional<UserDistributionType> distributionType = UserDistributionType.findByButtonText(request.getMessage());

        if (distributionType.isPresent()) {
            return processSubscribeChoice(user, subscription, distributionType.get());
        }

        if (request.getMessage().equalsIgnoreCase(UNSUBSCRIBE_BUTTON)) {
            return processUnsubscribeMenu(user, subscription);
        }

        if (user.getCurrentState() == UserState.SET_DISTRIBUTION) {
            return responseService.createUserResponseWithInlineKeyboard(user, UserState.SET_DISTRIBUTION,
                    INCORRECT_MESSAGE, createDistributionMenuKeyboard());
        }

        return createDistributionMenuResponse(user, subscription);
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.SET_DISTRIBUTION;
    }

    private UserResponseItem processSubscribeChoice(BotUser user, Subscription subscription, UserDistributionType distributionType) {
        distributionType.subscribe(subscription);

        return responseService.createUserResponseWithInlineKeyboard(user, UserState.SET_DISTRIBUTION,
                distributionType.getSubscribeMessage(), createDistributionMenuKeyboard());
    }

    private UserResponseItem processUnsubscribeMenu(BotUser user, Subscription subscription) {
        List<UserDistributionType> subscribedDistributionTypes = findSubscribedDistributionTypes(subscription);

        if (subscribedDistributionTypes.isEmpty()) {
            return responseService.createUserResponseWithInlineKeyboard(user, UserState.SET_DISTRIBUTION,
                    NO_SUBSCRIPTIONS_MESSAGE, createDistributionMenuKeyboard());
        }

        return responseService.createUserResponseWithPreviewMessagesAndInlineKeyboard(user, UserState.UNSUBSCRIBE_DISTRIBUTION,
                UNSUBSCRIBE_WARNING, List.of(UNSUBSCRIBE_CHOICE_MESSAGE),
                createUnsubscribeKeyboard(subscribedDistributionTypes));
    }

    private UserResponseItem processUnsubscribeChoice(MessageRequestBody request, BotUser user, Subscription subscription) {
        return UserDistributionType.findByButtonText(request.getMessage())
                .filter(distributionType -> distributionType.isSubscribed(subscription))
                .map(distributionType -> processCorrectUnsubscribeChoice(user, subscription, distributionType))
                .orElseGet(() -> responseService.createUserResponseWithPreviewMessagesAndInlineKeyboard(user,
                        UserState.UNSUBSCRIBE_DISTRIBUTION, INCORRECT_MESSAGE,
                        List.of(UNSUBSCRIBE_CHOICE_MESSAGE),
                        createUnsubscribeKeyboard(findSubscribedDistributionTypes(subscription))));
    }

    private UserResponseItem processCorrectUnsubscribeChoice(BotUser user, Subscription subscription,
                                                             UserDistributionType distributionType) {
        distributionType.unsubscribe(subscription);

        return responseService.createUserResponseWithInlineKeyboard(user, UserState.SET_DISTRIBUTION,
                distributionType.getUnsubscribeMessage() + "\n\n" + formSubscriptionsInfo(subscription),
                createDistributionMenuKeyboard());
    }

    private UserResponseItem createDistributionMenuResponse(BotUser user, Subscription subscription) {
        return responseService.createUserResponseWithInlineKeyboard(user, UserState.SET_DISTRIBUTION,
                formSubscriptionsInfo(subscription), createDistributionMenuKeyboard());
    }

    private String formSubscriptionsInfo(Subscription subscription) {
        List<UserDistributionType> subscribedDistributionTypes = findSubscribedDistributionTypes(subscription);

        if (subscribedDistributionTypes.isEmpty()) {
            return MENU_MESSAGE + "\n" + EMPTY_SUBSCRIPTIONS_MESSAGE;
        }

        StringBuilder message = new StringBuilder(MENU_MESSAGE);
        subscribedDistributionTypes.forEach(distributionType -> message.append("\n")
                .append("🔺")
                .append(distributionType.getButtonText()));

        return message.toString();
    }

    private List<UserDistributionType> findSubscribedDistributionTypes(Subscription subscription) {
        return UserDistributionType.valuesAsList().stream()
                .filter(distributionType -> distributionType.isSubscribed(subscription))
                .toList();
    }

    private List<List<ElementModel>> createDistributionMenuKeyboard() {
        return List.of(
                List.of(createWhiteButton(UserDistributionType.EVENTS.getButtonText())),
                List.of(createWhiteButton(UserDistributionType.COMPETITIONS.getButtonText())),
                List.of(createWhiteButton(UserDistributionType.SCHEDULE.getButtonText())),
                List.of(createBlackButton(UNSUBSCRIBE_BUTTON)),
                List.of(createBlackButton(BACK_BUTTON))
        );
    }

    private List<List<ElementModel>> createUnsubscribeKeyboard(List<UserDistributionType> distributionTypes) {
        List<List<ElementModel>> keyboard = new ArrayList<>();

        distributionTypes.forEach(distributionType -> keyboard.add(List.of(createBlackButton(distributionType.getButtonText()))));
        keyboard.add(List.of(createBlackButton(BACK_BUTTON)));

        return keyboard;
    }

    private ElementModel createWhiteButton(String message) {
        return new ElementModel(ElementType.BUTTON, null, message, Colors.WHITE.getColor(), Colors.BLACK.getColor());
    }

    private ElementModel createBlackButton(String message) {
        return new ElementModel(ElementType.BUTTON, null, message, Colors.BLACK.getColor(), Colors.WHITE.getColor());
    }

    private Subscription getOrCreateSubscription(BotUser user) {
        if (user.getSubscription() != null) {
            return user.getSubscription();
        }

        Subscription subscription = new Subscription();
        subscription.setId(user.getInnerId());
        subscription.setUser(user);
        user.setSubscription(subscription);

        return subscription;
    }

}
