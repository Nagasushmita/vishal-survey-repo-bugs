package io.springboot.survey.impl;

import io.springboot.survey.exception.APIException;
import io.springboot.survey.exception.ForbiddenException;
import io.springboot.survey.models.TemplateAnswerModel;
import io.springboot.survey.models.TemplateModel;
import io.springboot.survey.models.TemplateQuestionModel;
import io.springboot.survey.repository.*;
import io.springboot.survey.request.CreateTemplateRequest;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.springboot.survey.utils.Constants.CommonConstant.TEMPLATE_NOT_DELETED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
class TemplateServiceCrudImplTest {

    @InjectMocks
    TemplateServiceCrudImpl templateServiceCrud;

    @Mock
    TemplateRepo templateRepo;

    @Mock
    UserRepo userRepo;

    @Mock
    QuestionTypeRepo questionTypeRepo;

    @Mock
    TemplateQuestionRepo templateQuestionRepo;

    @Mock
    TemplateAnswerRepo templateAnswerRepo;


    @Nested
    class CreateTemplateTest {
        @Test
        @DisplayName("Template Model Not Null And Success")
        void createTemplateNotNullSuccess() {
            TemplateAnswerModel templateAnswerModel=Mockito.mock(TemplateAnswerModel.class);
            CreateTemplateRequest createTemplateRequest = new CreateTemplateRequest();
            createTemplateRequest.setTemplateName("testTemplate");
            createTemplateRequest.setEmail("dummy@nineleaps.com");
            SurveyData surveyData1 = new SurveyData();
            surveyData1.setNumberOfOptions(3);
            surveyData1.setQuesType("radio");
            surveyData1.setAnswers(new ArrayList<>(Arrays.asList("Very Informative", "Slightly Informative,Not informative")));
            SurveyData surveyData2 = new SurveyData();
            surveyData2.setNumberOfOptions(5);
            surveyData2.setQuesType("rating");
            SurveyData surveyData3=new SurveyData();
            surveyData3.setQuesType("default");
            List<SurveyData> surveyDataList = new ArrayList<>(Arrays.asList(surveyData1, surveyData2,surveyData3));
            createTemplateRequest.setSurveyDataList(surveyDataList);
            TemplateModel templateModel = Mockito.mock(TemplateModel.class);
            TemplateQuestionModel templateQuestionModel = Mockito.mock(TemplateQuestionModel.class);
            when(userRepo.getTemplateIdByName(Mockito.anyString())).thenReturn(null);
            when(userRepo.getUserIdByUserEmail(Mockito.anyString())).thenReturn(234);
            when(questionTypeRepo.getIdByQuestionName(Mockito.anyString())).thenReturn(1);
            when(templateAnswerRepo.save(Mockito.any())).thenReturn(templateAnswerModel);
            when(templateRepo.save(Mockito.any(TemplateModel.class))).thenReturn(templateModel);
            when(templateQuestionRepo.save(Mockito.any(TemplateQuestionModel.class))).thenReturn(templateQuestionModel);
            ResponseEntity<ResponseMessage> response = templateServiceCrud.createTemplate(createTemplateRequest);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }

        @Test
        @DisplayName("Template Model Not Null And Failure")
        void createTemplateNotNullFailure() {
            CreateTemplateRequest createTemplateRequest = new CreateTemplateRequest();
            createTemplateRequest.setTemplateName("testTemplate");
            when(userRepo.getTemplateIdByName(Mockito.anyString())).thenReturn(null);
            Exception exception = assertThrows(APIException.class, () -> templateServiceCrud.createTemplate(createTemplateRequest));
            assertNotNull(exception);
        }

        @Test
        @DisplayName("Template Model Null")
        void createTemplateNull() {
            CreateTemplateRequest createTemplateRequest = new CreateTemplateRequest();
            createTemplateRequest.setTemplateName("testTemplate");
            when(userRepo.getTemplateIdByName(Mockito.anyString())).thenReturn(1);
            ResponseEntity<ResponseMessage> response = templateServiceCrud.createTemplate(createTemplateRequest);
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        }
    }


    @Test
    @DisplayName("Archive Template Test")
    void archiveTemplate() {
        getTemplateByNameData();
        ResponseEntity<ResponseMessage> response = templateServiceCrud.archiveTemplate("testTemplate");
        when(templateRepo.findByTemplateName(Mockito.anyString())).thenReturn(null);
        Exception exception = assertThrows(APIException.class,
                () -> templateServiceCrud.archiveTemplate("testTemplate"));
        assertAll(() -> assertNotNull(response),
                () -> assertNotNull(exception));

    }

    @Test
    @DisplayName("Unarchive Template")
    void unarchiveTemplate() {
        getTemplateByNameData();
        ResponseMessage response = templateServiceCrud.unarchiveTemplate("testTemplate");
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }
    @Nested
    class DeleteTemplateTest {
        @Test
        @DisplayName("Forbidden")
        void deleteTemplateForbidden() {
            when(userRepo.getSurveyIdByName(Mockito.anyString())).thenReturn(new ArrayList<>(Collections.singleton(1)));
            Exception exception=assertThrows(ForbiddenException.class,()->templateServiceCrud.deleteTemplates("testTemplate"));
            assertEquals(TEMPLATE_NOT_DELETED,exception.getMessage());
        }
        @Test
        @DisplayName("Success")
        void deleteTemplateSuccess() {
            long i = 1;
            when(userRepo.getSurveyIdByName("testTemplate")).thenReturn(new ArrayList<>());
            when(templateRepo.deleteTemplateModelByTemplateName(Mockito.anyString())).thenReturn(i);
            ResponseEntity<Void> response=templateServiceCrud.deleteTemplates("testTemplate");
            assertEquals(HttpStatus.NO_CONTENT,response.getStatusCode());
        }

        @Test
        @DisplayName("Exception")
        void exception() {
            when(userRepo.getSurveyIdByName("testTemplate")).thenReturn(new ArrayList<>());
            when(templateRepo.deleteTemplateModelByTemplateName(Mockito.anyString())).thenThrow(new RuntimeException());
            Exception exception=assertThrows(Exception.class,()->templateServiceCrud.deleteTemplates("testTemplate"));
            assertNotNull(exception);
        }
    }

    private void getTemplateByNameData() {
        TemplateModel templateModel = new TemplateModel();
        templateModel.setTemplateName("testTemplate");
        templateModel.setTemplateId(223);
        when(templateRepo.findByTemplateName("testTemplate")).thenReturn(templateModel);
    }


}