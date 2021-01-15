package io.springboot.survey.utils;

import io.springboot.survey.response.AnswerResponse;
import io.springboot.survey.response.SurveyDataResponse;
import io.springboot.survey.response.SurveyResponse;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created By: Vishal Jha
 * Date: 24/08/20
 * Time: 2:20 PM
 */
public class SurveyData {

    public void getSurveyDataResponse(@NotNull SurveyResponse surveyResponse, @NotNull List<Integer> responseId, int teamCount, List<AnswerResponse> answer, String quesType, @NotNull SurveyDataResponse surveyDataResponse, int size) {
        surveyDataResponse.setQuestionText(surveyResponse.getQuestionText());
        surveyDataResponse.setAnswerResponses(answer);
        surveyDataResponse.setNumberOfTeam(teamCount);
        surveyDataResponse.setQuesType(quesType);
        surveyDataResponse.setNumberOfAnswer(size);
        surveyDataResponse.setNumberOfUserResponse(responseId.size());
    }

}
