package io.springboot.survey.response;

import io.springboot.survey.models.SurveyResponseModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DateFilterResponse {
    private List<SurveyResponseModel> surveyModelList;
    private long startDate;
    private long endDate;
}
