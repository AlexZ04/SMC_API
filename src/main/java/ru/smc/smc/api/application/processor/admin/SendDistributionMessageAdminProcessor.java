package ru.smc.smc.api.application.processor.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.AdminDistributionType;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.ElementModel;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.properties.KeyboardsProperties;
import ru.smc.smc.api.application.service.distribution.DistributionSendService;
import ru.smc.smc.api.application.service.distribution.DistributionSendServiceResolver;
import ru.smc.smc.api.application.service.faculty.FacultyService;
import ru.smc.smc.api.application.service.monitoring.MonitoringEventService;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.domain.entity.Faculty;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SendDistributionMessageAdminProcessor implements MessageAdminProcessor {

    private static final String SEND_DISTRIBUTION_MENU_MESSAGE = "Выберете тип рассылки:";
    private static final String CONFIRMATION_MESSAGE = "Для отправки сообщения введите ПОДТВЕРДИТЬ";
    private static final String CONFIRMATION_WORD = "ПОДТВЕРДИТЬ";
    private static final String INCORRECT_DISTRIBUTION_TYPE_MESSAGE = "Некорректный тип рассылки. Выберете тип рассылки из списка.";
    private static final String COMPETITIONS_FACULTIES_MESSAGE = "Введите номера или наименования факультетов, для которых будет отправлена рассылка (ввод через ;). Можно вводить комбинированно.";
    private static final String INCORRECT_FACULTIES_MESSAGE = "Некорректный список факультетов. Введите номера или наименования факультетов через ;";
    private static final String COMPETITIONS_CONFIRMATION_MESSAGE_FORMAT = "Сообщение отправится факультетам %s\n" + CONFIRMATION_MESSAGE;
    private static final String INCORRECT_CONFIRMATION_MESSAGE = "Для отправки сообщения введите ПОДТВЕРДИТЬ";
    private static final String DISTRIBUTION_SENT_MESSAGE_FORMAT = "Рассылка %s отправлена";

    private final ResponseService responseService;
    private final FacultyService facultyService;
    private final DistributionSendServiceResolver distributionSendServiceResolver;
    private final MonitoringEventService monitoringEventService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        if (user.getCurrentState() == UserState.SEND_DISTRIBUTION) {
            return processDistributionTypeChoice(request, user);
        }

        if (user.getCurrentState() == UserState.SEND_DISTRIBUTION_COMPETITIONS_FACULTIES) {
            return processCompetitionsFacultiesChoice(request, user);
        }

        if (user.getCurrentState() == UserState.SEND_DISTRIBUTION_CONFIRMATION) {
            return processDistributionConfirmation(request, user);
        }

        clearSelectedDistributionInfo(user);

        return responseService.createUserResponseWithInlineKeyboard(user, UserState.SEND_DISTRIBUTION,
                SEND_DISTRIBUTION_MENU_MESSAGE, createInlineKeyboard());
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.SEND_DISTRIBUTION;
    }

    private UserResponseItem processDistributionTypeChoice(MessageRequestBody request, BotUser user) {
        return AdminDistributionType.findByButtonText(request.getMessage())
                .map(type -> processCorrectDistributionTypeChoice(user, type))
                .orElseGet(() -> responseService.createUserResponseWithInlineKeyboard(user, UserState.SEND_DISTRIBUTION,
                        INCORRECT_DISTRIBUTION_TYPE_MESSAGE, createInlineKeyboard()));
    }

    private UserResponseItem processCorrectDistributionTypeChoice(BotUser user, AdminDistributionType distributionType) {
        user.setSelectedDistributionType(distributionType.name());
        user.setSelectedDistributionFacultyIds(null);

        if (distributionType == AdminDistributionType.COMPETITIONS) {
            return responseService.createUserResponseWithPreviewMessages(user, UserState.SEND_DISTRIBUTION_COMPETITIONS_FACULTIES,
                    COMPETITIONS_FACULTIES_MESSAGE, List.of(facultyService.getAllFacultiesMessage()));
        }

        return createDistributionConfirmationResponse(user, CONFIRMATION_MESSAGE);
    }

    private UserResponseItem processCompetitionsFacultiesChoice(MessageRequestBody request, BotUser user) {
        return facultyService.findActiveFacultiesByMessage(request.getMessage())
                .map(faculties -> processCorrectCompetitionsFacultiesChoice(user, faculties))
                .orElseGet(() -> responseService.createUserResponse(user, UserState.SEND_DISTRIBUTION_COMPETITIONS_FACULTIES,
                        INCORRECT_FACULTIES_MESSAGE));
    }

    private UserResponseItem processCorrectCompetitionsFacultiesChoice(BotUser user, List<Faculty> faculties) {
        user.setSelectedDistributionFacultyIds(facultyService.formFacultyIds(faculties));

        return createDistributionConfirmationResponse(user,
                String.format(COMPETITIONS_CONFIRMATION_MESSAGE_FORMAT, facultyService.formFacultyNames(faculties)));
    }

    private UserResponseItem processDistributionConfirmation(MessageRequestBody request, BotUser user) {
        AdminDistributionType distributionType = AdminDistributionType.valueOf(user.getSelectedDistributionType());

        if (!request.getMessage().trim().equals(CONFIRMATION_WORD)) {
            return createDistributionConfirmationResponse(user, INCORRECT_CONFIRMATION_MESSAGE);
        }

        DistributionSendService distributionSendService = distributionSendServiceResolver.resolve(distributionType);
        String distributionText = distributionSendService.getDistributionText();
        List<UUID> distributionFiles = distributionSendService.getDistributionFiles();
        var receivers = distributionSendService.getReceivers(user);
        String selectedDistributionFacultyIds = user.getSelectedDistributionFacultyIds();

        clearSelectedDistributionInfo(user);
        monitoringEventService.sendDistributionSentInfo(user, formDistributionSentMonitoringMessage(distributionType, user,
                distributionText, distributionFiles, selectedDistributionFacultyIds));

        return responseService.createUserResponseWithDistributionReceivers(user, UserState.MAIN_MENU,
                String.format(DISTRIBUTION_SENT_MESSAGE_FORMAT, distributionType.getResultText()),
                distributionText,
                false,
                receivers,
                distributionFiles);
    }

    private UserResponseItem createDistributionConfirmationResponse(BotUser user, String confirmationMessage) {
        AdminDistributionType distributionType = AdminDistributionType.valueOf(user.getSelectedDistributionType());
        DistributionSendService distributionSendService = distributionSendServiceResolver.resolve(distributionType);

        return responseService.createUserResponseWithPreviewMessages(user, UserState.SEND_DISTRIBUTION_CONFIRMATION,
                distributionSendService.getDistributionText(), List.of(confirmationMessage),
                distributionSendService.getDistributionFiles());
    }

    private List<List<ElementModel>> createInlineKeyboard() {
        return List.of(
                List.of(KeyboardsProperties.createInlineButton(AdminDistributionType.GENERAL.getButtonText())),
                List.of(KeyboardsProperties.createInlineButton(AdminDistributionType.EVENTS.getButtonText())),
                List.of(KeyboardsProperties.createInlineButton(AdminDistributionType.COMPETITIONS.getButtonText())),
                List.of(KeyboardsProperties.createInlineButton(AdminDistributionType.SCHEDULE.getButtonText())),
                List.of(KeyboardsProperties.createInlineButton(AdminDistributionType.GIVEAWAY.getButtonText()))
        );
    }

    private void clearSelectedDistributionInfo(BotUser user) {
        user.setSelectedDistributionType(null);
        user.setSelectedDistributionFacultyIds(null);
    }

    private String formDistributionSentMonitoringMessage(AdminDistributionType distributionType, BotUser user,
                                                         String distributionText, List<UUID> distributionFiles,
                                                         String selectedDistributionFacultyIds) {
        return """
                Отправлена рассылка.
                Тип: %s
                Пользователь: %s на платформе %s
                Текст: %s
                Файлы: %s
                Факультеты: %s
                """.formatted(distributionType.getButtonText(), user.getIdOnPlatform(), user.getPlatform(), distributionText,
                distributionFiles.size(), formMonitoringFacultyNames(selectedDistributionFacultyIds));
    }

    private String formMonitoringFacultyNames(String selectedDistributionFacultyIds) {
        if (selectedDistributionFacultyIds == null || selectedDistributionFacultyIds.isBlank()) {
            return "не указаны";
        }

        String facultyNames = List.of(selectedDistributionFacultyIds.split(";")).stream()
                .map(String::trim)
                .map(this::findFacultyName)
                .collect(Collectors.joining(", "));

        return facultyNames.isBlank() ? "не указаны" : facultyNames;
    }

    private String findFacultyName(String facultyId) {
        try {
            return facultyService.findActiveFacultyById(Integer.parseInt(facultyId))
                    .map(Faculty::getNameRu)
                    .orElse(facultyId);
        } catch (NumberFormatException exception) {
            return facultyId;
        }
    }
}
