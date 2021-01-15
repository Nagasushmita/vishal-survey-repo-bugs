package io.springboot.survey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.service.SurveyDashboardService;
import io.springboot.survey.utils.AuthorizationService;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static io.springboot.survey.utils.Constants.SurveyModuleConstants.SURVEY_CREATED_BY_ME;
import static io.springboot.survey.utils.Constants.SurveyModuleConstants.TOTAL_SURVEY_CREATED;
import static io.springboot.survey.utils.Constants.ValidationConstant.MOCK_TOKEN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(SurveyDashboardController.class)
@Tag("Controller")
class SurveyDashboardControllerTest {


    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    AuthorizationService authorizationService;
    @MockBean
    UserRepo userRepo;
    @MockBean
    SurveyDashboardService dashboardService;

    @BeforeEach
    void setUp() {
        UserModel userModel = Mockito.mock(UserModel.class);
        when(authorizationService.showEndpointsAction(Mockito.anyString())).thenReturn(true);
        when(authorizationService.authorizationManager(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
        when(userRepo.findByUserEmail(Mockito.anyString())).thenReturn(userModel);
    }


    @Nested
    @DisplayName("HR Dashboard Info Test")
    class HrDashboardInfoTest {
        @Test
        @DisplayName("Success - 200")
        void hrDashboardInfo() throws Exception {

            Map<String,Integer> response= new HashMap<>();
            response.put(TOTAL_SURVEY_CREATED,2);
            when(dashboardService.hrDashboardInfo(Mockito.anyString())).thenReturn(response);
        MvcResult mvcResult= mvc.perform((get("/surveyManagement/v1/hrDashboard")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("email","vishal.jha@nineleps.com")))
                    .andExpect(status().isOk())
                      .andReturn();
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertEquals(objectMapper.writeValueAsString(response), actualResponseBody);
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/hrDashboard")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("email","")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("HR Dashboard Graph Test")
    class HrDashboardGraphTest {
        @Test
        @DisplayName("Success - 200")
        void hrDashboardGraph() throws Exception {
            mvc.perform((get("/surveyManagement/v1/hrDashboardGraph")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("email","vishal.jha@nineleaps.com")))
                    .andExpect(status().isOk());
            verify(dashboardService,times(1)).hrDashboardGraph(Mockito.anyString());
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/hrDashboardGraph")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("email","")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Manager Dashboard Test")
    class ManagerTest {
        @Test
        @DisplayName("Success - 200")
        void manager() throws Exception {
            Map<String,Integer> response= new HashMap<>();
            response.put(SURVEY_CREATED_BY_ME,2);
            when(dashboardService.managerDashboard(Mockito.anyString())).thenReturn(response);
            MvcResult mvcResult= mvc.perform((get("/surveyManagement/v1/managerDashboard")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("email","vishal.jha@nineleps.com")))
                    .andExpect(status().isOk())
                    .andReturn();
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertEquals(objectMapper.writeValueAsString(response), actualResponseBody);
        }

        @Test
        @DisplayName("Invalid Data")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/managerDashboard")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("email","")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }


    @Nested
    @DisplayName("Total Survey Test")
   class TotalSurveyTest{
        @Test
        @DisplayName("Success - 200")
        void totalSurveys() throws Exception {
            mvc.perform((get("/surveyManagement/v1/totalSurveys")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("page","0")))
                    .andExpect(status().isOk());
            verify(dashboardService,times(1)).totalSurveys(Mockito.anyInt(),Mockito.anyInt());
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/totalSurveys")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("page","")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Survey In Week Test")
    class SurveyInWeekTest {
        @DisplayName("Success - 200")
        @Test
        void surveysInWeek() throws Exception {
            mvc.perform((get("/surveyManagement/v1/surveysThisWeek")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("page","0")))
                    .andExpect(status().isOk());
            verify(dashboardService,times(1)).surveysInWeek(Mockito.anyInt(),Mockito.anyInt());

        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/surveysThisWeek")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("page","")))
                    .andExpect(status().isUnprocessableEntity());

        }
    }

    @Nested
    @DisplayName("My Survey This Week Test")
    class MySurveySurveyInWeek {
        @Test
        @DisplayName("Success - 200")
        void testSurveysInWeek() throws Exception {
            mvc.perform((get("/surveyManagement/v1/mySurveysThisWeek")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("email","vishal.jha@nineleaps.com")
                    .param("page","0")))
                    .andExpect(status().isOk());
            verify(dashboardService,times(1)).mySurveysInWeek(Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt());
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/mySurveysThisWeek")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("email","")
                    .param("page","")))
                    .andExpect(status().isUnprocessableEntity());

        }
    }

    @Test
    @DisplayName("Resource Not Found Exception Test")
    void resourceNotFound() throws Exception {
        when(dashboardService.hrDashboardGraph(Mockito.anyString())).thenThrow(ResourceNotFoundException.class);
        mvc.perform((get("/surveyManagement/v1/hrDashboardGraph")
                .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                .contentType(MediaType.TEXT_PLAIN)
                .param("email","vishal.jha@nineleaps.com")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void authorizationException() throws Exception {
        when(authorizationService.authorizationManager(Mockito.anyString(),Mockito.anyString())).thenReturn(false);
        mvc.perform((get("/surveyManagement/v1/hrDashboardGraph")
                .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                .contentType(MediaType.TEXT_PLAIN)
                .param("email","vishal.jha@nineleaps.com")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Missing Servlet RequestParameter Exception Test")
    void missingParameterException() throws Exception {
        mvc.perform((get("/surveyManagement/v1/hrDashboardGraph")
                .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Null Pointer Exception Test")
    void nullPointerException() throws Exception {
        when(dashboardService.hrDashboardGraph(Mockito.anyString())).thenThrow(NullPointerException.class);
        mvc.perform((get("/surveyManagement/v1/hrDashboardGraph")
                .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                .contentType(MediaType.TEXT_PLAIN)
                .param("email","vishal.jha@nineleaps.com")))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Type Mismatch Exception Test")
    void typeMistMatchError() throws Exception {
        when(dashboardService.hrDashboardGraph(Mockito.anyString())).thenThrow(TypeMismatchException.class);
        mvc.perform((get("/surveyManagement/v1/hrDashboardGraph")
                .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                .contentType(MediaType.TEXT_PLAIN)
                .param("email","vishal.jha@nineleaps.com")))
                .andExpect(status().isBadRequest());
    }
}
