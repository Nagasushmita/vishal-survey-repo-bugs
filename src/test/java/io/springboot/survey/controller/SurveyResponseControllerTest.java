package io.springboot.survey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.response.SurveyResponse;
import io.springboot.survey.service.SurveyReportService;
import io.springboot.survey.service.SurveySecondService;
import io.springboot.survey.utils.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static io.springboot.survey.utils.Constants.ValidationConstant.MOCK_TOKEN;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SurveyResponseController.class)
@Tag("Controller")
class SurveyResponseControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    AuthorizationService authorizationService;
    @MockBean
    UserRepo userRepo;
    @MockBean
    SurveySecondService surveySecondService;
    @MockBean
    SurveyReportService surveyReportService;

    @BeforeEach
    void setUp() {
        UserModel userModel = Mockito.mock(UserModel.class);
        when(authorizationService.showEndpointsAction(Mockito.anyString())).thenReturn(true);
        when(authorizationService.authorizationManager(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
        when(userRepo.findByUserEmail(Mockito.anyString())).thenReturn(userModel);
    }

    @Test
    @DisplayName("Find All Answers Test")
    void findAnswers() throws Exception {
        SurveyResponse surveyResponse=new SurveyResponse();
        surveyResponse.setSurveyName("dummySurvey");
        mvc.perform(post("/surveyManagement/v1/io.springboot.survey/report")
                .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                .content(objectMapper.writeValueAsString(surveyResponse))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        verify(surveySecondService,times(1)).surveyReport(Mockito.any());

    }

    @Test
    void reportFilterInfo() throws Exception {
        mvc.perform(get("/surveyManagement/v1/template/report/filter")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(surveySecondService,times(1)).reportFilterInfo();
    }

    @Test
    void reportSwitch() throws Exception {
        SurveyResponse surveyResponse=new SurveyResponse();
        surveyResponse.setSurveyName("dummySurvey");
        mvc.perform(post("/surveyManagement/v1/template-report")
                .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                .content(objectMapper.writeValueAsString(surveyResponse))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        verify(surveyReportService,times(1)).reportSwitch(Mockito.any());

    }
}