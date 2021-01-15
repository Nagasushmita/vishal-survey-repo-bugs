package io.springboot.survey.service;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface SurveyDashboardService {
    Map<String, Integer> hrDashboardInfo(String email);
    MappingJacksonValue hrDashboardGraph(String email);
    Map<String, Integer> managerDashboard(String email);
    MappingJacksonValue totalSurveys(int page, Integer pageSize);
    MappingJacksonValue surveysInWeek(int page, Integer pageSize);
    MappingJacksonValue mySurveysInWeek(String email, Integer page, Integer pageSize);
}
