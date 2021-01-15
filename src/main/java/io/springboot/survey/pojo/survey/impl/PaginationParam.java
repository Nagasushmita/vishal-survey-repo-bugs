package io.springboot.survey.pojo.survey.impl;

import io.springboot.survey.mapper.SurveyStatusDto;
import io.springboot.survey.response.PendingTakenResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created By: Vishal Jha
 * Date: 20/10/20
 * Time: 4:39 PM
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationParam {


    int page;
    Integer pageSize;
    List<SurveyStatusDto> surveyStatusModels;
    List<PendingTakenResponse>responseList;
}
