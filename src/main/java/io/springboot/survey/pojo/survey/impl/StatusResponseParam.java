package io.springboot.survey.pojo.survey.impl;

import io.springboot.survey.models.SurveyModel;
import io.springboot.survey.response.StatusResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created By: Vishal Jha
 * Date: 20/10/20
 * Time: 3:01 PM
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StatusResponseParam {
    Integer pageSize;
    List<StatusResponse> responses;
    List<SurveyModel> modelList;
    Integer modelSize;

}
