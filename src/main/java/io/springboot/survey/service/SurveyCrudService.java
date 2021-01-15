package io.springboot.survey.service;

import io.springboot.survey.request.CreateSurveyRequest;
import io.springboot.survey.request.DeleteArchiveRequest;
import io.springboot.survey.response.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface SurveyCrudService {
    ResponseEntity<ResponseMessage> createSurvey(CreateSurveyRequest createSurveyRequest);
    ResponseEntity<ResponseMessage> archiveSurvey(DeleteArchiveRequest archiveResponse);
    ResponseEntity<ResponseMessage> unarchiveSurvey(DeleteArchiveRequest archiveResponse);
    ResponseEntity<Void> deleteSurvey(DeleteArchiveRequest deleteResponse);
}
