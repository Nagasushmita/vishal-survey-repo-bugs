package io.springboot.survey.pojo.report;

import io.springboot.survey.models.SurveyResponseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamDateFilterParam {
    List<Integer> surveyId;
    int teamId;
    List<SurveyResponseModel> surveyResponseModels;
    Long creationDate;
    long endDate;
    long startDate;
}
