package io.springboot.survey.impl;

import io.springboot.survey.exception.APIException;
import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.models.TeamMemberModel;
import io.springboot.survey.models.TeamModel;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.TeamMemberRepo;
import io.springboot.survey.repository.TeamRepo;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.request.AddMemberRequest;
import io.springboot.survey.request.CreateTeamRequest;
import io.springboot.survey.response.Member;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.response.UserFilter;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;

import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static io.springboot.survey.utils.Constants.ErrorMessageConstant.ADDED;
import static io.springboot.survey.utils.Constants.TeamConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
class TeamServiceImplementationTest {

    @InjectMocks
    TeamServiceImplementation teamServiceImplementation;

    @Mock
    UserRepo userRepo;

    @Mock
    TeamRepo teamRepo;

    @Mock
    TeamMemberRepo teamMemberRepo;
    @Mock
    SpecificationService specificationService;

    @Nested
    class CreateTeamTest {
        @Test
        @DisplayName("New Team")
        void createNewTeamNotNull() {
            CreateTeamRequest createTeamRequest =new CreateTeamRequest();
            createTeamRequest.setTeamName("testTeam");
            createTeamRequest.setCreatorEmail("creator@nineleaps.com");
            createTeamRequest.setManagerEmail("manager@nineleaps.com");
            List<String> emailList=new ArrayList<>(Collections.singleton("dummy@nineleaps.com"));
            createTeamRequest.setEmailList(emailList);
            when(userRepo.getUserIdByUserEmail(Mockito.anyString())).thenReturn(234);
            when(userRepo.getUserIdByUserEmail(Mockito.anyString())).thenReturn(124);
            when(userRepo.getUserIdByUserEmail(Mockito.anyString())).thenReturn(990);
            when(teamRepo.getTeamIdByNameAndId(Mockito.anyString(),Mockito.anyInt())).thenReturn(1);
            ResponseEntity<ResponseMessage> response=teamServiceImplementation.createNewTeam(createTeamRequest);
            assertEquals(HttpStatus.CREATED,response.getStatusCode());
        }
        @Test
        @DisplayName("Team Already Created")
        void createNewTeamNull() {
            CreateTeamRequest createTeamRequest = new CreateTeamRequest();
            createTeamRequest.setTeamName("testTeam");
            getFindByTeamName();
            ResponseEntity<ResponseMessage> response=teamServiceImplementation.createNewTeam(createTeamRequest);
            assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
        }
    }

    @Nested
    class DeleteTeamTest {
        @Test
        @DisplayName("TeamModel Not Null And Success")
        void deleteTeamNotNullSuccess() {
            long i = 1;
            when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(234);
            when(teamRepo.getTeamIdByNameAndId(Mockito.anyString(),Mockito.anyInt())).thenReturn(2);
            when(teamRepo.deleteByTeamNameAndUserId(Mockito.anyString(),Mockito.anyInt())).thenReturn(i);
            ResponseEntity<Void> response = teamServiceImplementation.deleteTeamByTeamName("testTeam", "dummy@nineleaps.com");
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        @Test
        @DisplayName("TeamModel Not Null And Failure")
        void deleteTeamNotNullFailure() {
            long i = 0;
            when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(234);
            when(teamRepo.getTeamIdByNameAndId(Mockito.anyString(),Mockito.anyInt())).thenReturn(2);
            when(teamRepo.deleteByTeamNameAndUserId(Mockito.anyString(),Mockito.anyInt())).thenReturn(i);
            Exception exception=assertThrows(APIException.class,()->teamServiceImplementation.deleteTeamByTeamName("testTeam", "dummy@nineleaps.com"));
            assertEquals(DELETE_TEAM_ERROR,exception.getMessage());
        }
        @Test
        @DisplayName("TeamModel Null")
        void deleteTeamNull() {
            when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(234);
            when(teamRepo.getTeamIdByNameAndId(Mockito.anyString(), Mockito.anyInt())).thenReturn(null);
            Exception exception = assertThrows(APIException.class, () -> teamServiceImplementation.deleteTeamByTeamName("testTeam", "dummy@nineleaps.com"));
            assertNotNull(exception);
        }
    }
    @Nested
    class CreatedTeamTest {
        @Test
        @DisplayName("Team List Not Empty")
        void getCreatedTeamNotEmpty() {
            TeamModel teamModel = Mockito.mock(TeamModel.class);
            List<TeamModel> list = new ArrayList<>(Collections.singletonList(teamModel));
            when(userRepo.getUserIdByUserEmail(Mockito.anyString())).thenReturn(124);
            when(teamRepo.findByUserId(Mockito.anyInt())).thenReturn(list);
            MappingJacksonValue response = teamServiceImplementation.getCreatedTeam("dummy@nineleaps.com", 0, 10);
            assertNotNull(response);
        }
        @Test
        @DisplayName("Team List Empty")
        void getCreatedTeamEmpty() {
            when(userRepo.getUserIdByUserEmail(Mockito.anyString())).thenReturn(124);
            when(teamRepo.findByUserId(124)).thenReturn(new ArrayList<>());
            Exception exception = assertThrows(ResourceNotFoundException.class, () -> teamServiceImplementation.getCreatedTeam("dummy@nineleaps.com", 0, 10));
            assertNotNull(exception);

        }
    }

    @Nested
    class getManagedTeamTest {
        @Test
        @DisplayName("Team List Not Empty")
        void getManagedTeamNotEmpty() {
            TeamModel teamModel = Mockito.mock(TeamModel.class);
            List<TeamModel> list = new ArrayList<>(Collections.singletonList(teamModel));
            when(userRepo.getUserIdByUserEmail(Mockito.anyString())).thenReturn(124);
            when(teamRepo.findByManagerId(Mockito.anyInt())).thenReturn(list);
            MappingJacksonValue response1 = teamServiceImplementation.getManagedTeam("dummy@nineleaps.com", 0, 10);
            MappingJacksonValue response2 = teamServiceImplementation.getManagedTeam("dummy@nineleaps.com", 1, 1);
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));
        }
        @Test
        @DisplayName("Team List Empty")
        void getCreatedTeamEmpty() {
            when(userRepo.getUserIdByUserEmail(Mockito.anyString())).thenReturn(124);
            when(teamRepo.findByManagerId(Mockito.anyInt())).thenReturn(new ArrayList<>());
            Exception exception = assertThrows(ResourceNotFoundException.class, () -> teamServiceImplementation.getManagedTeam("dummy@nineleaps.com", 1, 10));
            assertNotNull(exception);
        }
    }


    @Nested
    class DeleteTeamMember
    {
        @Test
        @DisplayName("TeamModel Not Null And Success")
        void deleteTeamMemberNotNullSuccess() {
            long i =1;
            getFindByTeamName();
            when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(1);
            when(teamRepo.getTeamId(Mockito.anyString())).thenReturn(1);
            when(teamMemberRepo.deleteByUserIdAndTeamId(Mockito.anyInt(),Mockito.anyInt())).thenReturn(i);
            ResponseEntity<Void>response=teamServiceImplementation.deleteTeamMember("testTeam","dummy@nineleaps.com");
            assertEquals(HttpStatus.NO_CONTENT,response.getStatusCode());
        }
        @Test
        @DisplayName("TeamModel Not Null And Failure")
        void deleteTeamMemberNotNullFailure() {
            getFindByTeamName();
            when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(null);
            Exception exception=assertThrows(APIException.class,()->teamServiceImplementation.deleteTeamMember("testTeam","dummy@nineleaps.com"));
            assertNotNull(exception);
        }
        @Test
        @DisplayName("TeamModel Null")
        void deleteTeamMemberNull() {
            when(teamRepo.findByTeamName("testTeam")).thenReturn(null);
            Exception exception=assertThrows(ResourceNotFoundException.class,()->teamServiceImplementation.deleteTeamMember("testTeam","dummy@nineleaps.com"));
            assertNotNull(exception);
        }
    }

    @Nested
    class GetTeamMembersTest {
        @Test
        @DisplayName("TeamModel Null")
        void teamModelNull() {
            when(teamRepo.findByTeamName("testTeam")).thenReturn(null);
            Exception exception=assertThrows(ResourceNotFoundException.class,()->teamServiceImplementation.getTeamMembers("testTeam"));
            assertAll(()->assertNotNull(exception),
                    ()->assertEquals(TEAM_NOT_FOUND,exception.getMessage()));
        }

        @Test
        @DisplayName("TeamMemberModel Null")
        void teamMemberModelNull() {
            getFindByTeamName();
            when(teamRepo.getTeamMemberByTeamName("testTeam")).thenReturn(new ArrayList<>());
            Exception exception=assertThrows(ResourceNotFoundException.class,()->teamServiceImplementation.getTeamMembers("testTeam"));
            assertAll(()->assertNotNull(exception),
                    ()->assertEquals(TEAM_MEMBER_NOT_FOUND,exception.getMessage()));
        }

        @Test
        @DisplayName("Team Model And TeamMemberModel Not Null")
        void teamModelAndTeamMemberModelNotNull() {
            UserModel userModel1=new UserModel();
            userModel1.setUserId(124);
            userModel1.setUserEmail("dummy@nineleaps.com");
            userModel1.setUserName("dummy");
            TeamMemberModel teamMemberModel=new TeamMemberModel();
            teamMemberModel.setTeamId(135);
            teamMemberModel.setUserId(124);
            getFindByTeamName();
            when(teamRepo.getCreatorUserId("testTeam")).thenReturn(234);
            when(teamRepo.getManagerUserId("testTeam")).thenReturn(990);
            when(teamRepo.getTeamMemberByTeamName("testTeam")).thenReturn(new ArrayList<>(Collections.singleton(124)));
            when(userRepo.findByUserId(124)).thenReturn(userModel1);
            when(userRepo.getUserNameAndUserEmail(234)).thenReturn(Mockito.mock(Tuple.class));
            when(userRepo.getUserNameAndUserEmail(990)).thenReturn(Mockito.mock(Tuple.class));
            List<Member> result=teamServiceImplementation.getTeamMembers("testTeam");
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("Add Member Test")
    class AddMembers {
        @Test
        @DisplayName("Add New Member")
        void addMembersNotPresent() {
            AddMemberRequest addMemberRequest =new AddMemberRequest();
            addMemberRequest.setCreatorEmail("creator@nineleaps.com");
            addMemberRequest.setTeamName("testTeam");
            List<String> emailList=new ArrayList<>(Collections.singleton("dummy@nineleaps.com"));
            addMemberRequest.setMemberList(emailList);
            when(userRepo.getUserIdByUserEmail("creator@nineleaps.com")).thenReturn(124);
            when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(234);
            when(teamRepo.getTeamIdByNameAndId("testTeam",124)).thenReturn(1);
            when(teamMemberRepo.findByTeamIdAndAndUserId(1,234)).thenReturn(null);
            ResponseEntity<ResponseMessage>response=teamServiceImplementation.addMembers(addMemberRequest);
            assertEquals(ADDED, Objects.requireNonNull(response.getBody()).getMessage());
        }
        @Test
        @DisplayName("Add Already Present Member")
        void addMembersAlreadyPresent() {
            AddMemberRequest addMemberRequest =new AddMemberRequest();
            addMemberRequest.setCreatorEmail("creator@nineleaps.com");
            addMemberRequest.setTeamName("testTeam");
            List<String> emailList=new ArrayList<>(Collections.singleton("dummy@nineleaps.com"));
            addMemberRequest.setMemberList(emailList);
            TeamMemberModel teamMemberModel=new TeamMemberModel();
            teamMemberModel.setTeamId(1);
            when(userRepo.getUserIdByUserEmail("creator@nineleaps.com")).thenReturn(124);
            when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(234);
            when(teamRepo.getTeamIdByNameAndId("testTeam",124)).thenReturn(1);
            when(teamMemberRepo.findByTeamIdAndAndUserId(1,234)).thenReturn(teamMemberModel);
            ResponseEntity<ResponseMessage>response=teamServiceImplementation.addMembers(addMemberRequest);
            assertEquals(HttpStatus.CONFLICT.value(), Objects.requireNonNull(response.getBody()).getStatusCode());
        }

        @Test
        @DisplayName("Team Model Not Null")
        void addMembersNull() {
            AddMemberRequest addMemberRequest =new AddMemberRequest();
            addMemberRequest.setCreatorEmail("creator@nineleaps.com");
            addMemberRequest.setTeamName("testTeam");
            when(userRepo.getUserIdByUserEmail("creator@nineleaps.com")).thenReturn(124);
            when(teamRepo.getTeamIdByNameAndId("testTeam",124)).thenReturn(null);
            Exception exception=assertThrows(ResourceNotFoundException.class,
                    ()->teamServiceImplementation.addMembers(addMemberRequest));
            assertNotNull(exception);
        }

    }
    @Nested
    class GetTeamInfoTest {
        @Test
        @DisplayName("TeamMemberModel List Not Empty")
        void getTeamInfoNotNull() {
            TeamMemberModel teamMemberModel=new TeamMemberModel();
            teamMemberModel.setTeamId(1);
            teamMemberModel.setUserId(124);
            TeamModel teamModel=new TeamModel();
            teamModel.setTeamId(1);
            teamModel.setUserId(10);
            teamModel.setManagerId(11);
            teamModel.setTeamName("testTeam");
            when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(124);
            when(userRepo.getUserNameAndUserEmail(10)).thenReturn(Mockito.mock(Tuple.class));
            when(userRepo.getUserNameAndUserEmail(11)).thenReturn(Mockito.mock(Tuple.class));
            when(teamMemberRepo.findByUserId(124)).thenReturn(new ArrayList<>(Collections.singleton(1)));
            when(teamRepo.findByTeamId(1)).thenReturn(teamModel);
            MappingJacksonValue response1=teamServiceImplementation.getTeamInfo("dummy@nineleaps.com",1,10);
            MappingJacksonValue response2=teamServiceImplementation.getTeamInfo("dummy@nineleaps.com",1,1);
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));
        }
        @Test
        @DisplayName("TeamMemberModel List Empty")
        void getTeamInfoNull() {
            when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(124);
            when(teamMemberRepo.findByUserId(124)).thenReturn(new ArrayList<>());
            Exception exception=assertThrows(ResourceNotFoundException.class,
                    ()->teamServiceImplementation.getTeamInfo("dummy@nineleaps.com",1,10));
            assertNotNull(exception);
        }
    }

    @Nested
    class GetAllTeamTest {
        @Test
        @DisplayName("TeamModel List Not Empty")
        void getAllTeamsNotNull() {
            TeamModel teamModel = new TeamModel();
            teamModel.setTeamName("testTeam");
            teamModel.setTeamId(135);
            List<TeamModel> teamModelList=new ArrayList<>(Collections.singleton(teamModel));
            when(teamRepo.findAll()).thenReturn(teamModelList);
            List<TeamModel> result=teamServiceImplementation.getAllTeams();
            assertNotNull(result);
        }
        @Test
        @DisplayName("TeamModel List Empty")
        void getAllTeamsNull() {
            when(teamRepo.findAll()).thenReturn(new ArrayList<>());
            Exception exception=assertThrows(ResourceNotFoundException.class,()->teamServiceImplementation.getAllTeams());
            assertAll(()-> assertNotNull(exception),
                    ()->assertEquals(TEAM_NOT_FOUND,exception.getMessage()));
        }
    }

    @Nested
    class updateProjectStatus {
        @Test
        @DisplayName("TeamModel Not Null")
        void updateProjectStatusNotNull() {
            when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(124);
            getTeamNameAndUserId();
            ResponseEntity<ResponseMessage> response=teamServiceImplementation.updateProjectStatus("dummy@nineleaps.com","testTeam","ACTIVE");
            assertAll(()->assertNotNull(response),
                    ()->assertEquals(HttpStatus.OK,response.getStatusCode()));
        }
        @Test
        @DisplayName("TeamModel Null")
        void updateProjectStatusNull() {
            when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(124);
            when(teamRepo.findByTeamNameAndUserId("testTeam", 124)).thenReturn(null);
            Exception exception=assertThrows(ResourceNotFoundException.class,()->teamServiceImplementation.updateProjectStatus("dummy@nineleaps.com","testTeam","ACTIVE"));
            assertAll(()-> assertNotNull(exception),
                    ()->assertEquals(TEAM_NOT_FOUND,exception.getMessage()));

        }
    }

    @Test
    @DisplayName("Team Filter Test")
    void teamFilter() {
        TeamModel teamModel=Mockito.mock(TeamModel.class);
        UserFilter userFilter= new UserFilter();
        userFilter.setProject(new ArrayList<>(Collections.singletonList("First")));
        userFilter.setStatus(new ArrayList<>(Collections.singletonList("ACTIVE")));
        when(teamRepo.findAll(specificationService.getTeamFilter("dummy@nineleaps.com",userFilter)))
                .thenReturn(new ArrayList<>(Collections.singleton(teamModel)));
        List<TeamModel> response=teamServiceImplementation.teamFilter("dummy@nineleaps.com",userFilter);
        assertNotNull(response);

    }

    private void getTeamNameAndUserId()
    {
        TeamModel teamModel = new TeamModel();
        teamModel.setTeamName("testTeam");
        teamModel.setTeamId(1);
        teamModel.setManagerId(124);
        when(teamRepo.findByTeamNameAndUserId("testTeam", 124)).thenReturn(teamModel);

    }

    private void getFindByTeamName()
    {
        TeamModel teamModel =Mockito.mock(TeamModel.class);
        teamModel.setTeamName("testTeam");
        teamModel.setTeamId(135);
        teamModel.setManagerId(990);
        teamModel.setUserId(234);
        when(teamRepo.findByTeamName("testTeam")).thenReturn(teamModel);
    }


}
