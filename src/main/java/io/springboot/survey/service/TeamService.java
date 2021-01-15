package io.springboot.survey.service;

import io.springboot.survey.models.TeamModel;
import io.springboot.survey.request.AddMemberRequest;
import io.springboot.survey.request.CreateTeamRequest;
import io.springboot.survey.response.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TeamService {
     MappingJacksonValue getTeamInfo(String email, int page, Integer pageSize);
     ResponseEntity<ResponseMessage> createNewTeam(CreateTeamRequest createTeamRequest);
     ResponseEntity<Void> deleteTeamByTeamName(String teamName, String email);
     MappingJacksonValue getCreatedTeam(String email, Integer page, Integer pageSize);
     MappingJacksonValue getManagedTeam(String email, Integer page, Integer pageSize);
     ResponseEntity<Void>  deleteTeamMember(String teamName, String email);
     ResponseEntity<ResponseMessage> addMembers(AddMemberRequest addMemberRequest);
     List<TeamModel> getAllTeams();
     ResponseEntity<ResponseMessage> updateProjectStatus(String email, String teamName, String status);
     List<TeamModel> teamFilter(String email, UserFilter userFilter);
     List<Member> getTeamMembers(String teamName);
}

