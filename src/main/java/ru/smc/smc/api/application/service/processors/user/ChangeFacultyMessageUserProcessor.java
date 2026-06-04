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
import ru.smc.smc.api.application.service.faculty.FacultyService;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.domain.entity.Faculty;
import ru.smc.smc.api.domain.entity.Subscription;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChangeFacultyMessageUserProcessor implements MessageUserProcessor {

    private static final String FACULTY_CHANGED_MESSAGE_FORMAT = "Факультет настроен: %s";
    private static final String FACULTY_RESET_MESSAGE = "Факультет сброшен";
    private static final String INCORRECT_FACULTY_MESSAGE = "Некорректный факультет. Выберите факультет из списка.";
    private static final String RESET_FACULTY_BUTTON = "Сбросить факультет";
    private static final String UNSUBSCRIBE_BUTTON = "Отписаться от уведомлений";
    private static final String BACK_BUTTON = "Назад";

    private final ResponseService responseService;
    private final FacultyService facultyService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {
        if (user.getCurrentState() != UserState.CHANGE_FACULTY) {
            user.setSelectedDistributionType(null);

            return responseService.createUserResponseWithInlineKeyboard(user, UserState.CHANGE_FACULTY,
                    facultyService.getFacultiesChoiceMessage(), createChangeFacultyKeyboard());
        }

        if (request.getMessage().equalsIgnoreCase(RESET_FACULTY_BUTTON)) {
            return processFacultyReset(user);
        }

        return facultyService.findActiveFacultyByMessage(request.getMessage())
                .map(faculty -> processCorrectFacultyChoice(user, faculty))
                .orElseGet(() -> responseService.createUserResponseWithPreviewMessagesAndInlineKeyboard(user,
                        UserState.CHANGE_FACULTY, INCORRECT_FACULTY_MESSAGE,
                        List.of(facultyService.getFacultiesChoiceMessage()), createChangeFacultyKeyboard()));
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.CHANGE_FACULTY;
    }

    private UserResponseItem processCorrectFacultyChoice(BotUser user, Faculty faculty) {
        user.setFaculty(faculty);

        if (UserDistributionType.COMPETITIONS.name().equals(user.getSelectedDistributionType())) {
            user.setSelectedDistributionType(null);
            UserDistributionType.COMPETITIONS.subscribe(getOrCreateSubscription(user));

            return responseService.createUserResponseWithInlineKeyboard(user, UserState.SET_DISTRIBUTION,
                    UserDistributionType.COMPETITIONS.getSubscribeMessage(),
                    createDistributionMenuKeyboard());
        }

        user.setSelectedDistributionType(null);

        return responseService.createUserResponse(user, UserState.MAIN_MENU,
                String.format(FACULTY_CHANGED_MESSAGE_FORMAT, faculty.getNameRu()));
    }

    private UserResponseItem processFacultyReset(BotUser user) {
        user.setFaculty(facultyService.findDefaultFaculty());
        user.setSelectedDistributionType(null);

        return responseService.createUserResponse(user, UserState.MAIN_MENU, FACULTY_RESET_MESSAGE);
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

    private List<List<ElementModel>> createChangeFacultyKeyboard() {
        return List.of(
                List.of(createBlackButton(RESET_FACULTY_BUTTON))
        );
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

    private ElementModel createWhiteButton(String message) {
        return new ElementModel(ElementType.BUTTON, null, message, Colors.WHITE.getColor(), Colors.BLACK.getColor());
    }

    private ElementModel createBlackButton(String message) {
        return new ElementModel(ElementType.BUTTON, null, message, Colors.BLACK.getColor(), Colors.WHITE.getColor());
    }
}
