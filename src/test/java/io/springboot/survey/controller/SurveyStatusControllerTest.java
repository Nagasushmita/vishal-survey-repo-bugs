package io.springboot.survey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.request.UserRequest;
import io.springboot.survey.response.AssigneeInformationResponse;
import io.springboot.survey.response.QuestionResponse;
import io.springboot.survey.service.SurveyResponseService;
import io.springboot.survey.service.SurveySecondService;
import io.springboot.survey.service.SurveyStatusService;
import io.springboot.survey.utils.AuthorizationService;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;

import static io.springboot.survey.utils.Constants.ValidationConstant.MOCK_TOKEN;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SurveyStatusController.class)
@Tag("Controller")
class SurveyStatusControllerTest {


    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    SurveySecondService surveySecondService;
    @MockBean
    SurveyResponseService surveyResponseService;
    @MockBean
    SurveyStatusService surveyStatusService;
    @MockBean
    UserRepo userRepo;
    @MockBean
    AuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        UserModel userModel = Mockito.mock(UserModel.class);
        when(authorizationService.showEndpointsAction(Mockito.anyString())).thenReturn(true);
        when(authorizationService.authorizationManager(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
        when(userRepo.findByUserEmail(Mockito.anyString())).thenReturn(userModel);
    }


    @Nested
    @DisplayName("Get Count Test")
    class GetCountTest {
        @Test
        @DisplayName("Success - 200")
        void getCount() throws Exception {
            mvc.perform((get("/surveyManagement/v1/user/dashboard")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email", "vishal.jha@nineleaps.com")))
                    .andExpect(status().isOk());
            Mockito.verify(surveySecondService, times(1)).getCount("vishal.jha@nineleaps.com");
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/user/dashboard")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email", "")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Survey Taken Test")
    class SurveyTakenTest {
        @Test
        @DisplayName("Success - 200")
        void surveyTaken() throws Exception {
            mvc.perform((get("/surveyManagement/v1/user/surveys/taken")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                .param("email", "vishal.jha@nineleaps.com"))
                .param("page","0"))
                .andExpect(status().isOk());
            Mockito.verify(surveyStatusService, times(1)).getSurveyTakenInfo(Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt());
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/user/surveys/taken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email", ""))
                    .param("page",""))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Survey Pending Test")
    class SurveyPendingTest {
        @Test
        @DisplayName("Success - 200")
        void surveyPending() throws Exception {
            mvc.perform((get("/surveyManagement/v1/user/surveys/pending")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email", "vishal.jha@nineleaps.com"))
                    .param("page","0"))
                    .andExpect(status().isOk());
            Mockito.verify(surveyStatusService, times(1)).getSurveyPendingInfo(Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt());
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/user/surveys/pending")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email", ""))
                    .param("page",""))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Active Survey Test")
    class ActiveSurvey {
        @Test
        @DisplayName("Success - 200")
        void activeSurvey() throws Exception {
            mvc.perform((get("/surveyManagement/v1/user/surveys/active")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email", "vishal.jha@nineleaps.com")))
                    .andExpect(status().isOk());
            Mockito.verify(surveyStatusService, times(1)).getActiveSurvey(Mockito.anyString());
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/user/surveys/active")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email", "")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Get Survey Info Test ")
    class GetSurveyInfoTest {
        @Test
        @DisplayName("Success - 200")
        void getSurveyInfo() throws Exception {
            mvc.perform((get("/surveyManagement/v1/surveys/info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("creatorEmail", "vishal.jha@nineleaps.com")
                    .param("page", "0")))
                    .andExpect(status().isOk());
            Mockito.verify(surveyStatusService,
                    times(1))
                    .getSurveyInfo(Mockito.any());
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/surveys/info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("creatorEmail", "")
                    .param("page", "")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }
    @Nested
    @DisplayName("Survey Info Pending Test")
    class GetSurveyInfoPendingTest {
        @Test
        @DisplayName("Success - 200")
        void getSurveyInfoPending() throws Exception {
            mvc.perform((get("/surveyManagement/v1/io.springboot.survey/pending/info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("surveyName","testSurvey")
                    .param("creatorEmail", "vishal.jha@nineleaps.com")
                    .param("page", "0")))
                    .andExpect(status().isOk());

        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/io.springboot.survey/pending/info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("surveyName","")
                    .param("creatorEmail", "")
                    .param("page", "")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Get Survey Info Taken")
    class GetSurveyInfoTaken {
        @Test
        @DisplayName("Success - 200")
        void getSurveyInfoTaken() throws Exception {
            mvc.perform((get("/surveyManagement/v1/io.springboot.survey/taken/info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("surveyName","testSurvey")
                    .param("creatorEmail", "vishal.jha@nineleaps.com")
                    .param("page", "0")))
                    .andExpect(status().isOk());

        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/io.springboot.survey/taken/info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("surveyName","")
                    .param("creatorEmail", "")
                    .param("page", "")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Survey Info Assigned Test")
    class SurveyInfoAssignedTest {
        @Test
        @DisplayName("Success - 200")
        void getSurveyInfoAssigned() throws Exception {
            mvc.perform((get("/surveyManagement/v1/io.springboot.survey/assigned/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("surveyName","testSurvey")
                    .param("creatorEmail", "vishal.jha@nineleaps.com")
                    .param("page", "0")))
                    .andExpect(status().isOk());
                }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/io.springboot.survey/assigned/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("surveyName","")
                    .param("creatorEmail", "")
                    .param("page", "")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }
    @Nested
    @DisplayName("User Response Test")
    class UserResponseTest {
        @Test
        @DisplayName("Success - 200")
        void getUserResponse() throws Exception {
            QuestionResponse questionResponse=Mockito.mock(QuestionResponse.class);
            UserRequest userRequest=getUserRequest();
            when(surveyResponseService.getUserResponse(userRequest)).thenReturn(new ArrayList<>(Collections.singletonList(questionResponse)));
            mvc.perform((post("/surveyManagement/v1/user/response")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(userRequest))
                    .accept(MediaType.APPLICATION_JSON)))
                    .andExpect(status().isOk());

        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            UserRequest userRequest=new UserRequest();
            mvc.perform((post("/surveyManagement/v1/user/response")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(userRequest))
                    .accept(MediaType.APPLICATION_JSON)))
                    .andExpect(status().isUnprocessableEntity());
            }
    }
    @Nested
   @DisplayName("All Response Test")
    class AllResponseTest {
        @Test
        @DisplayName("Success - 200")
        void getAllResponse() throws Exception {
            mvc.perform((get("/surveyManagement/v1/io.springboot.survey/responses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("surveyName","testSurvey")
                    .param("creatorEmail", "vishal.jha@nineleaps.com")))
                    .andExpect(status().isOk());
            Mockito.verify(surveySecondService,
                    times(1))
                    .getAllResponse(Mockito.anyString(),Mockito.anyString());
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/io.springboot.survey/responses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("surveyName","")
                    .param("creatorEmail", "")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    class SurveyAssigneeInfoTest {
        @Test
        @DisplayName("Success - 200")
        void surveyAssigneeInfo() throws Exception {
        AssigneeInformationResponse response=new AssigneeInformationResponse();
        response.setRole("dummyRole");
        response.setPendingCount(1);
        response.setTakenCount(1);
        response.setTotalCount(2);
        when(surveyStatusService.surveyAssigneeInfo(Mockito.anyString())).thenReturn(new ArrayList<>(Collections.singleton(response)));
            mvc.perform((get("/surveyManagement/v1/user/assignee-info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email", "vishal.jha@nineleaps.com")))
                    .andExpect(status().isOk());
            Mockito.verify(surveyStatusService,
                    times(1))
                    .surveyAssigneeInfo(Mockito.anyString());
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/user/assignee-info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email", "")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }


    @Nested
    @DisplayName("Total Assigned Survey Test")
    class TotalAssignedSurveyTest {
        @Test
        @DisplayName("Success - 200")
        void totalAssignedSurveys() throws Exception {
            mvc.perform((get("/surveyManagement/v1/user/surveys/assigned")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email", "vishal.jha@nineleaps.com")
                    .param("page","0")))
                    .andExpect(status().isOk());
            Mockito.verify(surveyStatusService,
                    times(1))
                    .totalAssignedSurveys(Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt());
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/user/surveys/assigned")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email", "")
                    .param("page","")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Survey Information Test")
    class SurveyInformationTest {
        @Test
        @DisplayName("Success - 200")
        void surveyInformation() throws Exception {
            mvc.perform((get("/surveyManagement/v1/io.springboot.survey/info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("creatorEmail", "vishal.jha@nineleaps.com")
                    .param("surveyName","dummySurvey")))
                    .andExpect(status().isOk());
            Mockito.verify(surveyStatusService,
                    times(1))
                    .surveyInformation(Mockito.anyString(),Mockito.anyString());

        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/io.springboot.survey/info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("creatorEmail", "")
                    .param("surveyName","")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }
    private UserRequest getUserRequest()
    {
        UserRequest userRequest=new UserRequest();
        userRequest.setTeamName("testTeam");
        userRequest.setEmail("test@nineleaps.com");
        userRequest.setCreatorEmail("dummy@nineleaps.com");
        userRequest.setSurveyName("testSurvey");
        return userRequest;
    }
}