package io.springboot.survey.pojo.report;

import io.springboot.survey.models.SurveyResponseModel;
import io.springboot.survey.response.SurveyResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created By: Vishal Jha
 * Date: 21/10/20
 * Time: 2:46 PM
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConsolidatedReportParam {
    int index;
    int surveyId;
    SurveyResponse surveyResponse;
    List<SurveyResponseModel> surveyList;
    List<Integer> responseId;
    int userId;
    int teamCount;
}
