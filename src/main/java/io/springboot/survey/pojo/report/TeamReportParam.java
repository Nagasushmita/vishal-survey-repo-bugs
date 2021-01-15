package io.springboot.survey.pojo.report;

import io.springboot.survey.models.SurveyResponseModel;
import io.springboot.survey.response.SurveyResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamReportParam {
    int index;
    SurveyResponse surveyResponse;
    List<SurveyResponseModel> surveyList;
    List<Integer> responseId;
    int teamId;
    int teamCount;
    List<Integer> surveyId;
}
