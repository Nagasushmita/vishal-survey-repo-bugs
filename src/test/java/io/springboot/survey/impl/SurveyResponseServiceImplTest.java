package io.springboot.survey.impl;

import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.mapper.SurveyByNameAndCreatorId;
import io.springboot.survey.models.*;
import io.springboot.survey.repository.*;
import io.springboot.survey.request.UserRequest;
import io.springboot.survey.request.UserSurveyRequest;
import io.springboot.survey.response.GetSurveyResponse;
import io.springboot.survey.response.QuestionResponse;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.response.SurveyData;
import io.springboot.survey.service.SurveyUserResponseService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.springboot.survey.utils.Constants.ErrorMessageConstant.SURVEY_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
class SurveyResponseServiceImplTest {

    @InjectMocks
    SurveyResponseServiceImpl surveyResponseService;

    @Mock
    UserRepo userRepo;
    @Mock
    SurveyResponseRepo surveyResponseRepo;
    @Mock
    QuestionTypeRepo questionTypeRepo;
    @Mock
    QuestionRepo questionRepo;
    @Mock
    ResponseRepo responseRepo;
    @Mock
    SurveyUserResponseService surveyUserResponseService;
    @Mock
    SurveyRepo surveyRepo;


    @Nested
    @DisplayName("Survey Response Test")
    class SurveyResponse {
        @BeforeEach
        public void init() {
            SurveyByNameAndCreatorId survey = Mockito.mock(SurveyByNameAndCreatorId.class);
            SurveyResponseModel surveyResponseModel = Mockito.mock(SurveyResponseModel.class);
            when(userRepo.getSurveyDataByNameAndId("test", "dummy", false)).thenReturn(survey);
            when(userRepo.getUserIdByUserEmail(Mockito.anyString())).thenReturn(1);
            when(surveyResponseRepo.save(Mockito.any())).thenReturn(surveyResponseModel);
            SurveyStatusModel surveyStatusModel = Mockito.mock(SurveyStatusModel.class);
            when(userRepo.findBySurveyIdAndUserIdAndTeamId(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                    .thenReturn(surveyStatusModel);
        }

        @Test
        @DisplayName("Rating|CheckBox|Rating When Answer Empty")
        void answerListEmpty() {
            SurveyData surveyData = Mockito.mock(SurveyData.class);
            UserSurveyRequest userSurveyRequest = new UserSurveyRequest();
            userSurveyRequest.setSurveyName("test");
            userSurveyRequest.setCreatorEmail("dummy");
            userSurveyRequest.setEmail("dummy");
            userSurveyRequest.setSurveyDataList(new ArrayList<>(Collections.singletonList(surveyData)));
            ResponseModel responseModel = Mockito.mock(ResponseModel.class);
            when(responseRepo.save(Mockito.any())).thenReturn(responseModel);
            when(questionTypeRepo.getQuestTypeNameByQuestion(null, 0)).thenReturn("rating");
            when(questionRepo.getIdBySurveyIdAndQuesText(0, null)).thenReturn(1);
            ResponseEntity<ResponseMessage> response = surveyResponseService.surveyResponse(userSurveyRequest);
            assertAll(() -> assertNotNull(response),
                    () -> assertEquals(HttpStatus.OK, response.getStatusCode()));
        }
        @Test
        @DisplayName("Rating|CheckBox|Rating When Answer Not Empty")
        void answerListNotEmpty() {
            when(questionTypeRepo.getQuestTypeNameByQuestion(null, 0)).thenReturn("rating");
            when(questionRepo.getIdBySurveyIdAndQuesText(0, null)).thenReturn(1);
            ResponseModel responseModel = Mockito.mock(ResponseModel.class);
            when(responseRepo.save(Mockito.any())).thenReturn(responseModel);
            ResponseEntity<ResponseMessage> response = surveyResponseService.surveyResponse(getData());
            assertAll(() -> assertNotNull(response),
                    () -> assertEquals(HttpStatus.OK, response.getStatusCode()));
        }

        @Test
        @DisplayName("Text Question Type")
        void textQuestionType() {
            when(questionRepo.getIdBySurveyIdAndQuesText(0, null)).thenReturn(1);
            when(questionTypeRepo.getQuestTypeNameByQuestion(null, 0)).thenReturn("text");
            ResponseModel responseModel = Mockito.mock(ResponseModel.class);
            when(responseRepo.save(Mockito.any())).thenReturn(responseModel);
            ResponseEntity<ResponseMessage> response = surveyResponseService.surveyResponse(getData());
            assertAll(() -> assertNotNull(response),
                    () -> assertEquals(HttpStatus.OK, response.getStatusCode()));
        }

        @Test
        @DisplayName("File Type Question")
        void textFileType() {
            when(questionRepo.getIdBySurveyIdAndQuesText(0, null)).thenReturn(1);
            when(questionTypeRepo.getQuestTypeNameByQuestion(null, 0)).thenReturn("file");
            ResponseModel responseModel = Mockito.mock(ResponseModel.class);
            when(responseRepo.save(Mockito.any())).thenReturn(responseModel);
            ResponseEntity<ResponseMessage> response = surveyResponseService.surveyResponse(getData());
            assertAll(() -> assertNotNull(response),
                    () -> assertEquals(HttpStatus.OK, response.getStatusCode()));
        }

        @Test
        @DisplayName("Switch Default")
        void defaultTest() {
            when(questionTypeRepo.getQuestTypeNameByQuestion(null, 0)).thenReturn("default");
            ResponseEntity<ResponseMessage> response = surveyResponseService.surveyResponse(getData());
            assertNotNull(response);

        }

        private UserSurveyRequest getData() {
            SurveyData surveyData = new SurveyData();
            surveyData.setAnswers(new ArrayList<>(Collections.singleton("Hello World")));
            UserSurveyRequest userSurveyRequest = new UserSurveyRequest();
            userSurveyRequest.setSurveyName("test");
            userSurveyRequest.setCreatorEmail("dummy");
            userSurveyRequest.setEmail("dummy");
            userSurveyRequest.setTeamName("N/A");
            userSurveyRequest.setSurveyDataList(new ArrayList<>(Collections.singleton(surveyData)));
            return userSurveyRequest;
        }
    }

    @Nested
    @DisplayName("Get Survey Test")
    class GetSurveyTest {
        @BeforeEach
        void init() {
            QuestionModel questionModel = Mockito.mock(QuestionModel.class);
            SurveyByNameAndCreatorId survey = Mockito.mock(SurveyByNameAndCreatorId.class);
            when(userRepo.getSurveyDataByNameAndId(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
                    .thenReturn(survey);
            when(questionRepo.findAllBySurveyId(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(questionModel)));
        }

        @Test
        @DisplayName("Radio|CheckBox Question")
        void radioCheckBoxQuestion() {
            when(questionTypeRepo.getQuestionNameByTypeId(Mockito.anyInt())).thenReturn("check");
            when(questionRepo.getAnsIdByQuesId(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton("Hello World")));
            List<GetSurveyResponse> responses = surveyResponseService.getSurvey("test", "dummy@nineleaps.com");
            assertNotNull(responses);
        }

        @Test
        @DisplayName("File Type Question")
        void fileTypeQuestion() {
            when(questionTypeRepo.getQuestionNameByTypeId(Mockito.anyInt())).thenReturn("file");
            List<GetSurveyResponse> responses = surveyResponseService.getSurvey("test", "dummy@nineleaps.com");
            assertNotNull(responses);
        }
        @Test
        @DisplayName("Text Type Question")
        void textTypeQuestion() {
            when(questionTypeRepo.getQuestionNameByTypeId(Mockito.anyInt())).thenReturn("text");
            List<GetSurveyResponse> responses = surveyResponseService.getSurvey("test", "dummy@nineleaps.com");
            assertNotNull(responses);
        }

        @Test
        @DisplayName("Rating And Default Type Question")
        void ratingTypeQuestion() {
            when(questionTypeRepo.getQuestionNameByTypeId(Mockito.anyInt())).thenReturn("rating");
            List<GetSurveyResponse> response1 = surveyResponseService.getSurvey("test", "dummy@nineleaps.com");
            when(questionTypeRepo.getQuestionNameByTypeId(Mockito.anyInt())).thenReturn("default");
            List<GetSurveyResponse> response2 = surveyResponseService.getSurvey("test", "dummy@nineleaps.com");
            assertAll(() -> assertNotNull(response1),
                    () -> assertNotNull(response2));
        }
    }

    @Test
    @DisplayName("Survey By Link Test")
    void getSurveyByLink() {
        GetSurveyResponse surveyResponse = Mockito.mock(GetSurveyResponse.class);
        List<GetSurveyResponse> list = new ArrayList<>();
        list.add(surveyResponse);
        SurveyResponseServiceImpl surveyResponseService2 = Mockito.spy(surveyResponseService);
        String link = "Rm9vZCBGZWVkYmFjayBTdXJ2ZXkvbWVlbmFrc2hpLmthdXNoaWtAbmluZWxlYXBzLmNvbQ==";
        doReturn(list).when(surveyResponseService2).getSurvey(Mockito.anyString(), Mockito.anyString());
        List<GetSurveyResponse> response = surveyResponseService2.getSurveyByLink(link);
        assertNotNull(response);
    }


    @Nested
    @DisplayName("User Response Test")
    class UserResponseTest {

        @Test
        @DisplayName("Survey Model Not Null")
        void getUserResponse() {
            SurveyModel surveyModel = Mockito.mock(SurveyModel.class);
            Object[] objects = new Object[2];
            objects[0] = 23;
            UserRequest userRequest = Mockito.mock(UserRequest.class);
            QuestionResponse questionResponse = Mockito.mock(QuestionResponse.class);
            List<QuestionResponse> questionResponseList = new ArrayList<>();
            questionResponseList.add(questionResponse);
            when(surveyRepo.findBySurveyNameAndCreatorUserId(null, 1)).thenReturn(surveyModel);
            when(userRepo.getUserIdByUserEmail(null)).thenReturn(1);
            when(surveyRepo.findBySurveyNameAndCreatorUserIdAndArchivedFalse(null, 1)).thenReturn(surveyModel);
            when(userRepo.getTeamId(null)).thenReturn(1);
            when(surveyResponseRepo.getResponseIdAndTimestamp(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                    .thenReturn(objects);
            when(questionRepo.getQuestIdBySurveyId(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(124)));
            when(surveyUserResponseService.getUserResponse(Mockito.anyList(), Mockito.anyInt(), Mockito.anyInt()))
                    .thenReturn(questionResponseList);
            List<QuestionResponse> response1= surveyResponseService.getUserResponse(userRequest);
            when(surveyRepo.findBySurveyNameAndCreatorUserIdAndArchivedFalse(null, 1)).thenReturn(null);
            when(surveyRepo.getSurveyIdAndLink(null,1)).thenReturn(objects);
            UserRequest userRequest2 = new UserRequest();
            userRequest2.setTeamName("N/A");
            List<QuestionResponse> response2 = surveyResponseService.getUserResponse(userRequest2);
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));
        }


        @Test
        @DisplayName("Exception")
        void exception() {
            UserRequest userRequest=Mockito.mock(UserRequest.class);
            when(userRepo.getUserIdByUserEmail(null)).thenReturn(1);
            when(surveyRepo.findBySurveyNameAndCreatorUserId(null, 1)).thenReturn(null);
            Exception exception=assertThrows(ResourceNotFoundException.class,()->surveyResponseService.getUserResponse(userRequest));
            assertEquals(SURVEY_NOT_FOUND,exception.getMessage());

        }
    }



}