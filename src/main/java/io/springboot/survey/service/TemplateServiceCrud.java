package io.springboot.survey.service;

import io.springboot.survey.request.CreateTemplateRequest;
import io.springboot.survey.response.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface TemplateServiceCrud {
    ResponseEntity<ResponseMessage> createTemplate(CreateTemplateRequest createTemplateRequest);
    ResponseEntity<ResponseMessage> archiveTemplate(String templateName);
    ResponseMessage unarchiveTemplate(String templateName);
   ResponseEntity<Void> deleteTemplates(String templateName);
}
