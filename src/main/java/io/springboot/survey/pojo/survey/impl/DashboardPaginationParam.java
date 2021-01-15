package io.springboot.survey.pojo.survey.impl;

import io.springboot.survey.response.StatusFilteredResponse;
import io.springboot.survey.response.SurveyPagination;
import io.springboot.survey.utils.Pagination;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created By: Vishal Jha
 * Date: 20/10/20
 * Time: 5:40 PM
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardPaginationParam {
    int page;
    Integer pageSize;
     Pagination pagination;
     SurveyPagination surveyPagination;
     List<StatusFilteredResponse> surveyModels;
}
