package io.springboot.survey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.springboot.survey.exception.BadRequestException;
import io.springboot.survey.exception.ForbiddenException;
import io.springboot.survey.models.RoleModel;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.service.RoleService;
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

import static io.springboot.survey.utils.Constants.ErrorMessageConstant.ROLE_ADDED;
import static io.springboot.survey.utils.Constants.ValidationConstant.MOCK_TOKEN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(RoleController.class)
@Tag("Controller")
class RoleControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    UserRepo userRepo;
    @MockBean
    AuthorizationService authorizationService;
    @MockBean
    RoleService roleService;


    @BeforeEach
    void setUp() {
        UserModel userModel = Mockito.mock(UserModel.class);
        when(authorizationService.showEndpointsAction(Mockito.anyString())).thenReturn(true);
        when(authorizationService.authorizationManager(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
        when(userRepo.findByUserEmail(Mockito.anyString())).thenReturn(userModel);
    }

    @Test
    @DisplayName("Get All Roles Test")
    void getRoles() throws Exception {
        RoleModel roleModel=new RoleModel();
        roleModel.setRoleId(1);
        roleModel.setRole("dummyRole");
        roleModel.setCreatedBy(1);
        roleModel.setCreatedOn(System.currentTimeMillis());
        List<RoleModel> expectedResponse=new ArrayList<>(Collections.singletonList(roleModel));
        when(roleService.getAllRoles()).thenReturn(expectedResponse);
        MvcResult mvcResult = mvc.perform(get("/surveyManagement/v1/employee/roles")
                .header(HttpHeaders.AUTHORIZATION,MOCK_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        verify(roleService, times(1)).getAllRoles();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(objectMapper.writeValueAsString(expectedResponse), actualResponseBody);

    }
    @Nested
    @DisplayName("Add Role Test")
    class AddRoleTest {
        @Test
        @DisplayName("Success - 201")
        void addRole() throws Exception {
            ResponseEntity<ResponseMessage> expectedResponse=new ResponseEntity<>(new ResponseMessage(HttpStatus.CREATED.value(),ROLE_ADDED),HttpStatus.CREATED);
            when(roleService.addRole(Mockito.anyString(),Mockito.anyString() )).thenReturn(expectedResponse);
            MvcResult mvcResult = mvc.perform(post("/surveyManagement/v1/role")
                    .header(HttpHeaders.AUTHORIZATION,MOCK_TOKEN)
                    .param("roleName","dummyRole")
                    .param("email","dummy@nineleaps.com")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();
            verify(roleService, times(1)).addRole(Mockito.anyString(),Mockito.anyString() );
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            ResponseMessage responseMessage=expectedResponse.getBody();
            assertEquals(objectMapper.writeValueAsString(responseMessage), actualResponseBody);
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(post("/surveyManagement/v1/role")
                    .header(HttpHeaders.AUTHORIZATION,MOCK_TOKEN)
                    .param("roleName","")
                    .param("email","")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Delete Role Test")
    class DeleteRoleTest {
        @Test
        @DisplayName("Success - 204")
        void deleteRole() throws Exception {
            ResponseEntity<Void> expectedResponse=ResponseEntity.noContent().build();
            when(roleService.deleteRole(Mockito.anyString())).thenReturn(expectedResponse);
             mvc.perform(delete("/surveyManagement/v1/role")
                    .header(HttpHeaders.AUTHORIZATION,MOCK_TOKEN)
                    .param("roleName","dummyRole")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
            verify(roleService, times(1)).deleteRole(Mockito.anyString());

        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(delete("/surveyManagement/v1/role")
                    .header(HttpHeaders.AUTHORIZATION,MOCK_TOKEN)
                    .param("roleName","")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Test
    @DisplayName("Forbidden Exception")
    void forbiddenException() throws Exception {
        when(roleService.deleteRole(Mockito.anyString())).thenThrow(ForbiddenException.class);
        mvc.perform(delete("/surveyManagement/v1/role")
                .header(HttpHeaders.AUTHORIZATION,MOCK_TOKEN)
                .param("roleName","dummyRole")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Bad Request Exception")
    void badRequestException() throws Exception {
        when(roleService.deleteRole(Mockito.anyString())).thenThrow(BadRequestException.class);
        mvc.perform(delete("/surveyManagement/v1/role")
                .header(HttpHeaders.AUTHORIZATION,MOCK_TOKEN)
                .param("roleName","dummyRole")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}