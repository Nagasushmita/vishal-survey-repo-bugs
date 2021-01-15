package io.springboot.survey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.springboot.survey.exception.APIException;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.request.AddMemberRequest;
import io.springboot.survey.request.CreateTeamRequest;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.response.UserFilter;
import io.springboot.survey.service.TeamService;
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

import java.util.ArrayList;
import java.util.Collections;

import static io.springboot.survey.utils.Constants.TeamConstants.TEAM_CREATED;
import static io.springboot.survey.utils.Constants.ValidationConstant.MOCK_TOKEN;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TeamManagementController.class)
@Tag("Controller")
class TeamManagementControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    AuthorizationService authorizationService;
    @MockBean
    UserRepo userRepo;
    @MockBean
    TeamService teamService;

    @BeforeEach
    void setUp() {
        UserModel userModel = Mockito.mock(UserModel.class);
        when(authorizationService.showEndpointsAction(Mockito.anyString())).thenReturn(true);
        when(authorizationService.authorizationManager(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
        when(userRepo.findByUserEmail(Mockito.anyString())).thenReturn(userModel);
    }

    @Nested
    @DisplayName("Create Team Test")
    class CreateTeam {
        @Test
        @DisplayName("Success - 201")
        void createTeam() throws Exception {
            CreateTeamRequest createTeamRequest=getTeamRequestData();
            ResponseEntity<ResponseMessage> responseMessage=new ResponseEntity<>(new ResponseMessage(HttpStatus.CREATED.value(),TEAM_CREATED),HttpStatus.CREATED);
            when(teamService.createNewTeam(Mockito.any(CreateTeamRequest.class))).thenReturn(responseMessage);
            mvc.perform(post("/surveyManagement/v1/team")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(createTeamRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());
            verify(teamService,times(1)).createNewTeam(Mockito.any(CreateTeamRequest.class));
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            CreateTeamRequest createTeamRequest=new CreateTeamRequest();
            mvc.perform(post("/surveyManagement/v1/team")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(createTeamRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Delete Team Test")
    class DeleteTeamTest {
        @Test
        @DisplayName("Success - 204")
        void deleteTeamByTeamName() throws Exception {
            ResponseEntity<Void> responseMessage=ResponseEntity.noContent().build();
            when(teamService.deleteTeamByTeamName(Mockito.anyString(),Mockito.anyString())).thenReturn(responseMessage);
            mvc.perform(delete("/surveyManagement/v1/team")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("teamName","dummyTeam")
                    .param("email","dummy@nineleaps.com")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
            verify(teamService,times(1)).deleteTeamByTeamName(Mockito.anyString(),Mockito.anyString());
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(delete("/surveyManagement/v1/team")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("teamName","")
                    .param("email","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Show Created Team Test")
    class CreatedTeamTest {
        @Test
        @DisplayName("Success - 200")
        void showCreated () throws Exception {
            mvc.perform(get("/surveyManagement/v1/teams")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","dummy@nineleaps.com")
                    .param("page","0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(teamService,times(1)).getCreatedTeam(Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt());
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(get("/surveyManagement/v1/teams")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","")
                    .param("page","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }
    @Nested
    @DisplayName("Managed Team Test")
    class ManagedTeamTest {
        @Test
        @DisplayName("Success - 200")
        void showManagedTeam() throws Exception {
            mvc.perform(get("/surveyManagement/v1/managed-teams")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","dummy@nineleaps.com")
                    .param("page","0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(teamService,times(1)).getManagedTeam(Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt());

        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(get("/surveyManagement/v1/managed-teams")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","")
                    .param("page","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Delete Team Member Test")
    class DeleteTeamMemberTest {
        @Test
        @DisplayName("Success - 204")
        void deleteByUserId() throws Exception {
            mvc.perform(delete("/surveyManagement/v1/team/members")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","dummy@nineleaps.com")
                    .param("teamName","dummyTeam")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
            verify(teamService,times(1)).deleteTeamMember(Mockito.anyString(),Mockito.anyString());

        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(delete("/surveyManagement/v1/team/members")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","")
                    .param("teamName","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Add Member Test")
    class AddMemberTest {
        @Test
        @DisplayName("Success - 200")
        void addMember() throws Exception {
            AddMemberRequest addMemberRequest=new AddMemberRequest();
            addMemberRequest.setTeamName("dummyTeam");
            addMemberRequest.setCreatorEmail("dummy@nineleaps.com");
            addMemberRequest.setMemberList(new ArrayList<>(Collections.singleton("test@nineleaps.com")));
            mvc.perform(put("/surveyManagement/v1/team/members")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(addMemberRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(teamService,times(1)).addMembers(Mockito.any(AddMemberRequest.class));

        }

        @Test
        @DisplayName("Invalid Data -422")
        void invalidData() throws Exception {
            AddMemberRequest addMemberRequest=new AddMemberRequest();
            mvc.perform(put("/surveyManagement/v1/team/members")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(addMemberRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Get Team Info Test")
    class GetTeamInfoTest {
        @Test
        @DisplayName("Success - 200")
        void getTeamInfo() throws Exception {
            mvc.perform(get("/surveyManagement/v1/user/teams")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","dummy@nineleaps.com")
                    .param("page","0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(teamService,times(1)).getTeamInfo(Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt());
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(get("/surveyManagement/v1/user/teams")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","")
                    .param("page","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }


    @Nested
    @DisplayName("Get Team Member Test")
    class GetTeamMembersTest {
        @Test
        @DisplayName("Success - 200")
        void getTeamMembers() throws Exception {
            mvc.perform(get("/surveyManagement/v1/team-members")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("teamName","dummyTeam")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(teamService,times(1)).getTeamMembers(Mockito.anyString());
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(get("/surveyManagement/v1/team-members")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("teamName","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Test
    void getAllTeam() throws Exception {
        mvc.perform(get("/surveyManagement/v1/io.springboot.survey/teams/all")
                .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(teamService,times(1)).getAllTeams();
    }

    @Nested
    @DisplayName("Update Project Status Test")
    class UpdateProjectStatusTest {
        @Test
        @DisplayName("Success - 200")
        void updateProjectStatus() throws Exception {
            mvc.perform(put("/surveyManagement/v1/team/update")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","dummy@nineleaps.com")
                    .param("status","ACTIVE")
                    .param("teamName","dummyTeam")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(teamService,times(1)).updateProjectStatus(Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(put("/surveyManagement/v1/team/update")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","")
                    .param("status","")
                    .param("teamName","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }


    @Nested
    @DisplayName("Team Filter Test")
    class TeamFilterTest {
        @Test
        @DisplayName("Success - 200")
        void teamFilter() throws Exception {
            UserFilter userFilter=new UserFilter();
            mvc.perform(post("/surveyManagement/v1/io.springboot.survey/teams/filter")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","dummy@nineleaps.com")
                    .content(objectMapper.writeValueAsString(userFilter))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(teamService,times(1)).teamFilter(Mockito.anyString(),Mockito.any(UserFilter.class));

        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            UserFilter userFilter=new UserFilter();
            mvc.perform(post("/surveyManagement/v1/io.springboot.survey/teams/filter")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","")
                    .content(objectMapper.writeValueAsString(userFilter))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());

        }
    }

    @Test
    @DisplayName("API Exception Test")
    void apiException() throws Exception {
        when(teamService.deleteTeamByTeamName(Mockito.anyString(),Mockito.anyString())).thenThrow(APIException.class);
        mvc.perform(delete("/surveyManagement/v1/team")
                .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                .param("teamName","dummyTeam")
                .param("email","dummy@nineleaps.com")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

    }

    CreateTeamRequest getTeamRequestData()
    {
        CreateTeamRequest createTeamRequest=new CreateTeamRequest();
        createTeamRequest.setTeamName("dummyTeam");
        createTeamRequest.setProjectName("dummyProject");
        createTeamRequest.setCreatorEmail("test@nineleaps.com");
        createTeamRequest.setManagerEmail("dummy@nineleaps.com");
        createTeamRequest.setStatus("ACTIVE");
        createTeamRequest.setEmailList(new ArrayList<>(Collections.singletonList("test@nineleaps.com")));
        return createTeamRequest;
    }


}