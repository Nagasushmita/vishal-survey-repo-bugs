package io.springboot.survey.response;

import io.springboot.survey.models.TemplateModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TemplateResponse {
    private int noOfQuestions;
    private int noOfSurveys;
    private int totalResponses;
    private TemplateModel template;

}