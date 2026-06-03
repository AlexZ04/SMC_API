package ru.smc.smc.api.application.service.processors.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.constant.FeatureToggles;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.service.featuretoggle.FeatureToggleService;
import ru.smc.smc.api.application.service.giveaway.GiveawayService;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.domain.entity.GiveawayParticipant;

@Service
@RequiredArgsConstructor
public class GiveawayMessageUserProcessor implements MessageUserProcessor {

    private static final String GIVEAWAY_DISABLED_MESSAGE = "Розыгрыш сейчас недоступен";
    private static final String PARTICIPANT_REGISTERED_MESSAGE_FORMAT = "Ты участвуешь в розыгрыше. Твой номер участника: %s";

    private final ResponseService responseService;
    private final FeatureToggleService featureToggleService;
    private final GiveawayService giveawayService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        if (!featureToggleService.isToggleActive(FeatureToggles.GIVEAWAY)) {
            return responseService.createUserResponse(user, UserState.MAIN_MENU, GIVEAWAY_DISABLED_MESSAGE);
        }

        GiveawayParticipant giveawayParticipant = giveawayService.addParticipant(user);

        return responseService.createUserResponse(user, UserState.MAIN_MENU,
                String.format(PARTICIPANT_REGISTERED_MESSAGE_FORMAT, giveawayParticipant.getParticipantNumber()));
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.GIVEAWAY;
    }
}
