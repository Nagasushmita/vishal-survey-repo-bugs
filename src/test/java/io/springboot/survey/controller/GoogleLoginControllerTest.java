package io.springboot.survey.controller;

import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.UserRepo;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(GoogleLoginController.class)
@Tag("Controller")
class GoogleLoginControllerTest {


    @Autowired
    private MockMvc mvc;
    @MockBean
    UserService userService;
    @MockBean
    AuthorizationService authorizationService;
    @MockBean
    UserRepo userRepo;


    @Nested
    @DisplayName("Google Login Test")
    class GoogleLoginTest{
    @Test
    @DisplayName("Success - 200")
    void googleLogin() throws Exception {
        UserModel userModel=Mockito.mock(UserModel.class);
        when(userRepo.findByUserEmail(Mockito.anyString())).thenReturn(userModel);
        mvc.perform(get("/surveyManagement/v1/login/google")
                .param("email","vishal.jha@nineleaps.com")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk());
        Mockito.verify(userService,Mockito.times(1)).googleLogin("vishal.jha@nineleaps.com");
    }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(get("/surveyManagement/v1/login/google")
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("email",""))
                    .andExpect(status().isUnprocessableEntity());
        }
    }
}