package io.springboot.survey.mapper;

public interface SurveyTakenDto {
   String getUserName();
   String getUserEmail();
   String getSurveyName();
   long getCreationDate();
   int getNoOfQuestion();
   long getResponseDate();
}
