package io.springboot.survey.service;

import io.springboot.survey.pojo.template.GetTemplateParam;
import io.springboot.survey.response.GetSurveyResponse;
import io.springboot.survey.response.TemplateCountResponse;
import io.springboot.survey.response.TemplateInformation;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TemplateService {


     List<GetSurveyResponse> getTemplate(String templateName);
     MappingJacksonValue getAllTemplateByUserId(GetTemplateParam getTemplateParam);
     MappingJacksonValue getMyTemplate(GetTemplateParam getTemplateParam);
     MappingJacksonValue getUsedTemplates(Integer page, Integer pageSize, String sortBy);
     MappingJacksonValue getArchivedTemplate(GetTemplateParam getTemplateParam);
     List<TemplateCountResponse> getUnarchivedTemplate(String email);
     TemplateInformation templateInformation(String templateName);





}



