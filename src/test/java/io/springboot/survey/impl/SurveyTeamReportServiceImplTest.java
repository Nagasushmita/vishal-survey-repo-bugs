package io.springboot.survey.impl;

import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.mapper.SurveyStatusDto;
import io.springboot.survey.models.AnswerModel;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
class SurveyTeamReportServiceImplTest {

    @InjectMocks
    SurveyTeamReportServiceImpl surveyTeamReportService;
    @Mock
    UserRepo userRepo;
    @Mock
    QuestionTypeRepo questionTypeRepo;
    @Mock
    SurveyResponseRepo surveyResponseRepo;
    @Mock
    QuestionRepo questionRepo;
    @Mock
    AnswerRepo answerRepo;
    @Mock
    ResponseRepo responseRepo;
    @Mock
    SpecificationModel specificationModel;


    @Nested
    @DisplayName("Team Report Test")
    class TeamReport {
        @BeforeEach
        void init() {
            when(userRepo.getSurveyIdByName(null)).thenReturn(new ArrayList<>(Collections.singletonList(1)));
            when(userRepo.getTemplateIdByName(null)).thenReturn(1);
            when(userRepo.findAllByTemplateId(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton("hello")));
        }

        @Test
        @DisplayName("File|Text Question Type")
        void fileTextType() {
            SurveyResponse surveyResponse = Mockito.mock(SurveyResponse.class);
            when(questionTypeRepo.getQuestTypeNameByTemplateQuestion(Mockito.anyString(), Mockito.anyInt())).thenReturn("file");
            Object response1 = surveyTeamReportService.teamReport(surveyResponse);
            when(questionTypeRepo.getQuestTypeNameByTemplateQuestion(Mockito.anyString(), Mockito.anyInt())).thenReturn("text");
            Object response2 = surveyTeamReportService.teamReport(surveyResponse);
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
        SurveyStatusDto surveyStatusDto=Mockito.mock(SurveyStatusDto.class);
        when(userRepo.getSurveyById(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(surveyStatusDto)));
        when(questionTypeRepo.getQuestTypeNameByTemplateQuestion(Mockito.anyString(),Mockito.anyInt())).thenReturn("rating");
        when(surveyResponseRepo.getSurveyIdByTeamId(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(1)));
        Object [] objects=new Object[3];
        objects[0]=1L;
        objects[1]=2L;
        when(userRepo.getSurveyData(Mockito.anyInt())).thenReturn(objects);
        SurveyResponse surveyResponse=new SurveyResponse();
        surveyResponse.setFilter("default");
        surveyResponse.setDesignation(new ArrayList<>());
         Exception exception=assertThrows(IllegalStateException.class,()->surveyTeamReportService.teamReport(surveyResponse));
        assertNotNull(exception);

    }

    @Nested
    @DisplayName("Team Report Filter Test")
    class TeamReportFilterTest{
        @BeforeEach
        public void init()
        {
            when(userRepo.getSurveyIdByName(null)).thenReturn(new ArrayList<>(Collections.singletonList(1)));
            when(userRepo.getTemplateIdByName(null)).thenReturn(1);
            when(userRepo.findAllByTemplateId(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton("hello")));
            Object [] objects=new Object[4];
            objects[0]=1L;
            objects[1]=1L;
            SurveyStatusDto surveyStatusDto=Mockito.mock(SurveyStatusDto.class);
            when(userRepo.getSurveyById(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(surveyStatusDto)));
            when(questionTypeRepo.getQuestTypeNameByTemplateQuestion(Mockito.anyString(),Mockito.anyInt())).thenReturn("rating");
            when(surveyResponseRepo.getSurveyIdByTeamId(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(1)));
            when(userRepo.getSurveyData(Mockito.anyInt())).thenReturn(objects);
            AnswerModel answerModel=Mockito.mock(AnswerModel.class);
            when(questionRepo.findByQuesTextAndSurveyId(Mockito.anyString(),Mockito.anyInt())).thenReturn(1);
            when(answerRepo.findByQuesId(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(answerModel)));
            when(questionTypeRepo.getQuestTypeNameByQuestion(Mockito.anyString(),Mockito.anyInt())).thenReturn("radio");
            when(responseRepo.findByAnswerIdIsIn(Mockito.anyList())).thenReturn(1);
            when(responseRepo.findByAnswerIdIsInAndResponseIdIsIn(Mockito.anyList(), Mockito.anyList())).thenReturn(1);
            when(userRepo.getTeamNameByTeamId(Mockito.anyInt())).thenReturn("testTeam");
        }
        @Test
        @DisplayName("Day Filter")
        void dayFilter() {
            SurveyResponseModel surveyResponseModel=Mockito.mock(SurveyResponseModel.class);
            SurveyResponse surveyResponse=new SurveyResponse();
            surveyResponse.setFilter("Day(s)");
            surveyResponse.setNumber(5);
            surveyResponse.setDesignation(new ArrayList<>());
            when(surveyResponseRepo.findBySurveyIdAndTeamIdAndResponseDateBetween(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyLong(),Mockito.anyLong()))
                    .thenReturn(new ArrayList<>(Collections.singleton(surveyResponseModel)));
            when(userRepo.getCreationDateBySurveyId(Mockito.anyInt())).thenReturn(1600167456697L);
            Object response=surveyTeamReportService.teamReport(surveyResponse);
            when(surveyResponseRepo.getSurveyIdByTeamId(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(2)));
            Exception exception=assertThrows(ResourceNotFoundException.class,()->surveyTeamReportService.teamReport(surveyResponse));
            assertAll(()->assertNotNull(response),
                    ()->assertNotNull(exception) );
        }
        @Test
        @DisplayName("Week Filter")
        void weekFilter() {
            SurveyResponseModel surveyResponseModel=Mockito.mock(SurveyResponseModel.class);
            SurveyResponse surveyResponse=new SurveyResponse();
            surveyResponse.setFilter("Week(s)");
            surveyResponse.setNumber(5);
            surveyResponse.setDesignation(new ArrayList<>());
            when(surveyResponseRepo.findBySurveyIdAndTeamIdAndResponseDateBetween(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyLong(),Mockito.anyLong()))
                    .thenReturn(new ArrayList<>(Collections.singleton(surveyResponseModel)));
            when(userRepo.getCreationDateBySurveyId(Mockito.anyInt())).thenReturn(1600167456697L);
            Object response1=surveyTeamReportService.teamReport(surveyResponse);
            Date date = new Date();
            Timestamp endDate = new Timestamp(date.getTime());
            Timestamp timestamp=Timestamp.valueOf(endDate.toLocalDateTime().minusWeeks(6));
            when(userRepo.getCreationDateBySurveyId(Mockito.anyInt())).thenReturn(timestamp.getTime());
            Object response2=surveyTeamReportService.teamReport(surveyResponse);
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));
        }
        @Test
        @DisplayName("Month Filter")
        void monthFilter() {
            SurveyResponseModel surveyResponseModel=Mockito.mock(SurveyResponseModel.class);
            SurveyResponse surveyResponse=new SurveyResponse();
            surveyResponse.setFilter("Month(s)");
            surveyResponse.setNumber(5);
            surveyResponse.setDesignation(new ArrayList<>());
            when(surveyResponseRepo.findBySurveyIdAndTeamIdAndResponseDateBetween(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyLong(),Mockito.anyLong()))
                    .thenReturn(new ArrayList<>(Collections.singleton(surveyResponseModel)));
            when(userRepo.getCreationDateBySurveyId(Mockito.anyInt())).thenReturn(1600167456697L);
            Object response=surveyTeamReportService.teamReport(surveyResponse);
            assertNotNull(response);
        }
        @Test
        @DisplayName("Year Filter")
        void yearFilter() {
            SurveyResponseModel surveyResponseModel=Mockito.mock(SurveyResponseModel.class);
            SurveyResponse surveyResponse=new SurveyResponse();
            surveyResponse.setFilter("Year(s)");
            surveyResponse.setNumber(2);
            surveyResponse.setDesignation(new ArrayList<>());
            when(surveyResponseRepo.findBySurveyIdAndTeamIdAndResponseDateBetween(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyLong(),Mockito.anyLong()))
                    .thenReturn(new ArrayList<>(Collections.singleton(surveyResponseModel)));
            when(userRepo.getCreationDateBySurveyId(Mockito.anyInt())).thenReturn(1600167456697L);
            Object response1=surveyTeamReportService.teamReport(surveyResponse);
            when(surveyResponseRepo.findBySurveyIdAndTeamIdAndResponseDateBetween(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyLong(),Mockito.anyLong()))
                    .thenReturn(new ArrayList<>());
            Object response2=surveyTeamReportService.teamReport(surveyResponse);
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));
        }
        @Test
        @DisplayName("Designation Filter")
        void designationFilter() {
            UserModel userModel=Mockito.mock(UserModel.class);
            SurveyResponseModel surveyResponseModel=Mockito.mock(SurveyResponseModel.class);
            SurveyResponse surveyResponse=new SurveyResponse();
            surveyResponse.setDesignation(new ArrayList<>(Collections.singleton("HR")));
            when(specificationModel.getAllByDesignation(null,surveyResponse)).thenReturn(new ArrayList<>(Collections.singleton(userModel)));
            when(surveyResponseRepo.findByUserIdAndSurveyId(Mockito.anyInt(),Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(surveyResponseModel)));
            Object response1=surveyTeamReportService.teamReport(surveyResponse);
            when(surveyResponseRepo.findByUserIdAndSurveyId(Mockito.anyInt(),Mockito.anyInt())).thenReturn(new ArrayList<>());
            Object response2=surveyTeamReportService.teamReport(surveyResponse);
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));
        }

        @Test
        @DisplayName("Designation Filter And DurationFilter")
        void bothFilter() {
            Date date=new Date();
            UserModel userModel=Mockito.mock(UserModel.class);
            SurveyResponseModel surveyResponseModel=Mockito.mock(SurveyResponseModel.class);
            SurveyResponse surveyResponse=new SurveyResponse();
            surveyResponse.setFilter("Year(s)");
            surveyResponse.setNumber(2);
            surveyResponse.setDesignation(new ArrayList<>(Collections.singleton("HR")));
            when(surveyResponseRepo.findBySurveyIdAndTeamIdAndResponseDateBetween(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyLong(),Mockito.anyLong()))
                    .thenReturn(new ArrayList<>(Collections.singleton(surveyResponseModel)));
            when(userRepo.getCreationDateBySurveyId(Mockito.anyInt())).thenReturn(1600167456697L);
            when(specificationModel.getAllByDesignation(null,surveyResponse)).thenReturn(new ArrayList<>(Collections.singleton(userModel)));
            when(surveyResponseRepo.findByUserIdAndSurveyId(Mockito.anyInt(),Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(surveyResponseModel)));
            Object response1=surveyTeamReportService.teamReport(surveyResponse);
            when(responseRepo.findByAnswerIdIsIn(Mockito.anyList())).thenReturn(0);
            Object response2=surveyTeamReportService.teamReport(surveyResponse);
            when(surveyResponseRepo.findBySurveyIdAndTeamIdAndResponseDateBetween(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyLong(),Mockito.anyLong()))
                    .thenReturn(new ArrayList<>());
            Object response3=surveyTeamReportService.teamReport(surveyResponse);
            when(surveyResponseRepo.findByUserIdAndSurveyId(Mockito.anyInt(),Mockito.anyInt())).thenReturn(new ArrayList<>());
            Object response4=surveyTeamReportService.teamReport(surveyResponse);
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2),
                    ()->assertNotNull(response3),
                    ()->assertNotNull(response4));
        }

        @Test
        @DisplayName("No Filter")
        void noFilter() {
            SurveyResponseModel surveyResponseModel=Mockito.mock(SurveyResponseModel.class);
            SurveyResponse surveyResponse=new SurveyResponse();
            surveyResponse.setDesignation(new ArrayList<>());
            when(surveyResponseRepo.findByTeamIdAndSurveyId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(surveyResponseModel)));
            when(answerRepo.findByAnsText(null)).thenReturn(new ArrayList<>(Collections.singleton(0)));
            Object response1=surveyTeamReportService.teamReport(surveyResponse);
            when(answerRepo.findByAnsText(null)).thenReturn(new ArrayList<>(Collections.singleton(2)));
            Object response2=surveyTeamReportService.teamReport(surveyResponse);
            when(surveyResponseRepo.findByTeamIdAndSurveyId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(new ArrayList<>());
            SurveyStatusDto surveyStatusDto=new SurveyStatusDto() {
                @Override
                public int getUserId()
                { return 0; }
                @Override
                public Integer getTeamId()
                { return -1; }
            };
            when(userRepo.getSurveyById(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(surveyStatusDto)));
            Object response3=surveyTeamReportService.teamReport(surveyResponse);
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2),
                    ()->assertNotNull(response3));
        }


    }

}