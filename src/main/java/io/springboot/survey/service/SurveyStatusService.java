package io.springboot.survey.service;

import io.springboot.survey.pojo.survey.controller.GetSurveyInfoParam;
import io.springboot.survey.pojo.survey.controller.SurveyInfoParam;
import io.springboot.survey.response.AssigneeInformationResponse;
import io.springboot.survey.response.StatusFilteredResponse;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created By: Vishal Jha
 * Date: 19/08/20
 * Time: 9:25 PM
 */
@Service
public interface SurveyStatusService {

    MappingJacksonValue getSurveyTakenInfo(String email, int page, Integer pageSize);
    MappingJacksonValue getSurveyPendingInfo(String email, int page, Integer pageSize);
    MappingJacksonValue getSurveyInfo(GetSurveyInfoParam surveyInfoParam);
    MappingJacksonValue getSurveyInfoPending(SurveyInfoParam surveyInfoParam);
    MappingJacksonValue getSurveyInfoTaken(SurveyInfoParam surveyInfoParam);
    MappingJacksonValue getSurveyInfoAssigned(SurveyInfoParam surveyInfoParam);
    MappingJacksonValue surveyInformation(String surveyName, String creatorEmail);
    List<StatusFilteredResponse> getActiveSurvey(String email);
    MappingJacksonValue totalAssignedSurveys(String email, int page, Integer pageSize);
    List<AssigneeInformationResponse> surveyAssigneeInfo(String email);



}
