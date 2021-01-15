package io.springboot.survey.impl;

import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.mapper.SurveyByNameAndCreatorId;
import io.springboot.survey.mapper.SurveyResponseDto;
import io.springboot.survey.models.QuestionModel;
import io.springboot.survey.models.SurveyResponseModel;
import io.springboot.survey.pojo.report.GetAnswerResponseParam;
import io.springboot.survey.repository.*;
import io.springboot.survey.response.GraphListResponse;
import io.springboot.survey.response.QuestionResponse;
import io.springboot.survey.response.SurveyResponse;
import io.springboot.survey.service.ConsolidatedReportService;
import io.springboot.survey.service.SurveyUserResponseService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJacksonValue;

import javax.persistence.Tuple;
import java.text.ParseException;
import java.util.*;

import static io.springboot.survey.utils.Constants.ErrorMessageConstant.SURVEY_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
class SurveySecondServiceImplementationTest {

    @InjectMocks
    SurveySecondServiceImplementation secondServiceImplementation;

    @Mock
    UserRepo userRepo;
    @Mock
    SurveyStatusRepo surveyStatusRepo;
    @Mock
    QuestionRepo questionRepo;
    @Mock
    SurveyResponseRepo surveyResponseRepo;
    @Mock
    QuestionTypeRepo questionTypeRepo;
    @Mock
    SurveyUserResponseService surveyUserResponseService;
    @Mock
    ConsolidatedReportService consolidatedReportService;


    @Test
    @DisplayName("Report Filter Info Test")
    void reportFilterInfo() {
        List<String> response= secondServiceImplementation.reportFilterInfo();
        assertNotNull(response);
    }

    @Test
    @DisplayName("Count Test")
    void getCount() {
        when(userRepo.getSurveyStatusModelByEmailSize(Mockito.anyString())).thenReturn(1);
        when(userRepo.getSurveyStatusModelSize(Mockito.anyString(),Mockito.anyBoolean())).thenReturn(1);
        Map<String, Integer> response= secondServiceImplementation.getCount("dummy@nineleaps.com");
        assertNotNull(response);
    }

    @Nested
    @DisplayName("Get All Response Test")
    class AllResponseTest {
        @Test
        @DisplayName("Not Null Condition")
        void getAllResponse() {
            Tuple tuple = Mockito.mock(Tuple.class);
            SurveyResponseDto surveyResponseDto = Mockito.mock(SurveyResponseDto.class);
            SurveyByNameAndCreatorId survey = Mockito.mock(SurveyByNameAndCreatorId.class);
            QuestionResponse questionResponse = Mockito.mock(QuestionResponse.class);
            when(userRepo.getSurveyDataByNameAndId(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
                    .thenReturn(survey);
            when(surveyStatusRepo.getUserIdByIdAndTaken(Mockito.anyInt(), Mockito.anyBoolean()))
                    .thenReturn(new HashSet<>(Collections.singletonList(1)));
            when(questionRepo.getQuestIdBySurveyId(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(23)));
            when(surveyResponseRepo.getResponseIdAndTeamId(Mockito.anyInt(), Mockito.anyInt()))
                    .thenReturn(new ArrayList<>(Collections.singleton(surveyResponseDto)));
            when(userRepo.getUserNameAndUserEmail(Mockito.anyInt())).thenReturn(tuple);
            when(surveyUserResponseService.getUserResponse(Mockito.anyList(), Mockito.anyInt(), Mockito.anyInt()))
                    .thenReturn(new ArrayList<>(Collections.singleton(questionResponse)));
            MappingJacksonValue response1 = secondServiceImplementation.getAllResponse("testSurvey", "dummy@nineleaps.com");
            SurveyResponseDto surveyResponseDto1 = new SurveyResponseDto() {
                @Override
                public int getResponseId()
                { return 0; }
                @Override
                public Integer getTeamId()
                { return -1; }
            };
            when(surveyResponseRepo.getResponseIdAndTeamId(Mockito.anyInt(), Mockito.anyInt()))
                    .thenReturn(new ArrayList<>(Collections.singleton(surveyResponseDto1)));
            MappingJacksonValue response2 = secondServiceImplementation.getAllResponse("testSurvey", "dummy@nineleaps.com");
            assertAll(() -> assertNotNull(response1),
                    () -> assertNotNull(response2));
        }

        @Test
        @DisplayName("Null Condition")
        void nullCondition() {
            when(userRepo.getSurveyDataByNameAndId(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
                    .thenReturn(null);
            Exception exception=assertThrows(ResourceNotFoundException.class,
                    ()->secondServiceImplementation.getAllResponse("testSurvey", "dummy@nineleaps.com"));
            assertEquals(SURVEY_NOT_FOUND,exception.getMessage());
        }
    }
    @Nested
    @DisplayName("All Answers Test")
    class AllAnswerTest {

        @BeforeEach
        void init()
        {
            SurveyByNameAndCreatorId survey = Mockito.mock(SurveyByNameAndCreatorId.class);
            QuestionModel questionModel = Mockito.mock(QuestionModel.class);
            when(userRepo.getSurveyDataByNameAndId(null, null, false)).thenReturn(survey);
            when(questionRepo.findAllBySurveyId(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(questionModel)));
        }

        @Test
        @DisplayName("Radio|Checkbox|Rating")
        void getAllAnswers() throws ParseException {
            SurveyResponse surveyResponse = Mockito.mock(SurveyResponse.class);
            GraphListResponse graphListResponse = Mockito.mock(GraphListResponse.class);
            when(questionTypeRepo.getQuestTypeNameByQuestion(null, 0)).thenReturn("radio");
            List<Integer> answerId = new ArrayList<>(Collections.singleton(1));
            when(consolidatedReportService.getAnswersId(Mockito.any(), Mockito.anyInt())).thenReturn(answerId);
            when(consolidatedReportService.reportFilter(Mockito.any(), Mockito.anyInt())).thenReturn(graphListResponse);
            when(questionRepo.getMandatoryByQuesTextAndSurveyId(null, 0)).thenReturn(true);
            Object response1 = secondServiceImplementation.surveyReport(surveyResponse);
            GraphListResponse graphListResponse1 = new GraphListResponse();
            SurveyResponseModel surveyResponseModel = Mockito.mock(SurveyResponseModel.class);
            List<SurveyResponseModel> modelList = new ArrayList<>(Collections.singleton(surveyResponseModel));
            List<Integer> responseId = new ArrayList<>(Collections.singleton(1));
            graphListResponse1.setSurveyList(modelList);
            graphListResponse1.setResponseId(responseId);
            when(consolidatedReportService.reportFilter(Mockito.any(), Mockito.anyInt())).thenReturn(graphListResponse1);
            when(consolidatedReportService.getAnswerResponse(Mockito.any(GetAnswerResponseParam.class))).thenReturn(1);
            Object response2 = secondServiceImplementation.surveyReport(surveyResponse);
            assertAll(() -> assertNotNull(response1),
                    () -> assertNotNull(response2));
        }
        @Test
        @DisplayName("File|Text")
        void fileAndText() throws ParseException {
            SurveyResponse surveyResponse=Mockito.mock(SurveyResponse.class);
             when(questionTypeRepo.getQuestTypeNameByQuestion(null, 0)).thenReturn("file");
            Object response1 = secondServiceImplementation.surveyReport(surveyResponse);
            when(questionTypeRepo.getQuestTypeNameByQuestion(null, 0)).thenReturn("text");
            Object response2 = secondServiceImplementation.surveyReport(surveyResponse);
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));
        }
    }
}