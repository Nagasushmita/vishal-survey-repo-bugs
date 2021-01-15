package io.springboot.survey.response;

import com.fasterxml.jackson.annotation.JsonFilter;
import io.springboot.survey.models.SurveyModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

import static io.springboot.survey.utils.Constants.FilterConstants.SURVEY_PAGINATION_FILTER;

@Getter
@Setter
@NoArgsConstructor
@JsonFilter(SURVEY_PAGINATION_FILTER)
public class SurveyPagination {

    private List<StatusFilteredResponse> responsesList;
    private int pageRequired;
    List<HashMap<String,String>> hashMapList;
    private List<SurveyModel>surveyModelList;
    private List<PendingTakenResponse>pendingTakenResponses;
}
