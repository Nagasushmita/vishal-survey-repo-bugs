package io.springboot.survey.impl;

import io.springboot.survey.exception.BadRequestException;
import io.springboot.survey.exception.ForbiddenException;
import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.models.RoleModel;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.RoleRepo;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.response.ResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.springboot.survey.utils.Constants.ErrorMessageConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
class RoleServiceImplementationTest {

    @InjectMocks
    RoleServiceImplementation roleServiceImplementation;

    @Mock
    RoleRepo roleRepo;
    @Mock
    UserRepo userRepo;

    @Test
    @DisplayName("All Roles")
    void getAllRoles() {
        List<RoleModel> result=roleServiceImplementation.getAllRoles();
        assertNotNull(result);
    }
    @Nested
    @DisplayName("Add Role Test")
    class AddRoleTest {
        @Test
        @DisplayName("RoleModel Null")
        void addRoleWhenRoleModelNull() {
            ResponseEntity<ResponseMessage> responseEntity = roleServiceImplementation.addRole("HR","dummy@nineleaps.com" );
            assertThat(responseEntity.getStatusCodeValue()).isEqualTo(201);
        }
        @Test
        @DisplayName("RoleModel Not Null")
        void addRoleWhenRoleModelNotNull() {
            RoleModel roleModel = Mockito.mock(RoleModel.class);
            when(roleRepo.findByRole(Mockito.anyString())).thenReturn(roleModel);
            ResponseEntity<ResponseMessage> responseEntity = roleServiceImplementation.addRole("DummyDate","dummy@nineleaps.com" );
            assertThat(responseEntity.getStatusCodeValue()).isEqualTo(409);
        }
    }

    @Nested
    @DisplayName("Delete Role Test")
    class DeleteRoleTest {
        @Test
        @DisplayName("Forbidden")
        void deleteRoleForbidden() {
            Exception exception1 = assertThrows(ForbiddenException.class,()->roleServiceImplementation.deleteRole("HR"));
            Exception exception2 = assertThrows(ForbiddenException.class,()->roleServiceImplementation.deleteRole("Manager"));
            Exception exception3 = assertThrows(ForbiddenException.class,()->roleServiceImplementation.deleteRole("Employee"));

            assertAll(()->assertThat(exception1.getMessage()).isEqualTo(ROLE_CANNOT_BE_DELETED),
                    ()->assertThat(exception2.getMessage()).isEqualTo(ROLE_CANNOT_BE_DELETED),
                    ()->assertThat(exception3.getMessage()).isEqualTo(ROLE_CANNOT_BE_DELETED));
        }
        @Test()
        @DisplayName("Role Not Found")
        void deleteRoleNotFound() {
            when(roleRepo.findByRole(Mockito.anyString())).thenReturn(null);
            Throwable exception = assertThrows(ResourceNotFoundException.class, () -> roleServiceImplementation.deleteRole("DummyData"));
            assertEquals(ROLE_NOT_FOUND, exception.getMessage());
        }

        @Test
        @DisplayName("Delete Role Success")
        void deleteRoleWhenNotNull() {
            long i = 1;
            RoleModel roleModel = Mockito.mock(RoleModel.class);
            when(roleRepo.getRoleIdByRole(Mockito.anyString())).thenReturn(123);
            when(roleRepo.findByRole(Mockito.anyString())).thenReturn(roleModel);
            when(userRepo.findByRoleId(Mockito.anyInt())).thenReturn(getUserModelList());
            when(roleRepo.deleteByRole(Mockito.anyString())).thenReturn(i);
            ResponseEntity<Void> responseEntity = roleServiceImplementation.deleteRole("Dummy");
            assertThat(responseEntity.getStatusCodeValue()).isEqualTo(204);
        }

        @Test
        @DisplayName("Delete Role Failure")
        void deleteRoleWhenNull() {
            RoleModel roleModel = Mockito.mock(RoleModel.class);
            when(roleRepo.findByRole(Mockito.anyString())).thenReturn(roleModel);
            when(roleRepo.getRoleIdByRole(Mockito.anyString())).thenReturn(123);
            when(userRepo.findByRoleId(Mockito.anyInt())).thenReturn(getUserModelList());
            Exception exception1 = assertThrows(BadRequestException.class,()->roleServiceImplementation.deleteRole("Dummy"));
            assertThat(exception1.getMessage()).isEqualTo(ERROR_MESSAGE);
        }

    }

    private List<UserModel> getUserModelList()
    {
        UserModel userModel1=new UserModel();
        userModel1.setUserId(13);
        userModel1.setRoleId(2);
        userModel1.setUserName("First");
        UserModel userModel2=new UserModel();
        userModel2.setUserId(14);
        userModel2.setRoleId(1);
        userModel2.setUserName("Second");
        return new ArrayList<>(Arrays.asList(userModel1,userModel2));
    }

}