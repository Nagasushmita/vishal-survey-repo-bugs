package io.springboot.survey.impl;

import io.springboot.survey.models.UploadFileModel;
import io.springboot.survey.repository.*;
import io.springboot.survey.response.QuestionResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
class   SurveyUserResponseServiceImplTest {

    @InjectMocks
    SurveyUserResponseServiceImpl surveyUserResponseService;
    @Mock
    QuestionRepo questionRepo;
    @Mock
    QuestionTypeRepo questionTypeRepo;
    @Mock
    UserRepo userRepo;
    @Mock
    AnswerRepo answerRepo;
    @Mock
    ResponseRepo responseRepo;
    @Mock
    UploadFileRepo uploadFileRepo;

    @Nested
    @DisplayName("User Response Test")
    class UserResponse {
        @BeforeEach
        void init()
        {
            Tuple tuple= Mockito.mock(Tuple.class);
            Object [] objects=new Object[4];
            when(questionRepo.findByQuesId(Mockito.anyInt())).thenReturn(tuple);
            when(userRepo.getSurveyData(Mockito.anyInt())).thenReturn(objects);
        }
        @Test
        @DisplayName("Radio|Checkbox|Rating Answer List Not Empty")
        void getUserResponse() {
            when(questionTypeRepo.getQuestionNameByTypeId(null)).thenReturn("rating");
            when(answerRepo.getAnswersByResponseModel(Mockito.anyInt(),Mockito.anyInt()))
                    .thenReturn(new ArrayList<>(Collections.singletonList("hello World")));
            List<QuestionResponse> responses=surveyUserResponseService.getUserResponse(new ArrayList<>(Collections.singleton(12)),
                    1,1);
            assertNotNull(responses);
        }
        @Test
        @DisplayName("Radio|Checkbox|Rating Answer List Not Empty")
        void answerListEmpty() {
            when(answerRepo.getAnswersByResponseModel(Mockito.anyInt(),Mockito.anyInt())).thenReturn(new ArrayList<>());
            when(questionTypeRepo.getQuestionNameByTypeId(null)).thenReturn("check");
            List<QuestionResponse> responses=surveyUserResponseService.getUserResponse(new ArrayList<>(Collections.singleton(12)),
                    1,1);
            assertNotNull(responses);
        }
        @Test
        @DisplayName("Text Question Type")
        void textQuestionType() {
            when(questionTypeRepo.getQuestionNameByTypeId(null)).thenReturn("text");
            when(responseRepo.getTextAnswerByResponseIdAndQuesId(Mockito.anyInt(), Mockito.anyInt())).thenReturn("Hello World");
            List<QuestionResponse> responses=surveyUserResponseService.getUserResponse(new ArrayList<>(Collections.singleton(12)),
                    1,1);
            assertNotNull(responses);
        }
        @Test
        @DisplayName("File Question Type")
        void fileQuestionType() {
            UploadFileModel uploadFileModel=Mockito.mock(UploadFileModel.class);
            when(questionTypeRepo.getQuestionNameByTypeId(null)).thenReturn("file");
            when(responseRepo.getFileByResponseIdAndQuesId(Mockito.anyInt(),Mockito.anyInt())).thenReturn("hello");
            when(uploadFileRepo.findByFileId(Mockito.anyString())).thenReturn(uploadFileModel);
            List<QuestionResponse> response1=surveyUserResponseService.getUserResponse(new ArrayList<>(Collections.singleton(12)),
                    1,1);
            when(responseRepo.getFileByResponseIdAndQuesId(Mockito.anyInt(),Mockito.anyInt())).thenReturn("");
            List<QuestionResponse> response2=surveyUserResponseService.getUserResponse(new ArrayList<>(Collections.singleton(12)),
                    1,1);
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));
        }

        @Test
        @DisplayName("Default Question Type")
        void defaultQuestionType() {
            when(questionTypeRepo.getQuestionNameByTypeId(null)).thenReturn("default");
             List<QuestionResponse> responses=surveyUserResponseService.getUserResponse(new ArrayList<>(Collections.singleton(12)),
                    1,1);
            assertNotNull(responses);
        }
    }
}