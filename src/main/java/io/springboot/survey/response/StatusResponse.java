package io.springboot.survey.response;
import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static io.springboot.survey.utils.Constants.FilterConstants.STATUS_RESPONSE_FILTER;

@Getter
@Setter
@NoArgsConstructor
@JsonFilter(STATUS_RESPONSE_FILTER)
public class StatusResponse {
    private String surveyName;
    private String assignedBy;
    private long creationDate;
    private long expirationDate;
    private String assignedByEmail;
    private String templateName;
    private List<EmployeeDetails> employeeDetails;
    private int surveyTakenCount;
    private int surveyPendingCount;
    private String surveyDescription;
    private int noOfQuestion;
    private int assignedTo;
    private int pageRequired;
    private boolean taken;
    private String teamName;
}