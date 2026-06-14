package ru.smc.smc.api.application.processor.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.AvailablePlatform;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.application.service.stats.UserStatsService;
import ru.smc.smc.api.domain.entity.BotUser;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUserInfoMessageAdminProcessor implements MessageAdminProcessor {

    private static final String INPUT_USER_INFO_MESSAGE_FORMAT = "Введите платформу и идентификатор пользователя. Пример: VK 123456\nДопустимые платформы: %s";
    private static final String INCORRECT_USER_INFO_MESSAGE = "Некорректный формат. Введите платформу и идентификатор пользователя. Пример: VK 123456";

    private final ResponseService responseService;
    private final UserStatsService userStatsService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        if (user.getCurrentState() == UserState.GET_USER_INFO) {
            return processUserInfo(request, user);
        }

        return responseService.createUserResponse(user, UserState.GET_USER_INFO, formInputUserInfoMessage());
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.GET_USER_INFO;
    }

    private UserResponseItem processUserInfo(MessageRequestBody request, BotUser user) {
        String[] userInfoParts = request.getMessage().trim().split("\\s+");

        if (userInfoParts.length != 2) {
            return responseService.createUserResponse(user, UserState.GET_USER_INFO, INCORRECT_USER_INFO_MESSAGE);
        }

        AvailablePlatform platform = parsePlatform(userInfoParts[0]);

        if (platform == null) {
            return responseService.createUserResponse(user, UserState.GET_USER_INFO, INCORRECT_USER_INFO_MESSAGE);
        }

        return responseService.createUserResponse(user, UserState.MAIN_MENU,
                userStatsService.getUserInfo(platform, userInfoParts[1]));
    }

    private String formInputUserInfoMessage() {
        return String.format(INPUT_USER_INFO_MESSAGE_FORMAT, formAvailablePlatforms());
    }

    private String formAvailablePlatforms() {
        return Arrays.stream(AvailablePlatform.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    private AvailablePlatform parsePlatform(String platform) {
        try {
            return AvailablePlatform.valueOf(platform.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
