package io.springboot.survey.service;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Service;

@Service
public interface TemplateResponseService {
    MappingJacksonValue getAllTemplateResponse(String templateName, boolean team);


}
