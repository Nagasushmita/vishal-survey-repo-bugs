package io.springboot.survey.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GetSurveyResponse {

    private String question;
    private Boolean mandatory;
    private List<String> answers;
    private String quesType;
    private String surveyName;
    private  String surveyDescription;
    private Integer numberOfOptions;

}
