package io.springboot.survey.impl;

import io.springboot.survey.exception.APIException;
import io.springboot.survey.mapper.SurveyByNameAndCreatorId;
import io.springboot.survey.models.AnswerModel;
import io.springboot.survey.models.QuestionModel;
import io.springboot.survey.models.SurveyModel;
import io.springboot.survey.repository.*;
import io.springboot.survey.request.CreateSurveyRequest;
import io.springboot.survey.request.DeleteArchiveRequest;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.response.SurveyData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
class SurveyCrudServiceImplTest {


    @InjectMocks
    SurveyCrudServiceImpl surveyCrud;
    @Mock
    UserRepo userRepo;
    @Mock
    QuestionTypeRepo questionTypeRepo;
    @Mock
    SurveyRepo surveyRepo;
    @Mock
    QuestionRepo questionRepo;
    @Mock
    AnswerRepo answerRepo;
    @Mock
    SurveyStatusRepo surveyStatusRepo;


    @Nested
    class CreateSurveyTest {
        @Test
        @DisplayName("Survey Model Not Empty")
        void createSurveyNotEmpty() {
            CreateSurveyRequest createSurveyRequest = new CreateSurveyRequest();
            createSurveyRequest.setSurveyName("testSurvey");
            createSurveyRequest.setCreatorEmail("dummy@nineleaps.com");
            SurveyData surveyData1 = new SurveyData();
            surveyData1.setQuestion("Was the workshop informative?");
            surveyData1.setNumberOfOptions(3);
            surveyData1.setQuesType("radio");
            surveyData1.setAnswers(new ArrayList<>(Arrays.asList("Very","Slightly,Not")));
            SurveyData surveyData2 = new SurveyData();
            surveyData2.setQuestion("rating");
            surveyData2.setNumberOfOptions(5);
            surveyData2.setQuesType("rating");
            SurveyData surveyData3 = new SurveyData();
            surveyData3.setQuesType("default");
            surveyData3.setQuestion("default");
            List<SurveyData> surveyDataList = new ArrayList<>(Arrays.asList(surveyData1, surveyData2,surveyData3));
            createSurveyRequest.setSurveyDataList(surveyDataList);
            SurveyModel surveyModel=Mockito.mock(SurveyModel.class);
            QuestionModel questionModel = Mockito.mock(QuestionModel.class);
            when(questionTypeRepo.getIdByQuestionName(Mockito.anyString())).thenReturn(1);
            when(surveyRepo.save(Mockito.any())).thenReturn(surveyModel);
            when(questionRepo.save(Mockito.any())).thenReturn(questionModel);
            when(answerRepo.save(Mockito.any())).thenReturn(new AnswerModel());
            when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(234);
            ResponseEntity<ResponseMessage> response1= surveyCrud.createSurvey(createSurveyRequest);
            createSurveyRequest.setTemplateName("testTemplate");
            when(userRepo.getTemplateIdByName(Mockito.anyString())).thenReturn(223);
            ResponseEntity<ResponseMessage> response2= surveyCrud.createSurvey(createSurveyRequest);
            assertAll(() -> assertNotNull(response1),
                    ()->assertNotNull(response2),
                    ()->assertEquals(HttpStatus.CREATED,response1.getStatusCode()),
                    ()->assertEquals(HttpStatus.CREATED,response2.getStatusCode()));
        }
        @Test
        @DisplayName("Survey Model Empty And Exception")
        void createSurvey() {
            SurveyByNameAndCreatorId survey=Mockito.mock(SurveyByNameAndCreatorId.class);
            CreateSurveyRequest createSurveyRequest=new CreateSurveyRequest();
            createSurveyRequest.setCreatorEmail("dummy@nineleaps.com");
            createSurveyRequest.setSurveyName("testSurvey");
            when(userRepo.getSurveyDataByNameAndId("testSurvey","dummy@nineleaps.com",false))
                    .thenReturn(survey);
            ResponseEntity<ResponseMessage> response=surveyCrud.createSurvey(createSurveyRequest);
            CreateSurveyRequest createSurveyRequest1=new CreateSurveyRequest();
            Exception exception=assertThrows(APIException.class,()->surveyCrud.createSurvey(createSurveyRequest1));
            assertAll(()-> assertEquals(HttpStatus.CONFLICT,response.getStatusCode()),
                    ()->  assertNotNull(exception));
        }
    }

    @Nested
    class ArchiveSurvey {
        @Test
        @DisplayName("Survey Model Not Empty")
        void archiveSurveyNotEmpty() {
            Date date = new Date();
            DeleteArchiveRequest deleteArchiveRequest=new DeleteArchiveRequest();
            deleteArchiveRequest.setSurveyName("testSurvey");
            deleteArchiveRequest.setCreatorEmail("dummy@nineleaps.com");
            deleteArchiveRequest.setCreationDate(System.currentTimeMillis());
            SurveyModel surveyModel=Mockito.mock(SurveyModel.class);
            when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(234);
            when(surveyRepo.findBySurveyNameAndCreatorUserIdAndCreationDateAndArchivedFalse("testSurvey",234,new Timestamp(date.getTime()).getTime())).thenReturn(surveyModel);
            when( surveyStatusRepo.getParKeyByIdAndTaken(0,false)).thenReturn(new ArrayList<>(Collections.singleton(1)));
            ResponseEntity<ResponseMessage> response=surveyCrud.archiveSurvey(deleteArchiveRequest);
            assertAll(()->assertNotNull(response),
                    ()->assertEquals(HttpStatus.OK,response.getStatusCode()));
        }
    }


    @Test
    @DisplayName("Unarchive Survey")
    void unarchiveSurvey() {
        Date date = new Date();
        DeleteArchiveRequest deleteArchiveRequest=new DeleteArchiveRequest();
        deleteArchiveRequest.setSurveyName("testSurvey");
        deleteArchiveRequest.setCreatorEmail("dummy@nineleaps.com");
        deleteArchiveRequest.setCreationDate(System.currentTimeMillis());
        SurveyModel surveyModel=Mockito.mock(SurveyModel.class);
        when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(234);
        when(surveyRepo.findBySurveyNameAndCreatorUserIdAndCreationDateAndArchivedTrue("testSurvey",234,new Timestamp(date.getTime()).getTime())).thenReturn(surveyModel);
        when(userRepo.getTemplateIdByName(Mockito.anyString())).thenReturn(12);
        ResponseEntity<ResponseMessage> response1=surveyCrud.unarchiveSurvey(deleteArchiveRequest);
        when(userRepo.getTemplateIdByName(Mockito.anyString())).thenReturn(null);
        ResponseEntity<ResponseMessage> response2=surveyCrud.unarchiveSurvey(deleteArchiveRequest);
        assertAll(()->assertNotNull(response1),
                ()->assertEquals(HttpStatus.OK,response1.getStatusCode()),
                ()->assertEquals(HttpStatus.OK,response2.getStatusCode()));

    }

    @Test
    @DisplayName("Delete Survey")
    void deleteSurvey() {
        Date date = new Date();
        DeleteArchiveRequest deleteArchiveRequest=new DeleteArchiveRequest();
        deleteArchiveRequest.setSurveyName("testSurvey");
        deleteArchiveRequest.setCreatorEmail("dummy@nineleaps.com");
        deleteArchiveRequest.setCreationDate(System.currentTimeMillis());
        SurveyModel surveyModel=Mockito.mock(SurveyModel.class);
        when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(234);
        when(surveyRepo.findBySurveyNameAndCreatorUserIdAndCreationDateAndArchivedTrue("testSurvey",234,new Timestamp(date.getTime()).getTime())).thenReturn(surveyModel);
        ResponseEntity<Void> response1=surveyCrud.deleteSurvey(deleteArchiveRequest);
        when(surveyRepo.findBySurveyNameAndCreatorUserIdAndCreationDateAndArchivedTrue("testSurvey",234,new Timestamp(date.getTime()).getTime())).thenReturn(Mockito.any());
        ResponseEntity<Void> response2=surveyCrud.deleteSurvey(deleteArchiveRequest);
        DeleteArchiveRequest requestBody=new DeleteArchiveRequest();
        when(userRepo.getUserIdByUserEmail(null)).thenReturn(null);
        Exception exception=assertThrows(APIException.class,
                ()->surveyCrud.deleteSurvey(requestBody));
        assertAll(()->assertNotNull(response1),
                ()->assertEquals(HttpStatus.NO_CONTENT,response1.getStatusCode()),
                ()->assertEquals(HttpStatus.NO_CONTENT,response2.getStatusCode()),
                ()->assertNotNull(exception));
    }

}