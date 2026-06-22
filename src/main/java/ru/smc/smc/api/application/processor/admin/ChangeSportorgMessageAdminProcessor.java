package ru.smc.smc.api.application.processor.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.DistributionGroups;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.service.faculty.FacultyService;
import ru.smc.smc.api.application.service.monitoring.MonitoringEventService;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.application.service.sportorg.SportorgService;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.domain.entity.Faculty;
import ru.smc.smc.api.domain.entity.SportsOrganizer;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChangeSportorgMessageAdminProcessor implements MessageAdminProcessor {

    private static final String INCORRECT_FACULTY_MESSAGE = "Неверный факультет! Выбери один факультет из списка.";
    private static final String INPUT_SPORTORG_INFO_MESSAGE_FORMAT = "Текущий спорторг: %s\nВведи нового спорторга %s в формате ФАМИЛИЯ ИМЯ ссылка на соц.сеть. Пример: Иванов Иван ссылка";
    private static final String EMPTY_SPORTORG_INFO = "не указан";
    private static final String INCORRECT_SPORTORG_INFO_MESSAGE = "Некорректный формат. Введи ФАМИЛИЮ ИМЯ и ссылку на соц.сеть нового спорторга. Пример: Иванов Иван ссылка";
    private static final String SPORTORG_INFO_CHANGED_MESSAGE_FORMAT = "Информация о спорторге %s изменена. Возвращение в главное меню.";
    private static final String SPORTORG_INFO_CHANGED_DISTRIBUTION_FORMAT = "Пользователь %s обновил(-а) информацию про спорторга %s";

    private final ResponseService responseService;
    private final FacultyService facultyService;
    private final SportorgService sportorgService;
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
        monitoringEventService.sendInfo(user, formSportorgChangedMonitoringMessage(user, selectedFaculty,
                previousSportorgInfo, sportorgName, sportorgLink));
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
}
