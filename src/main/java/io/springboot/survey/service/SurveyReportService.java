package io.springboot.survey.service;
import io.springboot.survey.response.SurveyResponse;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public interface SurveyReportService {

    Object reportSwitch(SurveyResponse surveyResponse) throws ParseException;

    }
