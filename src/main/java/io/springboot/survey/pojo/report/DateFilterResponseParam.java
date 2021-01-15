package io.springboot.survey.pojo.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class DateFilterResponseParam {
    int surveyId;
    Long creationDate;
    long endDate;
    long startDate;
}
