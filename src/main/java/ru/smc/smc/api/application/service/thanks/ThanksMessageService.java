package ru.smc.smc.api.application.service.thanks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.domain.entity.BotUser;

import java.security.SecureRandom;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ThanksMessageService {

    private static final Set<String> THANKS_MESSAGES = Set.of(
            "спасибо",
            "спасибо вам",
            "спасибо большое",
            "благодарю",
            "спс"
    );
    private static final List<String> THANKS_ANSWERS = List.of(
            "\u2764\uFE0F",
            "\uD83E\uDDE1",
            "\uD83D\uDC9B",
            "\uD83D\uDC9A",
            "\uD83D\uDC99",
            "\uD83D\uDC9C",
            "\uD83E\uDD0E",
            "\uD83D\uDDA4",
            "\uD83E\uDD0D",
            "\uD83E\uDEE7",
            "\uD83E\uDEE5",
            "\uD83E\uDEE6"
    );

    private final ResponseService responseService;
    private final SecureRandom random = new SecureRandom();

    public boolean isThanksMessage(String message) {
        return THANKS_MESSAGES.contains(normalizeMessage(message));
    }

    public UserResponseItem processThanksMessage(BotUser user) {
        return responseService.createUserResponse(user, user.getCurrentState(), getRandomAnswer());
    }

    private String normalizeMessage(String message) {
        if (message == null) {
            return "";
        }

        return message.strip()
                .replaceAll("^[\\p{Punct}\\s]+|[\\p{Punct}\\s]+$", "")
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }

    private String getRandomAnswer() {
        return THANKS_ANSWERS.get(random.nextInt(THANKS_ANSWERS.size()));
    }
}
