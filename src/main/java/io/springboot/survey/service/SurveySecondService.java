package io.springboot.survey.service;


import io.springboot.survey.response.*;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;


@Service
public interface SurveySecondService {

    Map<String,Integer> getCount(String email);
    MappingJacksonValue getAllResponse(String surveyName, String creatorEmail);
    Object surveyReport(SurveyResponse surveyResponse) throws ParseException;
    List<String>  reportFilterInfo();


}
