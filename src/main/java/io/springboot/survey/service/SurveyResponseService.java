package io.springboot.survey.service;

import io.springboot.survey.request.UserRequest;
import io.springboot.survey.request.UserSurveyRequest;
import io.springboot.survey.response.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SurveyResponseService {
    ResponseEntity<ResponseMessage> surveyResponse(UserSurveyRequest userSurveyRequest);
    List<GetSurveyResponse> getSurvey(String surveyName, String creatorEmail);
    List<GetSurveyResponse>  getSurveyByLink(String link);
    List<QuestionResponse> getUserResponse(UserRequest userRequest);


}
