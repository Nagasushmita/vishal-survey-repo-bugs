package io.springboot.survey.pojo.report;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamFilterSwitchParam {
    String filter;
    int number;
    List<Integer> surveyId;
    int teamId;
}
