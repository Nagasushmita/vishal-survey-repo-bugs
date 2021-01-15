package io.springboot.survey.impl;

import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.repository.*;
import io.springboot.survey.service.SurveyUserResponseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJacksonValue;

import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.Collections;

import static io.springboot.survey.utils.Constants.ErrorMessageConstant.NO_SURVEY_CREATED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
class TemplateResponseServiceImplTest {

    @InjectMocks
    TemplateResponseServiceImpl templateResponseService;

    @Mock
    SurveyResponseRepo surveyResponseRepo;

    @Mock
    QuestionRepo questionRepo;

    @Mock
    SurveyUserResponseService surveySecondService;

    @Mock
    UserRepo userRepo;

    @Mock
    TeamRepo teamRepo;



    @Nested
    class GetAllTemplateResponse {
        @Test
        @DisplayName("Survey List Empty")
        void getAllTemplateResponseEmpty() {
            when(userRepo.getSurveyIdByName(Mockito.anyString())).thenReturn(new ArrayList<>());
            Exception exception = assertThrows(ResourceNotFoundException.class,
                    () -> templateResponseService.getAllTemplateResponse("testTemplate",true));
            assertAll(() -> assertNotNull(exception),
                    () -> assertEquals(NO_SURVEY_CREATED, exception.getMessage()));
        }
        @Test
        @DisplayName("Survey List Not Empty")
        void getAllTemplateResponseNotEmpty() {
            Object [] obj= new Object[3];
            obj[0]=1;
            obj[1]=2;
            obj[2]=3;
            Tuple tuple=Mockito.mock(Tuple.class);
            when(userRepo.getSurveyIdByName(Mockito.anyString())).thenReturn(new ArrayList<>(Collections.singleton(1)));
            when(surveyResponseRepo.findResponseIdBySurveyId(1)).thenReturn(new ArrayList<>(Collections.singleton(10)));
            when(questionRepo.getQuestIdBySurveyId(3)).thenReturn(new ArrayList<>(Collections.singleton(77)));
            when(surveySecondService.getUserResponse(new ArrayList<>(Collections.singleton(77)),3,10)).thenReturn(Mockito.any());
            when(surveyResponseRepo.getDataByResponseId(10)).thenReturn(obj);
            when( userRepo.getUserNameAndUserEmail(1)).thenReturn(tuple);
            when(teamRepo.getTeamNameByTeamId(2)).thenReturn(Mockito.any());
            MappingJacksonValue response1=templateResponseService.getAllTemplateResponse("testTemplate",true);
            obj[1]=-1;
            when(surveyResponseRepo.getDataByResponseId(10)).thenReturn(obj);
            MappingJacksonValue response2=templateResponseService.getAllTemplateResponse("testTemplate",true);
            Object [] objects=new Object[2];
            objects[0]="teamName";
            when(userRepo.getUserNameAndEmailBySurveyId(3)).thenReturn(objects);
            MappingJacksonValue response3=templateResponseService.getAllTemplateResponse("testTemplate",false);
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2),
                    ()->assertNotNull(response3));
        }
    }


}