package ru.smc.smc.api.application.utility;

import lombok.experimental.UtilityClass;
import ru.smc.smc.api.application.common.constant.FaqQuestionsTexts;

import java.util.Map;

@UtilityClass
public class FaqUtility {

    private static final Map<String, String> FAQ = Map.of(
            FaqQuestionsTexts.FIRST_QUESTION.toLowerCase(), "faq/01",
            FaqQuestionsTexts.SECOND_QUESTION.toLowerCase(), "faq/02",
            FaqQuestionsTexts.THIRD_QUESTION.toLowerCase(), "faq/03",
            FaqQuestionsTexts.FOURTH_QUESTION.toLowerCase(), "faq/04",
            FaqQuestionsTexts.FIFTH_QUESTION.toLowerCase(), "faq/05",
            FaqQuestionsTexts.SIXTH_QUESTION.toLowerCase(), "faq/06"
    );

    public boolean checkIfQuestionIsFaq(String question) {
        return FAQ.containsKey(question);
    }

    public String getPathToAnswer(String question) {
        return FAQ.get(question);
    }
}
