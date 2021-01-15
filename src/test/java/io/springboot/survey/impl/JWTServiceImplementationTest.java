package io.springboot.survey.impl;

import io.springboot.survey.exception.AuthorizationException;
import io.springboot.survey.models.UserModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
class JWTServiceImplementationTest {

    @InjectMocks
    JWTServiceImplementation jwtServiceImplementation;

    @Test
    @DisplayName("Generate Token Test")
    void generateToken() {
        UserModel userModel= Mockito.mock(UserModel.class);
        String response=jwtServiceImplementation.generateToken(userModel);
        assertNotNull(response);
    }

    @Test
    @DisplayName("Extract UserEmail Test")
    void extractUserEmail() {
        String mockToken=getToken();
        String response= jwtServiceImplementation.extractUserEmail(mockToken);
        assertNotNull(response);
    }
    @Test
    @DisplayName("Extract UserEmail Exception Test")
    void extractUserEmailException() {
        String mockToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2aXNoYWwuamhhQG5pbmVsZWFwcy5jb20iLCJleHAiOjE1OTY3OTExOTEsImlhdCI6MTU5NjcyNjM4OX0.LcwGqTQKErkgBE0-XxfCfngWNJDjAMmhIrpr31sCVJI";
        Throwable exception = assertThrows(AuthorizationException.class, () -> jwtServiceImplementation.extractUserEmail(mockToken));
        assertNotNull(exception);

    }

    private String getToken()
    {
        UserModel userModel=new UserModel();
        userModel.setUserName("Vishal Jha");
        userModel.setUserEmail("vishal.jha@nineleaps.com");
       return jwtServiceImplementation.generateToken(userModel).substring(7);
    }
    }