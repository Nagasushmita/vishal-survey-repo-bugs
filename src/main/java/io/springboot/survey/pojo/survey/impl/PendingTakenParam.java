package io.springboot.survey.pojo.survey.impl;

import io.springboot.survey.mapper.SurveyStatusDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created By: Vishal Jha
 * Date: 20/10/20
 * Time: 4:36 PM
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PendingTakenParam {
    int page;
    Integer pageSize;
    int surveyId;
    List<SurveyStatusDto> surveyStatusModels;
}
