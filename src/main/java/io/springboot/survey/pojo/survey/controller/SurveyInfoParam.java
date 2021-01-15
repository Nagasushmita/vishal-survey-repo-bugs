package io.springboot.survey.pojo.survey.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created By: Vishal Jha
 * Date: 20/10/20
 * Time: 3:27 PM
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SurveyInfoParam {
    String surveyName;
    String creatorEmail;
    Integer page;
    Integer pageSize;
}
