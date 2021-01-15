package io.springboot.survey.impl;

import io.springboot.survey.models.SurveyResponseModel;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.*;
import io.springboot.survey.response.SurveyResponse;
import io.springboot.survey.utils.SpecificationModel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
class ConsolidatedReportServiceImplTest {

    @InjectMocks
    ConsolidatedReportServiceImpl consolidatedReportService;
    @Mock
    UserRepo userRepo;
    @Mock
    QuestionTypeRepo questionTypeRepo;
    @Mock
    SurveyResponseRepo surveyResponseRepo;
    @Mock
    SurveyRepo surveyRepo;
    @Mock
    QuestionRepo questionRepo;
    @Mock
    ResponseRepo responseRepo;
    @Mock
    SpecificationModel specificationModel;


    @Nested
    @DisplayName("Consolidated Report Test")
    class ConsolidatedReportTest {
        @Test
        @DisplayName("File|Text Question Type")
        void fileTextQuestType() {
            SurveyResponse surveyResponse = Mockito.mock(SurveyResponse.class);
            when(userRepo.getSurveyIdByName(null)).thenReturn(new ArrayList<>(Collections.singletonList(1)));
            when(userRepo.getTemplateIdByName(null)).thenReturn(1);
            when(userRepo.findAllByTemplateId(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton("hello")));
            when(questionTypeRepo.getQuestTypeNameByTemplateQuestion(Mockito.anyString(), Mockito.anyInt())).thenReturn("file");
            Object response1 = consolidatedReportService.consolidatedReports(surveyResponse);
            when(questionTypeRepo.getQuestTypeNameByTemplateQuestion(Mockito.anyString(), Mockito.anyInt())).thenReturn("text");
            Object response2 = consolidatedReportService.consolidatedReports(surveyResponse);
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));
        }
    }

    @Test
    @DisplayName("Default Filter")
    void defaultFilter() {
        when(userRepo.getSurveyIdByName(null)).thenReturn(new ArrayList<>(Collections.singletonList(1)));
        when(userRepo.getTemplateIdByName(null)).thenReturn(1);
        when(userRepo.findAllByTemplateId(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton("hello")));
        when(questionTypeRepo.getQuestTypeNameByTemplateQuestion(Mockito.anyString(), Mockito.anyInt())).thenReturn("radio");
        SurveyResponse surveyResponse=new SurveyResponse();
        surveyResponse.setFilter("default");
        surveyResponse.setDesignation(new ArrayList<>());
        Exception exception=assertThrows(IllegalArgumentException.class,()->consolidatedReportService.consolidatedReports(surveyResponse));
        assertNotNull(exception);
    }

    @Nested
    @DisplayName("Consolidated Report Test Filter")
    class ConsolidatedReportFilterTest{
        @BeforeEach
        void init()
        {
            when(userRepo.getSurveyIdByName(null)).thenReturn(new ArrayList<>(Collections.singletonList(1)));
            when(userRepo.getTemplateIdByName(null)).thenReturn(1);
            when(userRepo.findAllByTemplateId(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton("hello")));
            when(questionTypeRepo.getQuestTypeNameByTemplateQuestion(Mockito.anyString(), Mockito.anyInt())).thenReturn("radio");
            Tuple tuple=Mockito.mock(Tuple.class);
            when(questionTypeRepo.getQuestTypeNameByQuestion(Mockito.anyString(),Mockito.anyInt())).thenReturn("radio");
            when(questionRepo.getAnsByQuesTextAndSurveyId(Mockito.anyString(),Mockito.anyInt()))
                    .thenReturn(new ArrayList<>(Collections.singleton(1)));
            when(questionRepo.findByAnsId(Mockito.anyInt())).thenReturn("test");
            when(userRepo.getUserNameAndUserEmail(Mockito.anyInt())).thenReturn(tuple);
        }

        @Test
        @DisplayName("Day Filter")
        void dayFilter() {
            SurveyResponseModel surveyResponseModel=Mockito.mock(SurveyResponseModel.class);
            SurveyResponse surveyResponse=new SurveyResponse();
            surveyResponse.setFilter("Day(s)");
            surveyResponse.setNumber(5);
            surveyResponse.setDesignation(new ArrayList<>());
            when(userRepo.getCreationDateBySurveyId(Mockito.anyInt())).thenReturn(1600167456697L);
            when (surveyResponseRepo.findBySurveyIdAndResponseDateBetween(Mockito.anyInt(),Mockito.anyLong(),Mockito.anyLong()))
                    .thenReturn(new ArrayList<>(Collections.singleton(surveyResponseModel)));
            when(surveyRepo.getCreatorById(Mockito.anyInt())).thenReturn(2);
            when(responseRepo.findByAnswerId(Mockito.anyInt())).thenReturn(1);
            when(responseRepo.findByAnswerIdAndResponseIdIsIn(Mockito.anyInt(), Mockito.anyList())).thenReturn(1);
            Object response1=consolidatedReportService.consolidatedReports(surveyResponse);
            when(userRepo.getCreationDateBySurveyId(Mockito.anyInt())).thenReturn(1600167456697L);
            Object response2=consolidatedReportService.consolidatedReports(surveyResponse);
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));

        }
        @Test
        @DisplayName("week Filter")
        void weekFilter() {
            SurveyResponseModel surveyResponseModel=Mockito.mock(SurveyResponseModel.class);
            SurveyResponse surveyResponse=new SurveyResponse();
            surveyResponse.setFilter("Week(s)");
            surveyResponse.setNumber(5);
            surveyResponse.setDesignation(new ArrayList<>());
            when(userRepo.getCreationDateBySurveyId(Mockito.anyInt())).thenReturn(123L);
            when (surveyResponseRepo.findBySurveyIdAndResponseDateBetween(Mockito.anyInt(),Mockito.anyLong(),Mockito.anyLong()))
                    .thenReturn(new ArrayList<>(Collections.singleton(surveyResponseModel)));
            when(surveyRepo.getCreatorById(Mockito.anyInt())).thenReturn(2);
            when(responseRepo.findByAnswerId(Mockito.anyInt())).thenReturn(1);
            when(responseRepo.findByAnswerIdAndResponseIdIsIn(Mockito.anyInt(), Mockito.anyList())).thenReturn(1);
            Object response=consolidatedReportService.consolidatedReports(surveyResponse);
            assertNotNull(response);
        }
        @Test
        @DisplayName("Month Filter")
        void monthFilter() {
            SurveyResponseModel surveyResponseModel=Mockito.mock(SurveyResponseModel.class);
            SurveyResponse surveyResponse=new SurveyResponse();
            surveyResponse.setFilter("Month(s)");
            surveyResponse.setNumber(5);
            surveyResponse.setDesignation(new ArrayList<>());
            when(userRepo.getCreationDateBySurveyId(Mockito.anyInt())).thenReturn(1600167456697L);
            when (surveyResponseRepo.findBySurveyIdAndResponseDateBetween(Mockito.anyInt(),Mockito.anyLong(),Mockito.anyLong()))
                    .thenReturn(new ArrayList<>(Collections.singleton(surveyResponseModel)));
            when(surveyRepo.getCreatorById(Mockito.anyInt())).thenReturn(2);
            when(responseRepo.findByAnswerId(Mockito.anyInt())).thenReturn(1);
            when(responseRepo.findByAnswerIdAndResponseIdIsIn(Mockito.anyInt(), Mockito.anyList())).thenReturn(1);
            Object response=consolidatedReportService.consolidatedReports(surveyResponse);
            assertNotNull(response);
        }
        @Test
        @DisplayName("Year Filter")
        void yearFilter() {
            SurveyResponseModel surveyResponseModel=Mockito.mock(SurveyResponseModel.class);
            SurveyResponse surveyResponse=new SurveyResponse();
            surveyResponse.setFilter("Year(s)");
            surveyResponse.setNumber(5);
            surveyResponse.setDesignation(new ArrayList<>());
            when(userRepo.getCreationDateBySurveyId(Mockito.anyInt())).thenReturn(1600167456697L);
            when (surveyResponseRepo.findBySurveyIdAndResponseDateBetween(Mockito.anyInt(),Mockito.anyLong(),Mockito.anyLong()))
                    .thenReturn(new ArrayList<>(Collections.singleton(surveyResponseModel)));
            when(surveyRepo.getCreatorById(Mockito.anyInt())).thenReturn(2);
            when(responseRepo.findByAnswerId(Mockito.anyInt())).thenReturn(0);
            Object response=consolidatedReportService.consolidatedReports(surveyResponse);
            assertNotNull(response);
        }

        @Test
        @DisplayName("Designation Filter")
        void designationFilter() {
            UserModel userModel=Mockito.mock(UserModel.class);
            Object [] objects=new Object[4];
            objects[0]=1L;
            objects[1]=1L;
            SurveyResponseModel surveyResponseModel=Mockito.mock(SurveyResponseModel.class);
            SurveyResponse surveyResponse=new SurveyResponse();
            surveyResponse.setDesignation(new ArrayList<>(Collections.singleton("HR")));
            when(specificationModel.getAllByDesignation(null,surveyResponse)).thenReturn(new ArrayList<>(Collections.singleton(userModel)));
            when(surveyResponseRepo.findByUserIdAndSurveyId(Mockito.anyInt(),Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(surveyResponseModel)));
            when(userRepo.getSurveyData(Mockito.anyInt())).thenReturn(objects);
            when(responseRepo.findByAnswerId(Mockito.anyInt())).thenReturn(1);
            Object response1=consolidatedReportService.consolidatedReports(surveyResponse);
            when(surveyResponseRepo.findByUserIdAndSurveyId(Mockito.anyInt(),Mockito.anyInt())).thenReturn(new ArrayList<>());
            Object response2=consolidatedReportService.consolidatedReports(surveyResponse);
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));
        }
        @Test
        @DisplayName("Designation Filter And Duration Filter")
        void bothFilter() {
            UserModel userModel=Mockito.mock(UserModel.class);
            SurveyResponseModel surveyResponseModel=Mockito.mock(SurveyResponseModel.class);
            SurveyResponse surveyResponse=new SurveyResponse();
            surveyResponse.setFilter("Day(s)");
            surveyResponse.setNumber(5);
            surveyResponse.setDesignation(new ArrayList<>(Collections.singleton("HR")));
            when(specificationModel.getAllByDesignation(null,surveyResponse)).thenReturn(new ArrayList<>(Collections.singleton(userModel)));
            when(surveyResponseRepo.findByUserIdAndSurveyId(Mockito.anyInt(),Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(surveyResponseModel)));
            when(userRepo.getCreationDateBySurveyId(Mockito.anyInt())).thenReturn(1600167456697L);
            when (surveyResponseRepo.findBySurveyIdAndResponseDateBetween(Mockito.anyInt(),Mockito.anyLong(),Mockito.anyLong()))
                    .thenReturn(new ArrayList<>(Collections.singleton(surveyResponseModel)));
            when(responseRepo.findByAnswerId(Mockito.anyInt())).thenReturn(1);
            Object response1=consolidatedReportService.consolidatedReports(surveyResponse);
            when (surveyResponseRepo.findBySurveyIdAndResponseDateBetween(Mockito.anyInt(),Mockito.anyLong(),Mockito.anyLong()))
                    .thenReturn(new ArrayList<>());
            Object response2=consolidatedReportService.consolidatedReports(surveyResponse);
            when(surveyResponseRepo.findByUserIdAndSurveyId(Mockito.anyInt(),Mockito.anyInt())).thenReturn(null);
            Object response3=consolidatedReportService.consolidatedReports(surveyResponse);
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2),
                    ()->assertNotNull(response3));
        }

        @Test
        @DisplayName("No Filter")
        void NoFilter() {
            SurveyResponse surveyResponse=new SurveyResponse();
            surveyResponse.setDesignation(new ArrayList<>());
            Object [] objects=new Object[4];
            objects[0]=1L;
            objects[1]=1L;
            when(userRepo.getSurveyData(Mockito.anyInt())).thenReturn(objects);
            when(surveyResponseRepo.findBySurveyId(Mockito.anyInt())).thenReturn(new ArrayList<>());
            Object response=consolidatedReportService.consolidatedReports(surveyResponse);
            assertNotNull(response);
        }
    }

}