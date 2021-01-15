package io.springboot.survey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.request.OtpRequest;
import io.springboot.survey.service.UserService;
import io.springboot.survey.utils.AuthorizationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(LoginController.class)
@Tag("Controller")
class LoginControllerTest {


    @Autowired
    private MockMvc mvc;
    @MockBean
    UserService userService;
    @MockBean
    AuthorizationService authorizationService;
    @MockBean
    UserRepo userRepo;
    @Autowired
    ObjectMapper objectMapper;


    @Nested
    @DisplayName("Send Email Test")
    class SendEmailTest {
        @Test
        @DisplayName("Null Value Return 422")
        void emailNull() throws Exception {
            mvc.perform((get("/surveyManagement/v1/login/email")
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("email", "")))
                    .andExpect(status().isUnprocessableEntity());
        }
        @Test
        @DisplayName("Valid Input Return 200")
        void validInput() throws Exception {
            UserModel userModel=Mockito.mock(UserModel.class);
            when(userRepo.findByUserEmail(Mockito.anyString())).thenReturn(userModel);
            String email="vishal.jha@nineleaps.com";
            mvc.perform((get("/surveyManagement/v1/login/email")
                   .contentType(MediaType.TEXT_PLAIN)
                   .param("email", email)))
                    .andExpect(status().isOk())
                    .andReturn();
        }
    }
    @Nested
    @DisplayName("Check Otp Test")
    class CheckOtpTest{

        @Test
        @DisplayName("Success - 200")
        void success() throws Exception{
            OtpRequest otpRequest=new OtpRequest();
            otpRequest.setOtpValue("23");
           mvc.perform(post("/surveyManagement/v1/login/email/otp")
                    .content(objectMapper.writeValueAsString(otpRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(userService, times(1)).userAuthentication(Mockito.any());
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception{
            OtpRequest otpRequest=new OtpRequest();
            mvc.perform(post("/surveyManagement/v1/login/email/otp")
                    .content(objectMapper.writeValueAsString(otpRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

}