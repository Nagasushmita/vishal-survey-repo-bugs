package io.springboot.survey.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatusFilteredResponse {
    private String surveyName;
    private String assignedBy;
    private long timestamp;
    private long creationDate;
    private long expirationDate;
    private String assignedByEmail;
    private int surveyTakenCount;
    private int surveyPendingCount;
    private String surveyDescription;
    private int noOfQuestion;
    private int assignedTo;
    private String teamName;
    private boolean taken;
}