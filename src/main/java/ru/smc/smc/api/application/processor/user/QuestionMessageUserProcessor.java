package ru.smc.smc.api.application.processor.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.constant.FaqQuestionsTexts;
import ru.smc.smc.api.application.common.enums.DistributionGroups;
import ru.smc.smc.api.application.common.enums.MessageMeaningType;
import ru.smc.smc.api.application.common.enums.UserState;
import ru.smc.smc.api.application.common.model.request.MessageRequestBody;
import ru.smc.smc.api.application.common.model.response.ElementModel;
import ru.smc.smc.api.application.common.model.response.PreviewMessage;
import ru.smc.smc.api.application.common.model.response.UserResponseItem;
import ru.smc.smc.api.application.properties.KeyboardsProperties;
import ru.smc.smc.api.application.service.response.ResponseService;
import ru.smc.smc.api.application.service.user.UserMessageToAdminsFormatter;
import ru.smc.smc.api.application.utilities.FaqUtility;
import ru.smc.smc.api.application.utilities.FileUtility;
import ru.smc.smc.api.domain.entity.BotUser;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionMessageUserProcessor implements MessageUserProcessor {

    private static final String ADMIN_CHANNEL = "admin-channel";
    private static final String QUESTION_DISTRIBUTION_HEADER = "Пользователь отправил вопрос администраторам";
    private static final String QUESTION_MENU_PREVIEW_MESSAGE = """
            Часто задаваемые вопросы представлены ниже.
            
            Также ты можешь задать вопрос напрямую администраторам — просто отправь его в чат.""";
    private static final String FAQ_QUESTIONS_PREVIEW_MESSAGE = "Вот список вопросов:";

    private final ResponseService responseService;
    private final UserMessageToAdminsFormatter userMessageToAdminsFormatter;

    @Override
    public UserResponseItem processMessage(MessageRequestBody request, BotUser user) {

        if (user.getCurrentState() == UserState.MAIN_MENU) {
            return responseService.createUserResponseWithCustomPreviewMessages(user, UserState.QUESTION,
                    QUESTION_MENU_PREVIEW_MESSAGE, createPreviewMessages());
        }

        if (FaqUtility.checkIfQuestionIsFaq(request.getMessage().toLowerCase())) {
            return responseService.createUserResponse(user, UserState.QUESTION,
                    FileUtility.getFileMessage(FaqUtility.getPathToAnswer(request.getMessage().toLowerCase())));
        }

        log.info("Пользователь ({}, {}) отправил вопрос администраторам",
                user.getPlatform(), user.getIdOnPlatform());
        return responseService.createUserResponseWithDistribution(user, UserState.QUESTION, FileUtility.getFileMessage("your-question-redirected"),
                new ArrayList<>(), userMessageToAdminsFormatter.formMessage(QUESTION_DISTRIBUTION_HEADER, request, user),
                DistributionGroups.ADMINS, false, ADMIN_CHANNEL);
    }

    @Override
    public MessageMeaningType meaning() {
        return MessageMeaningType.ASK_QUESTION;
    }

    private List<List<ElementModel>> createInlineKeyboard() {
        return List.of(
                List.of(KeyboardsProperties.createFaqInlineButton(FaqQuestionsTexts.FIRST_QUESTION)),
                List.of(KeyboardsProperties.createFaqInlineButton(FaqQuestionsTexts.SECOND_QUESTION)),
                List.of(KeyboardsProperties.createFaqInlineButton(FaqQuestionsTexts.THIRD_QUESTION)),
                List.of(KeyboardsProperties.createFaqInlineButton(FaqQuestionsTexts.FOURTH_QUESTION)),
                List.of(KeyboardsProperties.createFaqInlineButton(FaqQuestionsTexts.FIFTH_QUESTION)),
                List.of(KeyboardsProperties.createFaqInlineButton(FaqQuestionsTexts.SIXTH_QUESTION))
        );
    }

    private List<PreviewMessage> createPreviewMessages() {
        return List.of(new PreviewMessage(FAQ_QUESTIONS_PREVIEW_MESSAGE, createInlineKeyboard()));
    }
}
