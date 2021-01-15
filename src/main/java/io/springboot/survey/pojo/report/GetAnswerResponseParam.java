package io.springboot.survey.pojo.report;

import io.springboot.survey.models.SurveyResponseModel;
import io.springboot.survey.response.AnswerResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created By: Vishal Jha
 * Date: 21/10/20
 * Time: 2:50PM
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetAnswerResponseParam {

    List<Integer> answerId;
    List<AnswerResponse> answer;
    List<SurveyResponseModel> surveyList;
    List<Integer> responseId;
}

