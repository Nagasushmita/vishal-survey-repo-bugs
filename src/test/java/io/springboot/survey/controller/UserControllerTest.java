package io.springboot.survey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.request.AddUserRequest;
import io.springboot.survey.request.ModifyUserRequest;
import io.springboot.survey.request.UpdateUsersRequest;
import io.springboot.survey.response.EmployeeDetails;
import io.springboot.survey.response.UserFilter;
import io.springboot.survey.service.UserService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@Tag("Controller")
class UserControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    AuthorizationService authorizationService;
    @MockBean
    UserRepo userRepo;
    @MockBean
    UserService userService;

    @BeforeEach
    void setUp() {
        UserModel userModel = Mockito.mock(UserModel.class);
        when(authorizationService.showEndpointsAction(Mockito.anyString())).thenReturn(true);
        when(authorizationService.authorizationManager(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        when(userRepo.findByUserEmail(Mockito.anyString())).thenReturn(userModel);
    }



    @DisplayName("Add User Test")
    @Nested
    class AddUserTest {
        @Test
        @DisplayName("Success - 201")
        void addUser() throws Exception {
            AddUserRequest addUserRequest = new AddUserRequest();
            EmployeeDetails employeeDetails = new EmployeeDetails();
            addUserRequest.setRoleName("Dummy");
            addUserRequest.setEmployeeDetailsList(new ArrayList<>(Collections.singletonList(employeeDetails)));
            mvc.perform(post("/surveyManagement/v1/employee")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(addUserRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());
            verify(userService, times(1)).addUser(Mockito.any(AddUserRequest.class));
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            AddUserRequest addUserRequest=new AddUserRequest();
            mvc.perform(post("/surveyManagement/v1/employee")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(addUserRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @DisplayName("Delete User Test")
    @Nested
    class DeleteUserTest {
        @Test
        @DisplayName("Success - 204")
        void deleteUser() throws Exception{
            ModifyUserRequest modifyUserRequest=new ModifyUserRequest();
            modifyUserRequest.setListOfMails(new ArrayList<>(Collections.singleton("dummy@nineleaps.com")));
            mvc.perform(delete("/surveyManagement/v1/employee")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","test@nineleaps.com")
                    .content(objectMapper.writeValueAsString(modifyUserRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
            verify(userService, times(1)).deleteUsers(Mockito.any(ModifyUserRequest.class),Mockito.anyString());
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            ModifyUserRequest modifyUserRequest=new ModifyUserRequest();
            mvc.perform(delete("/surveyManagement/v1/employee")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","")
                    .content(objectMapper.writeValueAsString(modifyUserRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Soft Delete Test")
    class SoftDeleteTest {
        @Test
        @DisplayName("Success - 200")
        void softDelete() throws Exception {
            ModifyUserRequest modifyUserRequest=new ModifyUserRequest();
            modifyUserRequest.setListOfMails(new ArrayList<>(Collections.singleton("dummy@nineleaps.com")));
            mvc.perform(put("/surveyManagement/v1/employee/disable")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(modifyUserRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(userService, times(1)).softDeleteUser(Mockito.any(ModifyUserRequest.class));
        }

        @Test
        @DisplayName("Invalid Data")
        void invalidData() throws Exception {
            ModifyUserRequest modifyUserRequest=new ModifyUserRequest();
            mvc.perform(put("/surveyManagement/v1/employee/disable")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(modifyUserRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Update User Test")
    class UpdateUserTest {
        @Test
        @DisplayName("Success - 200")
        void updateUser() throws Exception {
            UpdateUsersRequest updateUsersRequest=new UpdateUsersRequest();
            updateUsersRequest.setEmail("dummy@nineleaps.com");
            updateUsersRequest.setRoleName("dummy");
            mvc.perform(put("/surveyManagement/v1/employee/update")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(updateUsersRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(userService, times(1)).updateRole(Mockito.any(UpdateUsersRequest.class));
        }

        @Test
        @DisplayName("Invalid Data")
        void invalidData() throws Exception {
            UpdateUsersRequest updateUsersRequest=new UpdateUsersRequest();
            mvc.perform(put("/surveyManagement/v1/employee/update")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(updateUsersRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("View User Test")
    class ViewUserTest {
        @Test
        @DisplayName("Success - 200")
        void viewUser() throws Exception{
            mvc.perform(get("/surveyManagement/v1/employees")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","dummy@nineleaps.com")
                    .param("page","0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(get("/surveyManagement/v1/employees")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","")
                    .param("page","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Import User Test")
    class ImportUserTest {
        @Test
        @DisplayName("Success - 200")
        void importUsers() throws Exception {
            AddUserRequest addUserRequest = new AddUserRequest();
            EmployeeDetails employeeDetails = new EmployeeDetails();
            addUserRequest.setRoleName("Dummy");
            addUserRequest.setEmployeeDetailsList(new ArrayList<>(Collections.singletonList(employeeDetails)));
            mvc.perform(post("/surveyManagement/v1/employees/import")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(addUserRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());

        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            AddUserRequest addUserRequest = new AddUserRequest();
            mvc.perform(post("/surveyManagement/v1/employees/import")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(addUserRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Dynamic Search Test")
    class DynamicSearchTest {
        @Test
        @DisplayName("Success - 200")
        void dynamicSearch() throws Exception {
            mvc.perform(get("/surveyManagement/v1/employee/search")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","dummy@nineleaps.com")
                    .param("name","test")
                    .param("active","true")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(get("/surveyManagement/v1/employee/search")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","")
                    .param("name","test")
                    .param("active","true")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("User By Role Test")
    class GetUserByRoleTest {
        @Test
        @DisplayName("Success - 200")
        void getUsersByRole() throws Exception {
            mvc.perform(get("/surveyManagement/v1/employees/filter/role")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","dummy@nineleaps.com")
                    .param("role","test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(userService, times(1)).getAllUserByRole(Mockito.anyString(),Mockito.anyString());
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(get("/surveyManagement/v1/employees/filter/role")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","")
                    .param("role","test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Enable User Test")
    class EnableUserTest{
    @Test
    @DisplayName("Success - 200")
    void enableUser() throws Exception {
        ModifyUserRequest modifyUserRequest=new ModifyUserRequest();
        modifyUserRequest.setListOfMails(new ArrayList<>(Collections.singleton("dummy@nineleaps.com")));
        mvc.perform(put("/surveyManagement/v1/employee/enable")
                .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                .content(objectMapper.writeValueAsString(modifyUserRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1)).enableUser(Mockito.any(ModifyUserRequest.class));
    }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            ModifyUserRequest modifyUserRequest=new ModifyUserRequest();
            mvc.perform(put("/surveyManagement/v1/employee/enable")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(modifyUserRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Disable User Test")
    class ViewDisabledUserTest {
        @Test
        @DisplayName("Success - 200")
        void viewDisabledUsers() throws Exception {
            mvc.perform(get("/surveyManagement/v1/employees/disabled")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","dummy@nineleaps.com")
                    .param("page","0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(get("/surveyManagement/v1/employees/disabled")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","")
                    .param("page","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }


    @Nested
    @DisplayName("Get All Test")
    class GetAll {
        @Test
        @DisplayName("Success - 200")
        void getAll() throws Exception {
            UserFilter userFilter=new UserFilter();
            mvc.perform(post("/surveyManagement/v1/io.springboot.survey/users/filter")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(userFilter))
                    .param("email","dummy@nineleaps.com")
                    .param("page","0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            UserFilter userFilter=new UserFilter();
            mvc.perform(post("/surveyManagement/v1/io.springboot.survey/users/filter")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(userFilter))
                    .param("email","")
                    .param("page","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Test
    @DisplayName("Filter Info Test")
    void getFilterInfo() throws Exception {
        mvc.perform(get("/surveyManagement/v1/filter/info")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}