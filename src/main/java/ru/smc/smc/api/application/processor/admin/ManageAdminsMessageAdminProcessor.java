package ru.smc.smc.api.application.processor.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.constant.BotCommands;
import ru.smc.smc.api.application.common.enums.AvailablePlatform;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserRole;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.application.service.user.UserService;
import ru.smc.smc.api.domain.entity.BotUser;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManageAdminsMessageAdminProcessor implements MessageAdminProcessor {

    private static final String INPUT_ADMIN_INFO_MESSAGE_FORMAT = "Введи платформу и идентификатор пользователя. Пример: TG 123456\nДопустимые платформы: %s";
    private static final String INCORRECT_ADMIN_INFO_MESSAGE = "Некорректный формат. Введи платформу и идентификатор пользователя. Пример: TG 123456";
    private static final String NO_RIGHTS_MESSAGE = "Недостаточно прав для выполнения этого действия.";

    private final ResponseService responseService;
    private final UserService userService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        if (isWaitingForAdminInfo(user.getCurrentState())) {
            return processAdminInfo(request, user);
        }

        if (request.getMessage().equalsIgnoreCase(BotCommands.ADD_ADMIN_COMMAND)) {
            return responseService.createUserResponse(user, UserState.ADD_ADMIN, formInputAdminInfoMessage());
        }

        if (!isSuperAdmin(user)) {
            return responseService.createUserResponse(user, UserState.MAIN_MENU, NO_RIGHTS_MESSAGE);
        }

        if (request.getMessage().equalsIgnoreCase(BotCommands.ADD_SUPER_ADMIN_COMMAND)) {
            return responseService.createUserResponse(user, UserState.ADD_SUPER_ADMIN, formInputAdminInfoMessage());
        }

        return responseService.createUserResponse(user, UserState.REMOVE_ADMIN, formRemoveAdminInfoMessage());
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.MANAGE_ADMINS;
    }

    private UserResponseItem processAdminInfo(MessageRequestBody request, BotUser user) {
        String[] adminInfoParts = request.getMessage().trim().split("\\s+");

        if (adminInfoParts.length != 2) {
            return responseService.createUserResponse(user, user.getCurrentState(), INCORRECT_ADMIN_INFO_MESSAGE);
        }

        AvailablePlatform platform = parsePlatform(adminInfoParts[0]);

        if (platform == null) {
            return responseService.createUserResponse(user, user.getCurrentState(), INCORRECT_ADMIN_INFO_MESSAGE);
        }

        String resultMessage = switch (user.getCurrentState()) {
            case ADD_ADMIN -> userService.addOrUpdateAdminRole(platform, adminInfoParts[1], UserRole.ADMIN);
            case ADD_SUPER_ADMIN -> userService.addOrUpdateAdminRole(platform, adminInfoParts[1], UserRole.SUPER_ADMIN);
            case REMOVE_ADMIN -> userService.removeAdminRole(platform, adminInfoParts[1]);
            default -> INCORRECT_ADMIN_INFO_MESSAGE;
        };

        return responseService.createUserResponse(user, UserState.MAIN_MENU, resultMessage);
    }

    private boolean isWaitingForAdminInfo(UserState userState) {
        return userState == UserState.ADD_ADMIN ||
                userState == UserState.ADD_SUPER_ADMIN ||
                userState == UserState.REMOVE_ADMIN;
    }

    private String formInputAdminInfoMessage() {
        return String.format(INPUT_ADMIN_INFO_MESSAGE_FORMAT, formAvailablePlatforms());
    }

    private String formRemoveAdminInfoMessage() {
        return userService.getAdminsInfo() + "\n\n" + formInputAdminInfoMessage();
    }

    private String formAvailablePlatforms() {
        return Arrays.stream(AvailablePlatform.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    private boolean isSuperAdmin(BotUser user) {
        return user.getRole() == UserRole.SUPER_ADMIN;
    }

    private AvailablePlatform parsePlatform(String platform) {
        try {
            return AvailablePlatform.valueOf(platform.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
