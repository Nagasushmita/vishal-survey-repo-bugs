package io.springboot.survey.response;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static io.springboot.survey.utils.Constants.FilterConstants.ALL_RESPONSE_FILTER;

@Getter
@Setter
@NoArgsConstructor
@JsonFilter(ALL_RESPONSE_FILTER)
public class AllResponse {
    String teamName;
    String userName;
    int totalSurveys;
    int totalTemplateResponses;
    String assignedBy;
    String assignedByEmail;
    List<QuestionResponse>questionResponses;
}
