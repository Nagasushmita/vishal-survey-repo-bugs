package io.springboot.survey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.request.ScheduleEmailRequest;
import io.springboot.survey.response.ListOfMails;
import io.springboot.survey.response.ScheduleEmailResponse;
import io.springboot.survey.service.SchedulingService;
import io.springboot.survey.utils.AuthorizationService;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collections;

import static io.springboot.survey.utils.Constants.SchedulingConstants.EMAIL_SCHEDULED_SUCCESSFULLY;
import static io.springboot.survey.utils.Constants.ValidationConstant.MOCK_TOKEN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(controllers = EmailJobSchedulerController.class)
@Tag("Controller")
class EmailJobSchedulerControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    UserRepo userRepo;
    @MockBean
    AuthorizationService authorizationService;
    @MockBean
    SchedulingService schedulingService;
    @Autowired
    ObjectMapper objectMapper;


    @BeforeEach
    void setUp()
    {
        UserModel userModel = Mockito.mock(UserModel.class);
        when(authorizationService.showEndpointsAction(Mockito.anyString())).thenReturn(true);
        when(authorizationService.authorizationManager(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
        when(userRepo.findByUserEmail(Mockito.anyString())).thenReturn(userModel);
    }

    @Nested
    @DisplayName("Schedule Email Test")
    class ScheduleEmailTest {
        @Test
        @DisplayName("Success - 200")
        void scheduleEmail() throws Exception {
            ScheduleEmailRequest scheduleEmailRequest =getMailRequest();
            ResponseEntity<ScheduleEmailResponse> expectedResponseBody = new ResponseEntity<>(new ScheduleEmailResponse(true, EMAIL_SCHEDULED_SUCCESSFULLY), HttpStatus.OK);
            when(schedulingService.scheduleEmail(Mockito.any())).thenReturn(expectedResponseBody);
            MvcResult mvcResult = mvc.perform(post("/surveyManagement/v1/io.springboot.survey/schedule")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(scheduleEmailRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            verify(schedulingService, times(1)).scheduleEmail(Mockito.any());
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            ScheduleEmailResponse responseMessage = expectedResponseBody.getBody();
            assertEquals(objectMapper.writeValueAsString(responseMessage), actualResponseBody);

        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            ScheduleEmailRequest scheduleEmailRequest =new ScheduleEmailRequest();
            mvc.perform(post("/surveyManagement/v1/io.springboot.survey/schedule")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(scheduleEmailRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }
    private ScheduleEmailRequest getMailRequest()
    {
        ListOfMails listOfMails=new ListOfMails();
        ScheduleEmailRequest scheduleEmailRequest =new ScheduleEmailRequest();
        scheduleEmailRequest.setSenderMail("test@nineleaps.com");
        scheduleEmailRequest.setSurveyLink("link");
        scheduleEmailRequest.setSurveyName("testSurvey");
        scheduleEmailRequest.setMailsList(new ArrayList<>(Collections.singleton(listOfMails)));
        scheduleEmailRequest.setDateTime(System.currentTimeMillis());
        scheduleEmailRequest.setEndDateTime(System.currentTimeMillis());
        scheduleEmailRequest.setFrequency("Weekly");
        return scheduleEmailRequest;
    }
}