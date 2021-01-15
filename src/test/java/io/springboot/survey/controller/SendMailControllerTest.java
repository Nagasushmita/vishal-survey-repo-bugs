package io.springboot.survey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.request.EmailRequest;
import io.springboot.survey.response.ListOfMails;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.service.MailService;
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

import static io.springboot.survey.utils.Constants.ErrorMessageConstant.SUCCESS;
import static io.springboot.survey.utils.Constants.ValidationConstant.MOCK_TOKEN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SendMailController.class)
@Tag("Controller")
class SendMailControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    AuthorizationService authorizationService;
    @MockBean
    UserRepo userRepo;
    @MockBean
    MailService mailService;


    @BeforeEach
    void setUp() {
        UserModel userModel = Mockito.mock(UserModel.class);
        when(authorizationService.showEndpointsAction(Mockito.anyString())).thenReturn(true);
        when(authorizationService.authorizationManager(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
        when(userRepo.findByUserEmail(Mockito.anyString())).thenReturn(userModel);
    }


    @Nested
    @DisplayName("Send Reminder Email Test")
    class SendReminderEmail {
        @Test
        @DisplayName("Success - 200")
        void sendReminderEmail() throws Exception {
            EmailRequest emailRequest=getMailRequest();
            ResponseEntity<ResponseMessage> expectedResponse=new ResponseEntity<>(new ResponseMessage(HttpStatus.OK.value(),SUCCESS),HttpStatus.OK);
            when(mailService.sendEmail(Mockito.any(EmailRequest.class),Mockito.anyBoolean())).thenReturn(expectedResponse);
            MvcResult mvcResult = mvc.perform(post("/surveyManagement/v1/io.springboot.survey/reminder")
                    .header(HttpHeaders.AUTHORIZATION,MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(emailRequest))
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            ResponseMessage responseMessage=expectedResponse.getBody();
            assertEquals(objectMapper.writeValueAsString(responseMessage), actualResponseBody);
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception{
            EmailRequest emailRequest=new EmailRequest();
            mvc.perform(post("/surveyManagement/v1/io.springboot.survey/reminder")
                    .header(HttpHeaders.AUTHORIZATION,MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(emailRequest))
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Send Email Test")
    class SendEmailTest {
        @Test
        @DisplayName("Success - 200")
        void sendEmail() throws Exception{
            EmailRequest emailRequest=getMailRequest();
            ResponseEntity<ResponseMessage> expectedResponse=new ResponseEntity<>(new ResponseMessage(HttpStatus.OK.value(),SUCCESS),HttpStatus.OK);
            when(mailService.sendEmail(Mockito.any(EmailRequest.class),Mockito.anyBoolean())).thenReturn(expectedResponse);
            MvcResult mvcResult = mvc.perform(post("/surveyManagement/v1/io.springboot.survey/send")
                    .header(HttpHeaders.AUTHORIZATION,MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(emailRequest))
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            ResponseMessage responseMessage=expectedResponse.getBody();
            assertEquals(objectMapper.writeValueAsString(responseMessage), actualResponseBody);

        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            EmailRequest emailRequest=new EmailRequest();
            mvc.perform(post("/surveyManagement/v1/io.springboot.survey/send")
                    .header(HttpHeaders.AUTHORIZATION,MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(emailRequest))
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity())
                    .andReturn();
        }
    }

    private EmailRequest getMailRequest()
    {
        ListOfMails listOfMails=new ListOfMails();
        EmailRequest emailRequest=new EmailRequest();
        emailRequest.setSurveyName("testSurvey");
        emailRequest.setSenderMail("test@nineleap.com");
        emailRequest.setMailsList(new ArrayList<>(Collections.singletonList(listOfMails)));
        return emailRequest;

    }
}