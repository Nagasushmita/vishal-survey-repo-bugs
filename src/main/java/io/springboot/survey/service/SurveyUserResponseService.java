package io.springboot.survey.service;

import io.springboot.survey.response.QuestionResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SurveyUserResponseService {

    List<QuestionResponse> getUserResponse(List<Integer> uniqueQuestion, int surveyId, int responseId);

}
