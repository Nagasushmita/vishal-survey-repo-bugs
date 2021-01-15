package io.springboot.survey.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AssigneeInformationResponse {

    String role;
    int pendingCount;
    int takenCount;
    int totalCount;

}