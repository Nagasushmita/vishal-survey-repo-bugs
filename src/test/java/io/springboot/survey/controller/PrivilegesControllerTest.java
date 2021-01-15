package io.springboot.survey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.request.PrivilegesRequest;
import io.springboot.survey.response.PrivilegeSet;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.service.PrivilegesService;
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
import java.util.List;

import static io.springboot.survey.utils.Constants.ErrorMessageConstant.PRIVILEGES_ADDED;
import static io.springboot.survey.utils.Constants.ValidationConstant.MOCK_TOKEN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PrivilegesController.class)
@Tag("Controller")
class PrivilegesControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PrivilegesService privilegesService;
    @MockBean
    AuthorizationService authorizationService;
    @MockBean
    UserRepo userRepo;


    @BeforeEach
    void setUp()
    {
        UserModel userModel = Mockito.mock(UserModel.class);
        when(authorizationService.showEndpointsAction(Mockito.anyString())).thenReturn(true);
        when(authorizationService.authorizationManager(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
        when(userRepo.findByUserEmail(Mockito.anyString())).thenReturn(userModel);
    }


    @Nested
    @DisplayName("Mapping Privileges Test")
    class MappingPrivilegesTest {
        @Test
        @DisplayName("Success - 200")
        void mappingPrivileges() throws Exception {
            PrivilegesRequest privilegesRequest = new PrivilegesRequest();
            PrivilegeSet privilegeSet = new PrivilegeSet();
            privilegesRequest.setPrivileges(new ArrayList<>(Collections.singletonList(privilegeSet)));
            privilegesRequest.setRoleName("dummyRole");
            ResponseEntity<ResponseMessage> expectedResponse = new ResponseEntity<>(new ResponseMessage(HttpStatus.OK.value(), PRIVILEGES_ADDED), HttpStatus.OK);
            when(privilegesService.mapPrivileges(Mockito.any(PrivilegesRequest.class))).thenReturn(expectedResponse);
            MvcResult mvcResult = mvc.perform(post("/surveyManagement/v1/role/privileges")
                    .header(HttpHeaders.AUTHORIZATION,MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(privilegesRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            verify(privilegesService, times(1)).mapPrivileges(Mockito.any());
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            ResponseMessage responseMessage = expectedResponse.getBody();
            assertEquals(objectMapper.writeValueAsString(responseMessage), actualResponseBody);
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            PrivilegesRequest privilegesRequest = new PrivilegesRequest();
            mvc.perform(post("/surveyManagement/v1/role/privileges")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(privilegesRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());

        }
    }

    @Test
    @DisplayName("Find All Test")
    void findAll() throws Exception{
        PrivilegeSet privilegeSet=new PrivilegeSet();
        PrivilegesRequest privilegesRequest=new PrivilegesRequest();
        privilegesRequest.setRoleName("dummy");
        privilegesRequest.setPrivileges(new ArrayList<>(Collections.singleton(privilegeSet)));
        List<PrivilegesRequest> expectedResponse=new ArrayList<>(Collections.singleton(privilegesRequest));
        when(privilegesService.showAllPrivileges()).thenReturn(expectedResponse);
        MvcResult mvcResult = mvc.perform(get("/surveyManagement/v1/all-roles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        verify(privilegesService, times(1)).showAllPrivileges();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(objectMapper.writeValueAsString(expectedResponse), actualResponseBody);
    }


    @Nested
    @DisplayName("Privileges By Role Test")
    class PrivilegesByRoleTest {
        @Test
        @DisplayName("Success - 200")
        void getPrivilegeByRoleName() throws Exception {
            PrivilegeSet privilegeSet=new PrivilegeSet();
            PrivilegesRequest privilegesRequest=new PrivilegesRequest();
            privilegesRequest.setRoleName("dummy");
            privilegesRequest.setPrivileges(new ArrayList<>(Collections.singleton(privilegeSet)));
            when(privilegesService.showPrivileges(Mockito.anyString())).thenReturn(privilegesRequest);
            MvcResult mvcResult = mvc.perform(get("/surveyManagement/v1/employee/role/privileges")
                    .param("roleName","dummyRole")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            verify(privilegesService, times(1)).showPrivileges(Mockito.anyString());
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertEquals(objectMapper.writeValueAsString(privilegesRequest), actualResponseBody);
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(get("/surveyManagement/v1/employee/role/privileges")
                    .param("roleName","")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());

        }
    }
}