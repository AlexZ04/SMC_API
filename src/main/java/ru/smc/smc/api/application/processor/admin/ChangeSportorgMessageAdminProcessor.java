package ru.smc.smc.api.application.processor.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.AvailablePlatform;
import ru.smc.smc.api.application.common.enums.DistributionGroups;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.service.faculty.FacultyService;
import ru.smc.smc.api.application.service.monitoring.MonitoringEventService;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.application.service.sportorg.SportorgService;
import ru.smc.smc.api.application.service.user.UserService;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.domain.entity.Faculty;
import ru.smc.smc.api.domain.entity.SportsOrganizer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangeSportorgMessageAdminProcessor implements MessageAdminProcessor {

    private static final String INCORRECT_FACULTY_MESSAGE = "Неверный факультет! Выбери один факультет из списка.";
    private static final String INPUT_SPORTORG_INFO_MESSAGE_FORMAT = "Текущий спорторг: %s\nВведи нового спорторга %s в формате ФАМИЛИЯ ИМЯ ссылка на соц.сеть. Пример: Иванов Иван ссылка";
    private static final String EMPTY_SPORTORG_INFO = "не указан";
    private static final String INCORRECT_SPORTORG_INFO_MESSAGE = "Некорректный формат. Введи ФАМИЛИЮ ИМЯ и ссылку на соц.сеть нового спорторга. Пример: Иванов Иван ссылка";
    private static final String INPUT_SPORTORG_USER_LINK_MESSAGE_FORMAT = "Информация о спорторге %s обновлена.\nЕсли нужно привязать спорторга к пользователю бота, введи платформу и идентификатор. Пример: VK 123456\nДопустимые платформы: %s\nЧтобы пропустить шаг, напиши Пропустить";
    private static final String INCORRECT_SPORTORG_USER_LINK_MESSAGE = "Некорректный формат. Введи платформу и идентификатор пользователя. Пример: VK 123456. Чтобы пропустить шаг, напиши Пропустить";
    private static final String SPORTORG_INFO_CHANGED_MESSAGE_FORMAT = "Информация о спорторге %s изменена. Возвращение в главное меню.";
    private static final String SPORTORG_INFO_CHANGED_DISTRIBUTION_FORMAT = "Пользователь %s обновил(-а) информацию про спорторга %s";
    private static final String SKIP_SPORTORG_USER_LINK_MESSAGE = "Пропустить";

    private final ResponseService responseService;
    private final FacultyService facultyService;
    private final SportorgService sportorgService;
    private final UserService userService;
    private final MonitoringEventService monitoringEventService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        if (user.getCurrentState() == UserState.CHANGE_SPORTORG) {
            return facultyService.findActiveFacultyByMessage(request.getMessage())
                    .map(faculty -> processCorrectFacultyChoice(user, faculty))
                    .orElseGet(() -> responseService.createUserResponse(user, UserState.CHANGE_SPORTORG, INCORRECT_FACULTY_MESSAGE));
        }

        if (user.getCurrentState() == UserState.CHANGE_SPORTORG_INFO) {
            return processSportorgInfo(request, user);
        }

        if (user.getCurrentState() == UserState.CHANGE_SPORTORG_USER_LINK) {
            return processSportorgUserLink(request, user);
        }

        return responseService.createUserResponse(user, UserState.CHANGE_SPORTORG,
                facultyService.getFacultiesChoiceMessage());
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.CHANGE_SPORTORG;
    }

    private UserResponseItem processCorrectFacultyChoice(BotUser user, Faculty faculty) {
        user.setSelectedFaculty(faculty);

        return responseService.createUserResponse(user, UserState.CHANGE_SPORTORG_INFO,
                String.format(INPUT_SPORTORG_INFO_MESSAGE_FORMAT, formCurrentSportorgInfo(faculty), faculty.getNameRu()));
    }

    private UserResponseItem processSportorgInfo(MessageRequestBody request, BotUser user) {
        String[] sportorgInfoParts = request.getMessage().trim().split("\\s+");

        if (user.getSelectedFaculty() == null || sportorgInfoParts.length != 3) {
            return responseService.createUserResponse(user, UserState.CHANGE_SPORTORG_INFO, INCORRECT_SPORTORG_INFO_MESSAGE);
        }

        Faculty selectedFaculty = user.getSelectedFaculty();
        String previousSportorgInfo = formCurrentSportorgInfo(selectedFaculty);
        String sportorgName = sportorgInfoParts[0] + " " + sportorgInfoParts[1];
        String sportorgLink = sportorgInfoParts[2];

        sportorgService.updateSportorg(selectedFaculty, sportorgName, sportorgLink);
        log.info("Администратор ({}, {}) обновил спорторга факультета {}. Было: {}. Стало: {} {}",
                user.getPlatform(), user.getIdOnPlatform(), selectedFaculty.getNameRu(), previousSportorgInfo,
                sportorgName, sportorgLink);
        monitoringEventService.sendInfo(user, formSportorgChangedMonitoringMessage(user, selectedFaculty,
                previousSportorgInfo, sportorgName, sportorgLink));

        return responseService.createUserResponse(user, UserState.CHANGE_SPORTORG_USER_LINK,
                String.format(INPUT_SPORTORG_USER_LINK_MESSAGE_FORMAT, selectedFaculty.getNameRu(), formAvailablePlatforms()));
    }

    private UserResponseItem processSportorgUserLink(MessageRequestBody request, BotUser user) {
        if (user.getSelectedFaculty() == null) {
            return responseService.createUserResponse(user, UserState.MAIN_MENU, INCORRECT_SPORTORG_USER_LINK_MESSAGE);
        }

        if (request.getMessage().equalsIgnoreCase(SKIP_SPORTORG_USER_LINK_MESSAGE)) {
            log.info("Администратор ({}, {}) пропустил привязку спорторга факультета {} к пользователю",
                    user.getPlatform(), user.getIdOnPlatform(), user.getSelectedFaculty().getNameRu());
            return finishSportorgChanging(user);
        }

        String[] sportorgUserInfoParts = request.getMessage().trim().split("\\s+");

        if (sportorgUserInfoParts.length != 2) {
            return responseService.createUserResponse(user, UserState.CHANGE_SPORTORG_USER_LINK, INCORRECT_SPORTORG_USER_LINK_MESSAGE);
        }

        AvailablePlatform platform = parsePlatform(sportorgUserInfoParts[0]);

        if (platform == null) {
            return responseService.createUserResponse(user, UserState.CHANGE_SPORTORG_USER_LINK, INCORRECT_SPORTORG_USER_LINK_MESSAGE);
        }

        BotUser sportorgUser = userService.findOrCreateAndSaveBotUser(platform, sportorgUserInfoParts[1]);
        sportorgService.bindSportorgToUser(user.getSelectedFaculty(), sportorgUser);
        log.info("Администратор ({}, {}) привязал спорторга факультета {} к пользователю ({}, {})",
                user.getPlatform(), user.getIdOnPlatform(), user.getSelectedFaculty().getNameRu(),
                sportorgUser.getPlatform(), sportorgUser.getIdOnPlatform());

        return finishSportorgChanging(user);
    }

    private UserResponseItem finishSportorgChanging(BotUser user) {
        Faculty selectedFaculty = user.getSelectedFaculty();
        user.setSelectedFaculty(null);

        return responseService.createUserResponseWithDistribution(user, UserState.MAIN_MENU,
                String.format(SPORTORG_INFO_CHANGED_MESSAGE_FORMAT, selectedFaculty.getNameRu()),
                List.of(),
                String.format(SPORTORG_INFO_CHANGED_DISTRIBUTION_FORMAT, user.getIdOnPlatform(), selectedFaculty.getNameRu()),
                DistributionGroups.ADMINS,
                false);
    }

    private String formCurrentSportorgInfo(Faculty faculty) {
        SportsOrganizer sportsOrganizer = faculty.getSportsOrganizer();

        if (sportsOrganizer == null) {
            return EMPTY_SPORTORG_INFO;
        }

        if (sportsOrganizer.getSocialLink() == null || sportsOrganizer.getSocialLink().isBlank()) {
            return sportsOrganizer.getName();
        }

        return sportsOrganizer.getName() + " " + sportsOrganizer.getSocialLink();
    }

    private String formSportorgChangedMonitoringMessage(BotUser user, Faculty faculty, String previousSportorgInfo,
                                                        String sportorgName, String sportorgLink) {
        return """
                Обновлена информация про спорторга.
                Факультет: %s
                Пользователь: %s на платформе %s
                Было: %s
                Стало: %s %s
                """.formatted(faculty.getNameRu(), user.getIdOnPlatform(), user.getPlatform(),
                previousSportorgInfo, sportorgName, sportorgLink);
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
