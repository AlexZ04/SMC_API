package ru.smc.smc.api.application.processor.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.constant.BotCommands;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.service.giveaway.GiveawayService;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.domain.entity.BotUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiveawayMessageAdminProcessor implements MessageAdminProcessor {

    private static final String PARTICIPANTS_CLEARED_MESSAGE = "Список участников розыгрыша обнулён.";
    private static final String INPUT_WINNERS_AMOUNT_MESSAGE_FORMAT = "Введи, сколько участников розыгрыша необходимо получить. Всего участников розыгрыша: %s";
    private static final String INCORRECT_WINNERS_AMOUNT_MESSAGE_FORMAT = "Некорректное количество участников. Введи число от 1 до %s";
    private static final String WINNERS_MESSAGE_FORMAT = "Выбранные участники розыгрыша:\n%s";

    private final ResponseService responseService;
    private final GiveawayService giveawayService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        if (user.getCurrentState() == UserState.GET_GIVEAWAY_WINNERS) {
            return processWinnersAmount(request, user);
        }

        if (request.getMessage().equalsIgnoreCase(BotCommands.GET_GIVEAWAY_PARTICIPANTS_COMMAND)) {
            return responseService.createUserResponse(user, UserState.MAIN_MENU, giveawayService.getParticipantsInfo());
        }

        if (request.getMessage().equalsIgnoreCase(BotCommands.CLEAR_GIVEAWAY_PARTICIPANTS_COMMAND)) {
            log.info("Администратор ({}, {}) запросил очистку списка участников розыгрыша",
                    user.getPlatform(), user.getIdOnPlatform());
            giveawayService.clearParticipants();

            return responseService.createUserResponse(user, UserState.MAIN_MENU, PARTICIPANTS_CLEARED_MESSAGE);
        }

        return responseService.createUserResponse(user, UserState.GET_GIVEAWAY_WINNERS,
                String.format(INPUT_WINNERS_AMOUNT_MESSAGE_FORMAT, giveawayService.countParticipants()));
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.GIVEAWAY;
    }

    private UserResponseItem processWinnersAmount(MessageRequestBody request, BotUser user) {
        int participantsAmount = parseParticipantsAmount(request.getMessage());
        int totalParticipants = giveawayService.countParticipants();

        return giveawayService.getRandomParticipantsInfo(participantsAmount)
                .map(participantsInfo -> {
                    log.info("Администратор ({}, {}) получил {} победителей розыгрыша",
                            user.getPlatform(), user.getIdOnPlatform(), participantsAmount);
                    return responseService.createUserResponse(user, UserState.MAIN_MENU,
                            String.format(WINNERS_MESSAGE_FORMAT, participantsInfo));
                })
                .orElseGet(() -> {
                    log.warn("Администратор ({}, {}) ввёл некорректное количество победителей розыгрыша: {}. Всего участников: {}",
                            user.getPlatform(), user.getIdOnPlatform(), request.getMessage(), totalParticipants);
                    return responseService.createUserResponse(user, UserState.GET_GIVEAWAY_WINNERS,
                            String.format(INCORRECT_WINNERS_AMOUNT_MESSAGE_FORMAT, totalParticipants));
                });
    }

    private int parseParticipantsAmount(String message) {
        try {
            return Integer.parseInt(message.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
