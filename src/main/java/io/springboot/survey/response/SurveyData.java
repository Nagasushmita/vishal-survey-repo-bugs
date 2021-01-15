package io.springboot.survey.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SurveyData {

    private String question;
    private String quesType;
    private List<String> answers ;
    private Integer numberOfOptions;
    private String answerText;
    private String file;
    private Boolean mandatory;
}
