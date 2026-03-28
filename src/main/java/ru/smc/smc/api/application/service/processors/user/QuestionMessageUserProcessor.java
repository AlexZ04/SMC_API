package ru.smc.smc.api.application.service.processors.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.constant.FaqQuestionsTexts;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.ElementModel;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.properties.KeyboardsProperties;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.application.utilities.FaqUtility;
import ru.smc.smc.api.application.utilities.FileUtility;
import ru.smc.smc.api.domain.entity.BotUser;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionMessageUserProcessor implements MessageUserProcessor {

    private final ResponseService responseService;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {

        if (user.getCurrentState() == UserState.MAIN_MENU) {
            return responseService.createUserResponseWithInlineKeyboard(user, UserState.QUESTION, FileUtility.getFileMessage("faq/general-message"),
                    createInlineKeyboard());
        }

        if (FaqUtility.checkIfQuestionIsFaq(request.getMessage().toLowerCase())) {
            return responseService.createUserResponseWithInlineKeyboard(user, UserState.QUESTION,
                    FileUtility.getFileMessage(FaqUtility.getPathToAnswer(request.getMessage().toLowerCase())),
                    createInlineKeyboard());
        }

        // todo сообщение передать также админам!!
        return responseService.createUserResponseWithInlineKeyboard(user, UserState.MAIN_MENU, FileUtility.getFileMessage("your-question-redirected"),
                createInlineKeyboard());
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.ASK_QUESTION;
    }

    private List<List<ElementModel>> createInlineKeyboard() {
        return List.of(
                List.of(KeyboardsProperties.createInlineButton(FaqQuestionsTexts.FIRST_QUESTION)),
                List.of(KeyboardsProperties.createInlineButton(FaqQuestionsTexts.SECOND_QUESTION)),
                List.of(KeyboardsProperties.createInlineButton(FaqQuestionsTexts.THIRD_QUESTION)),
                List.of(KeyboardsProperties.createInlineButton(FaqQuestionsTexts.FOURTH_QUESTION)),
                List.of(KeyboardsProperties.createInlineButton(FaqQuestionsTexts.FIFTH_QUESTION)),
                List.of(KeyboardsProperties.createInlineButton(FaqQuestionsTexts.SIXTH_QUESTION))
        );
    }
}
