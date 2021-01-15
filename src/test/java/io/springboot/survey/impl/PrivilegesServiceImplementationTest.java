package io.springboot.survey.impl;

import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.models.PrivilegesModel;
import io.springboot.survey.models.RoleModel;
import io.springboot.survey.repository.PrivilegesRepo;
import io.springboot.survey.repository.RoleRepo;
import io.springboot.survey.request.PrivilegesRequest;
import io.springboot.survey.response.PrivilegeSet;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
class PrivilegesServiceImplementationTest {

    @InjectMocks
    PrivilegesServiceImplementation privilegesServiceImplementation;

    @Mock
     PrivilegesRepo privilegesRepo;

    @Mock
     RoleRepo roleRepo;


    @Nested
    @DisplayName("Get Privileges Test")
    class GetPrivilegesTest {
        @Test
        @DisplayName("True")
        void getPrivilegesTrue() {
            PrivilegesModel privilegesModel = new PrivilegesModel();
            privilegesModel.setEditRole(true);
            privilegesModel.setRoleId(1);
            privilegesModel.setViewTeam(true);
            privilegesModel.setSurveyModule(true);
            privilegesModel.setTakeSurvey(true);
            privilegesModel.setTeamManagement(true);
            privilegesModel.setTemplateReport(true);
            privilegesModel.setTemplateModule(true);
            privilegesModel.setEmployeeManagement(true);
            List<String> response = privilegesServiceImplementation.getPrivileges(privilegesModel);
            assertNotNull(response);
        }

        @Test
        @DisplayName("False")
        void getPrivilegesFalse() {
            PrivilegesModel privilegesModel=new PrivilegesModel();
            privilegesModel.setEditRole(false);
            privilegesModel.setRoleId(1);
            privilegesModel.setViewTeam(false);
            privilegesModel.setSurveyModule(false);
            privilegesModel.setTakeSurvey(false);
            privilegesModel.setTeamManagement(false);
            privilegesModel.setTemplateReport(false);
            privilegesModel.setTemplateModule(false);
            privilegesModel.setEmployeeManagement(false);
            List<String> response = privilegesServiceImplementation.getPrivileges(privilegesModel);
            assertNotNull(response);
        }
    }


    @Nested
   class MapPrivilegesTest {
       @Test
       @DisplayName("RoleModel Not Null")
       void mapPrivilegesNotNull() {

           RoleModel roleModel = new RoleModel(123, "DummyData");
           PrivilegeSet privilegeSet1 = new PrivilegeSet("employeeManagement", true, "Employee Management");
           PrivilegeSet privilegeSet2 = new PrivilegeSet("surveyModule", true, "Survey Module");
           PrivilegeSet privilegeSet3 = new PrivilegeSet("templateModule", true, "Template Module");
           PrivilegeSet privilegeSet4 = new PrivilegeSet("teamManagement", false, "Team Management");
           PrivilegeSet privilegeSet5 = new PrivilegeSet("takeSurvey", true, "Take Survey");
           PrivilegeSet privilegeSet6 = new PrivilegeSet("managed-teams", true, "View Team");
           PrivilegeSet privilegeSet7 = new PrivilegeSet("template-report", false, "Template Report");
           PrivilegeSet privilegeSet8 = new PrivilegeSet("editRole", false, "Edit Role");
           PrivilegeSet privilegeSet9 = new PrivilegeSet("default", false, "default Role");
           List<PrivilegeSet> privilegeList = new ArrayList<>(
                   Arrays.asList(privilegeSet1,privilegeSet2,privilegeSet3,privilegeSet4,privilegeSet5,
                           privilegeSet6,privilegeSet7,privilegeSet8,privilegeSet9));
           PrivilegesRequest privilegesRequest = new PrivilegesRequest();
           privilegesRequest.setRoleName("DummyRole");
           privilegesRequest.setPrivileges(privilegeList);
           when(roleRepo.findByRole(Mockito.anyString())).thenReturn(roleModel);
           ResponseEntity<ResponseMessage> message = privilegesServiceImplementation.mapPrivileges(privilegesRequest);
           assertEquals(HttpStatus.OK, message.getStatusCode());
       }
       @Test
       @DisplayName("RoleModel Null")
       void mapPrivilegesNull() {
           PrivilegesRequest privilegesRequest = Mockito.mock(PrivilegesRequest.class);
           when(roleRepo.findByRole(null)).thenReturn(null);
           Exception exception= assertThrows(ResourceNotFoundException.class,()->privilegesServiceImplementation.mapPrivileges(privilegesRequest));
           assertNotNull(exception);

       }
   }
    @Test
    @DisplayName("AllPrivileges")
    void showAllPrivileges() {
       PrivilegesModel privilegesModel=Mockito.mock(PrivilegesModel.class);
        when(privilegesRepo.findAll()).thenReturn(new ArrayList<>(Collections.singletonList(privilegesModel)));
        List<PrivilegesRequest> result=privilegesServiceImplementation.showAllPrivileges();
        assertNotNull(result);

    }

   @Nested
   class ShowPrivilegesTest {
       @Test
       @DisplayName("Role Model Not Null")
       void showPrivilegesNotNull() {
           RoleModel roleModel = Mockito.mock(RoleModel.class);
           PrivilegesModel privilegesModel =Mockito.mock(PrivilegesModel.class);
           when(roleRepo.findByRole(Mockito.anyString())).thenReturn(roleModel);
           when(privilegesRepo.findByRoleId(Mockito.anyInt())).thenReturn(privilegesModel);
           when(roleRepo.getRoleByRoleId(Mockito.anyInt())).thenReturn("DummyData");
           PrivilegesRequest response = privilegesServiceImplementation.showPrivileges("DummyData");
           assertNotNull(response);
       }
       @Test
       @DisplayName("Role Model Null")
       void showPrivilegesNull() {
           when(roleRepo.findByRole(Mockito.anyString())).thenReturn(null);
           Throwable exception = assertThrows(ResourceNotFoundException.class, () -> privilegesServiceImplementation.showPrivileges("Dummy"));
           assertNotNull(exception);
       }
   }
}