package io.springboot.survey.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class QuestionResponse {

    private String question;
    private Boolean mandatory;
    private List<String> answers;
    private String quesType;
    private String surveyName;
    private  String surveyDescription;
    private Integer numberOfOptions;
    private String answerText;
    private byte[] fileData;
    private String fileName;
    private String fileType;
}
