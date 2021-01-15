package io.springboot.survey.service;


import io.springboot.survey.exception.CustomRetryException;
import io.springboot.survey.pojo.GetRequestParam;
import io.springboot.survey.response.SurveyResponse;
import io.springboot.survey.response.UploadFileResponse;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public interface SurveyService {

     MappingJacksonValue getAllSurvey(GetRequestParam getRequestParam);
     MappingJacksonValue getArchivedSurvey(GetRequestParam getRequestParam);
     SurveyResponse decodeLink(String link, String teamName);
     @Retryable(value = CustomRetryException.class,maxAttempts =4,backoff = @Backoff(200))
     UploadFileResponse uploadFile(MultipartFile file) throws IOException;
     @Recover
      String retryMechanism(CustomRetryException customRetryException);
     Map<String, Integer> tooltip(String templateName);
}
