package io.springboot.survey.response;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SurveyDataResponse {

    private Integer index;
    private String questionText;
    private Boolean mandatory;
    private List<AnswerResponse> answerResponses;
    private String quesType;
    private int numberOfUserResponse;
    private int totalNumberOfResponse;
    private int numberOfAnswer;
    private String teamName;
    private int numberOfTeam;
    private long startDate;
    private long endDate;
    private boolean isTeam;
}
