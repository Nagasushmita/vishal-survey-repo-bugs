package io.springboot.survey.pojo.survey.impl;

import io.springboot.survey.models.SurveyStatusModel;
import io.springboot.survey.response.StatusFilteredResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created By: Vishal Jha
 * Date: 20/10/20
 * Time: 3:55 PM
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SurveyPaginationParam {
    int page;
    Integer pageSize;
    List<StatusFilteredResponse> responses;
     List<SurveyStatusModel> model;
}
