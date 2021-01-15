package io.springboot.survey.service;

import io.springboot.survey.pojo.report.GetAnswerResponseParam;
import io.springboot.survey.response.GraphListResponse;
import io.springboot.survey.response.SurveyResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service
public interface ConsolidatedReportService {
    Object consolidatedReports(@NotNull SurveyResponse surveyResponse) throws ParseException;
    @NotNull List<Integer> getAnswersId(@NotNull SurveyResponse resp, Integer surveyId);
     GraphListResponse reportFilter(@NotNull SurveyResponse resp, int surveyId) throws ParseException;
     int getAnswerResponse(GetAnswerResponseParam param);
}
