package io.springboot.survey.impl;


import io.springboot.survey.exception.APIException;
import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.pojo.GetRequestParam;
import io.springboot.survey.pojo.user.DynamicSearchParam;
import io.springboot.survey.pojo.user.GetAllParam;
import io.springboot.survey.repository.RoleRepo;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.request.AddUserRequest;
import io.springboot.survey.request.ModifyUserRequest;
import io.springboot.survey.request.OtpRequest;
import io.springboot.survey.request.UpdateUsersRequest;
import io.springboot.survey.response.AuthenticationResponse;
import io.springboot.survey.response.EmployeeDetails;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.response.UserFilter;
import io.springboot.survey.service.JwtUtil;
import io.springboot.survey.service.NotificationService;
import io.springboot.survey.specification.SpecificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
class UserServiceImplementationTest {

    @InjectMocks
    UserServiceImplementation userServiceImplementation;
    @Mock
    NotificationService notificationService;
    @Mock
    UserRepo userRepo;
    @Mock
    RoleRepo roleRepo;
    @Mock
    JwtUtil jwtUtil;
    @Mock
    SpecificationService specification;


    @Nested
    @DisplayName("Send email")
    class sendEmail {
        @Test
        @DisplayName("Success")
        void sendEmailSuccess() {
            UserModel userModel = mock(UserModel.class);
            when(userRepo.findByUserEmailAndActive(anyString(), anyBoolean())).thenReturn(userModel);
            when(userRepo.findByUserEmail(anyString())).thenReturn(userModel);
            doNothing().when(notificationService).sendNotification("dummy@nineleaps.com");
            ResponseEntity<ResponseMessage> response = userServiceImplementation.sendEmail("dummy@nineleaps.com");
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
        @Test
        @DisplayName("User Model Null")
        void sendEmailNotSuccess() {
            when(userRepo.findByUserEmailAndActive(anyString(), anyBoolean())).thenReturn(null);
            ResponseEntity<ResponseMessage> response = userServiceImplementation.sendEmail("dummy@nineleaps.com");
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("Exception")
        void exception() {
            UserModel userModel = mock(UserModel.class);
            when(userRepo.findByUserEmailAndActive(anyString(), anyBoolean())).thenReturn(userModel);
            when(userRepo.findByUserEmail(anyString())).thenReturn(userModel);
            doThrow(RuntimeException.class).when(notificationService).sendNotification("dummy@nineleaps.com");
            Exception exception=assertThrows(APIException.class,()->userServiceImplementation.sendEmail("dummy@nineleaps.com"));
            assertNotNull(exception);
        }
    }

    @Nested
    @DisplayName("User Authentication Test")
    class userAuthenticationTest {
        @Test
        @DisplayName("Success")
        void userAuthentication() {
            UserModel userModel = mock(UserModel.class);
            OtpRequest otpRequest = new OtpRequest();
            otpRequest.setOtpValue("123");
            when(notificationService.getOtp()).thenReturn("123");
            ReflectionTestUtils.setField(userServiceImplementation, "userModel", userModel);
            when(roleRepo.getRoleByRoleId(anyInt())).thenReturn("dummy");
            when(userRepo.findByUserName(null)).thenReturn(userModel);
            when(jwtUtil.generateToken(any())).thenReturn("link");
            ResponseEntity<AuthenticationResponse> response = userServiceImplementation.userAuthentication(otpRequest);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
        @Test
        @DisplayName("Failure")
        void userAuthenticationFailure() {
            OtpRequest otpRequest = new OtpRequest();
            otpRequest.setOtpValue("123");
            when(notificationService.getOtp()).thenReturn("1234");
            ResponseEntity<AuthenticationResponse> response = userServiceImplementation.userAuthentication(otpRequest);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        }
    }

    @Nested
    @DisplayName("Google login Test")
    class GoogleLogging {
        @Test
        @DisplayName("Success")
        void googleLogin() {
            UserModel userModel= mock(UserModel.class);
            when(userRepo.findByUserEmail(anyString())).thenReturn(userModel);
            when(userRepo.findByUserName(null)).thenReturn(userModel);
            when(jwtUtil.generateToken(any())).thenReturn("link");
            when(roleRepo.getRoleByRoleId(anyInt())).thenReturn("dummy");
            ResponseEntity<AuthenticationResponse> response=userServiceImplementation.googleLogin("dummy@nineleaps.com");
            assertEquals(HttpStatus.OK,response.getStatusCode());
        }
        @Test
        @DisplayName("Exception")
        void exception() {
            when(userRepo.findByUserEmail(anyString())).thenReturn(null);
            Exception exception=assertThrows(APIException.class,
                    ()->userServiceImplementation.googleLogin("dummy@nineleaps.com"));
            assertNotNull(exception);
        }
    }

    @Nested
    @DisplayName("Add User Test")
    class  AddUser {
        @Test
        @DisplayName("Success")
        void addUser() {
            AddUserRequest addUserRequest=new AddUserRequest();
            EmployeeDetails employeeDetails= mock(EmployeeDetails.class);
            addUserRequest.setEmployeeDetailsList(new ArrayList<>(Collections.singletonList(employeeDetails)));
            when(userRepo.findByUserEmail(null)).thenReturn(null);
            when(userRepo.findByOrgId(null)).thenReturn(null);
            when(roleRepo.getRoleIdByRole(null)).thenReturn(1);
            ResponseEntity<ResponseMessage> response=userServiceImplementation.addUser(addUserRequest);
            assertEquals(HttpStatus.CREATED,response.getStatusCode());
        }
        @Test
        @DisplayName("Id Already exist")
        void idAlreadyExist() {
            UserModel userModel= mock(UserModel.class);
            AddUserRequest addUserRequest=new AddUserRequest();
            EmployeeDetails employeeDetails= mock(EmployeeDetails.class);
            addUserRequest.setEmployeeDetailsList(new ArrayList<>(Collections.singletonList(employeeDetails)));
            when(userRepo.findByUserEmail(null)).thenReturn(null);
            when(userRepo.findByOrgId(null)).thenReturn(userModel);
            ResponseEntity<ResponseMessage> response=userServiceImplementation.addUser(addUserRequest);
            assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
        }
        @Test
        @DisplayName("Email Already exist")
        void emailAlreadyExist() {
            UserModel userModel= mock(UserModel.class);
            AddUserRequest addUserRequest=new AddUserRequest();
            EmployeeDetails employeeDetails= mock(EmployeeDetails.class);
            addUserRequest.setEmployeeDetailsList(new ArrayList<>(Collections.singletonList(employeeDetails)));
            when(userRepo.findByUserEmail(null)).thenReturn(userModel);
            ResponseEntity<ResponseMessage> response=userServiceImplementation.addUser(addUserRequest);
            assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
        }
        @Test
        @DisplayName("Exception")
        void exception() {
            AddUserRequest addUserRequest=new AddUserRequest();
            EmployeeDetails employeeDetails= mock(EmployeeDetails.class);
            addUserRequest.setEmployeeDetailsList(new ArrayList<>(Collections.singletonList(employeeDetails)));
            when(userRepo.findByUserEmail(null)).thenReturn(null);
            when(userRepo.findByOrgId(null)).thenReturn(null);
            when(roleRepo.getRoleIdByRole(null)).thenReturn(null);
            Exception exception=assertThrows(APIException.class,()->userServiceImplementation.addUser(addUserRequest));
            assertNotNull(exception);
        }
    }

    @Nested
    @DisplayName("Delete User Test")
    class DeleteUsers {
        @Test
        @DisplayName("Success")
        void deleteUsersSuccess() {
            ModifyUserRequest modifyUserRequest=new ModifyUserRequest();
            modifyUserRequest.setListOfMails(new ArrayList<>(Collections.singleton("dummy@nineleaps.com")));
            when(userRepo.deleteByUserEmail(anyString())).thenReturn(1L);
            ResponseEntity<Void> response=userServiceImplementation.deleteUsers(modifyUserRequest,"test@nineleaps.com");
            assertEquals(HttpStatus.NO_CONTENT,response.getStatusCode());
        }
        @Test
        @DisplayName(value = "Failure And Exception")
        void deleteUsersFailure() {
            ModifyUserRequest modifyUserRequest=new ModifyUserRequest();
            modifyUserRequest.setListOfMails(new ArrayList<>(Collections.singleton("dummy@nineleaps.com")));
            Exception exception1=assertThrows(APIException.class,()->userServiceImplementation.deleteUsers(modifyUserRequest,"dummy@nineleaps.com"));
            when(userRepo.deleteByUserEmail(anyString())).thenReturn(0L);
            Exception exception2=assertThrows(APIException.class,()->userServiceImplementation.deleteUsers(modifyUserRequest,"test@nineleaps.com"));
            assertAll(()->assertNotNull(exception1),
                    ()->assertNotNull(exception2));
        }
    }

    @Nested
    @DisplayName("Soft Delete Test")
    class SoftDeleteTest {
        @Test
        @DisplayName("Success")
        void softDeleteUser() {
            UserModel userModel= mock(UserModel.class);
            ModifyUserRequest modifyUserRequest=new ModifyUserRequest();
            modifyUserRequest.setListOfMails(new ArrayList<>(Collections.singleton("dummy@nineleaps.com")));
            when(userRepo.findByUserEmail(anyString())).thenReturn(userModel);
            ResponseEntity<ResponseMessage> response=userServiceImplementation.softDeleteUser(modifyUserRequest);
            assertEquals(HttpStatus.OK,response.getStatusCode());
        }
        @Test
        @DisplayName("Failure")
        void softDeleteFailure() {
            ModifyUserRequest modifyUserRequest=new ModifyUserRequest();
            modifyUserRequest.setListOfMails(new ArrayList<>(Collections.singleton("dummy@nineleaps.com")));
            when(userRepo.findByUserEmail(anyString())).thenReturn(null);
            Exception exception=assertThrows(ResourceNotFoundException.class,
                    ()->userServiceImplementation.softDeleteUser(modifyUserRequest));
            assertNotNull(exception);
        }

    }

    @Nested
    @DisplayName("Update User Test")
    class UpdateRoleTest {
        @Test
        @DisplayName("Designation")
        void designation() {
            UserModel userModel= mock(UserModel.class);
            UpdateUsersRequest updateUsersRequest=new UpdateUsersRequest();
            updateUsersRequest.setEmail("dummy@nineleap.com");
            updateUsersRequest.setDesignation("SD2");
            updateUsersRequest.setRoleName(null);
            when(userRepo.findByUserEmail(anyString())).thenReturn(userModel);
            ResponseEntity<ResponseMessage> response=userServiceImplementation.updateRole(updateUsersRequest);
            assertEquals(HttpStatus.OK,response.getStatusCode());
        }
        @Test
        @DisplayName("Role")
        void role() {
            UserModel userModel= mock(UserModel.class);
            UpdateUsersRequest updateUsersRequest=new UpdateUsersRequest();
            updateUsersRequest.setEmail("dummy@nineleap.com");
            updateUsersRequest.setRoleName("Employee");
            when(userRepo.findByUserEmail(anyString())).thenReturn(userModel);
            ResponseEntity<ResponseMessage> response=userServiceImplementation.updateRole(updateUsersRequest);
            assertEquals(HttpStatus.OK,response.getStatusCode());
        }
        @Test
        @DisplayName("Designation And Role")
        void both() {
            UserModel userModel= mock(UserModel.class);
            UpdateUsersRequest updateUsersRequest=new UpdateUsersRequest();
            updateUsersRequest.setEmail("dummy@nineleap.com");
            updateUsersRequest.setDesignation("SD2");
            updateUsersRequest.setRoleName("Employee");
            when(userRepo.findByUserEmail(anyString())).thenReturn(userModel);
            ResponseEntity<ResponseMessage> response=userServiceImplementation.updateRole(updateUsersRequest);
            assertEquals(HttpStatus.OK,response.getStatusCode());
        }
        @Test
        @DisplayName("Both Null")
        void nothing() {
            UserModel userModel= mock(UserModel.class);
            UpdateUsersRequest updateUsersRequest= Mockito.mock(UpdateUsersRequest.class);
            when(userRepo.findByUserEmail(null)).thenReturn(userModel);
            Exception exception=assertThrows(APIException.class,()->userServiceImplementation.updateRole(updateUsersRequest));
            assertNotNull(exception);
        }

//        @Test
//        @DisplayName("Both Empty")
//        void bothEmpty() {
//            UserModel userModel= mock(UserModel.class);
//            UpdateUsersRequest updateUsersRequest= new UpdateUsersRequest();
//            updateUsersRequest.setDesignation("");
//            updateUsersRequest.setRoleName("");
//            updateUsersRequest.setEmail("dummy@nineleaps.com");
//            when(userRepo.findByUserEmail(Mockito.anyString())).thenReturn(userModel);
//            Exception exception=assertThrows(APIException.class,()->userServiceImplementation.updateRole(updateUsersRequest));
//            assertNotNull(exception);
//        }
    }

    @Test
    @DisplayName("View User Test")
    void viewUser() {
        UserModel userModel= mock(UserModel.class);
        List<UserModel>  modelList=new ArrayList<>(Collections.singleton(userModel));
        Page<UserModel> modelPage= new PageImpl<>(modelList);
        when(userRepo.findAllByUserEmailNotContainingAndActive(anyString(), any(Pageable.class), anyBoolean())).thenReturn(modelPage);
        when(userRepo.findAllByActiveTrueAndUserEmailNotContaining(anyString())).thenReturn(modelList);
        when(roleRepo.getRoleByRoleId(anyInt())).thenReturn("dummy");
        List<HashMap<String, String>> response=userServiceImplementation.viewUser(new GetRequestParam("dummy",1,1,"name"));
        assertNotNull(response);
    }

    @Nested
    @DisplayName("Import User Test")
    class ImportTestTest {
        @Test
        @DisplayName("Success")
        void success() {
            AddUserRequest addUserRequest=new AddUserRequest();
            EmployeeDetails employeeDetails= mock(EmployeeDetails.class);
            addUserRequest.setEmployeeDetailsList(new ArrayList<>(Collections.singletonList(employeeDetails)));
            when(userRepo.findByUserEmail(null)).thenReturn(null);
            when(roleRepo.getRoleIdByRole(null)).thenReturn(1);
            ResponseEntity<ResponseMessage> response=userServiceImplementation.importUsers(addUserRequest);
            assertEquals(HttpStatus.CREATED,response.getStatusCode());
        }
        @Test
        @DisplayName("Id Already exist")
        void idAlreadyExist() {
            UserModel userModel= mock(UserModel.class);
            AddUserRequest addUserRequest=new AddUserRequest();
            EmployeeDetails employeeDetails= mock(EmployeeDetails.class);
            addUserRequest.setEmployeeDetailsList(new ArrayList<>(Collections.singletonList(employeeDetails)));
            when(userRepo.findByUserEmail(null)).thenReturn(userModel);
            ResponseEntity<ResponseMessage> response=userServiceImplementation.importUsers(addUserRequest);
            assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
        }
        @Test
        @DisplayName("Email Already exist")
        void emailAlreadyExist() {
            UserModel userModel= mock(UserModel.class);
            AddUserRequest addUserRequest=new AddUserRequest();
            EmployeeDetails employeeDetails= mock(EmployeeDetails.class);
            addUserRequest.setEmployeeDetailsList(new ArrayList<>(Collections.singletonList(employeeDetails)));
            when(userRepo.findByUserEmail(null)).thenReturn(userModel);
            ResponseEntity<ResponseMessage> response=userServiceImplementation.importUsers(addUserRequest);
            assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
        }
        @Test
        @DisplayName("Exception")
        void exception() {
            AddUserRequest addUserRequest=new AddUserRequest();
            EmployeeDetails employeeDetails= mock(EmployeeDetails.class);
            addUserRequest.setEmployeeDetailsList(new ArrayList<>(Collections.singletonList(employeeDetails)));
            when(userRepo.findByUserEmail(null)).thenReturn(null);
            when(roleRepo.getRoleIdByRole(null)).thenReturn(null);
            Exception exception=assertThrows(APIException.class,()->userServiceImplementation.importUsers(addUserRequest));
            assertNotNull(exception);
        }
        @Test
        @DisplayName("Bad Request")
        void badRequest() {
            AddUserRequest addUserRequest=new AddUserRequest();
            addUserRequest.setEmployeeDetailsList(new ArrayList<>());
            ResponseEntity<ResponseMessage> response=userServiceImplementation.importUsers(addUserRequest);
            assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        }

    }

    @Nested
    @DisplayName("Dynamic Search Test")
    class DynamicSearchTest {
        @Test
        @DisplayName("Active")
        void dynamicSearch() {
            UserModel userModel1=mock(UserModel.class);
            UserModel userModel= mock(UserModel.class);
            when(userRepo.findByUserNameStartingWithIgnoreCaseAndActiveTrue(anyString()))
                    .thenReturn(new ArrayList<>(Collections.singleton(userModel)));
           when(userRepo.findByUserNameContainingIgnoreCaseAndActiveTrue(anyString()))
                   .thenReturn(new ArrayList<>(Collections.singleton(userModel)));
           when(userRepo.findByUserEmail(anyString())).thenReturn(userModel1);
            List<HashMap<String, String>> response=userServiceImplementation.dynamicSearch(new DynamicSearchParam("dummy@nineleap.com"
                    ,"test",1,true));
            assertNotNull(response);
        }
        @Test
        @DisplayName("In Active")
        void inActiveUsers() {
            UserModel userModel1= mock(UserModel.class);
            UserModel userModel2=mock(UserModel.class);
            UserModel userModel3=mock(UserModel.class);
            when(userRepo.findByUserNameStartingWithIgnoreCaseAndActiveFalse(anyString()))
                    .thenReturn(new ArrayList<>(Arrays.asList(userModel1,userModel2)));
            when(userRepo.findByUserNameContainingIgnoreCaseAndActiveFalse(anyString()))
                    .thenReturn(new ArrayList<>(Arrays.asList(userModel1,userModel2)));
            when(userRepo.findByUserEmail(anyString())).thenReturn(userModel3);
            List<HashMap<String, String>> response=userServiceImplementation.dynamicSearch(new DynamicSearchParam("dummy@nineleap.com"
                    ,"test",10,false));
            assertNotNull(response);
        }
    }

    @Nested
    @DisplayName("Get User By Role Test")
    class GetUserByRoleTest {
        @Test
        @DisplayName("Role Not Null")
        void getAllUserByRole() {
            UserModel userModel= mock(UserModel.class);
            when(roleRepo.getRoleIdByRole(anyString())).thenReturn(1);
            when(userRepo.findByRoleId(anyInt())).thenReturn(new ArrayList<>(Collections.singleton(userModel)));
            List<UserModel> response=userServiceImplementation.getAllUserByRole("HR","dummy@nineleaps.com");
            assertNotNull(response);
        }
        @Test
        @DisplayName("Role Null")
        void roleNull() {
            UserModel userModel= mock(UserModel.class);
            when(userRepo.findAllByActiveTrueAndUserEmailNotContaining(anyString())).thenReturn(new ArrayList<>(Collections.singleton(userModel)));
            List<UserModel> response=userServiceImplementation.getAllUserByRole("","dummy@nineleaps.com");
            assertNotNull(response);
        }
    }


    @Nested
    @DisplayName("Enable User Test")
    class EnableUserTest {
        @Test
        @DisplayName("Success")
        void enableUser() {
            UserModel userModel= mock(UserModel.class);
            ModifyUserRequest modifyUserRequest=new ModifyUserRequest();
            modifyUserRequest.setListOfMails(new ArrayList<>(Collections.singleton("dummy@nineleaps.com")));
            when(userRepo.findByUserEmail(anyString())).thenReturn(userModel);
            ResponseEntity<ResponseMessage> response=userServiceImplementation.enableUser(modifyUserRequest);
            assertEquals(HttpStatus.OK,response.getStatusCode());

        }
        @Test
        @DisplayName("Exception")
        void exception() {
            ModifyUserRequest modifyUserRequest=new ModifyUserRequest();
            modifyUserRequest.setListOfMails(new ArrayList<>(Collections.singleton("dummy@nineleaps.com")));
            when(userRepo.findByUserEmail(anyString())).thenReturn(null);
            Exception exception=assertThrows(ResourceNotFoundException.class,()->userServiceImplementation.enableUser(modifyUserRequest));
           assertNotNull(exception);
        }
    }
    @Test
    @DisplayName("View Disabled User Test")
    void viewDisabledUser() {
        UserModel userModel= mock(UserModel.class);
        List<UserModel> userModelList=new ArrayList<>(Collections.singleton(userModel));
        Page<UserModel> userModelPage=new PageImpl<>(userModelList);
         when(userRepo.findAllByUserEmailNotContainingAndActive(anyString(), any(), anyBoolean()))
                 .thenReturn(userModelPage);
       when(userRepo.findAllByActiveFalseAndUserEmailNotContaining(anyString())).thenReturn(userModelList);
        List<HashMap<String, String>> response=userServiceImplementation.viewDisabledUser(new GetRequestParam("dummy",-1,1,"userName"));
        assertNotNull(response);
    }
    @Test
    @DisplayName("Get All Test")
    void getAll() {
        UserFilter userFilter=new UserFilter();
        UserModel userModel= mock(UserModel.class);
        userFilter.setGender(new ArrayList<>(Collections.singleton("Male")));
        when(userRepo.findAll(specification.getUserFilter("dummy@nineleaps.com", userFilter))).thenReturn(new ArrayList<>(Collections.singleton(userModel)));
        MappingJacksonValue response1=userServiceImplementation.getAll(new GetAllParam("dummy",userFilter,1,2,"userName"));
        MappingJacksonValue response2=userServiceImplementation.getAll(new GetAllParam("dummy",userFilter,1,1,"userName"));
        assertAll(()->assertNotNull(response1),
                ()->assertNotNull(response2));
    }

    @Test
    @DisplayName("Filter Info Test")
    void getFilterInfo() {
        UserModel userModel= mock(UserModel.class);
        when(userRepo.findAll()).thenReturn(new ArrayList<>(Collections.singleton(userModel)));
        Object response=userServiceImplementation.getFilterInfo();
        assertNotNull(response  );
    }
}