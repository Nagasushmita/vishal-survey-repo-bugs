package io.springboot.survey.service;

import io.springboot.survey.response.SurveyResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;


@Service
public interface SurveyTeamReportService {
     Object teamReport(@NotNull SurveyResponse surveyResponse);
}
