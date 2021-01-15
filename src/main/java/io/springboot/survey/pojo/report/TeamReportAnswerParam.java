package io.springboot.survey.pojo.report;

import io.springboot.survey.models.SurveyResponseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeamReportAnswerParam {
    List<SurveyResponseModel> surveyList;
    Set<String> answerText;
    List<Integer> answerId;
    List<Integer> responseId;
}
