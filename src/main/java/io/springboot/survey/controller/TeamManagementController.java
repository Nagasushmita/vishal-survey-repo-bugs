package io.springboot.survey.controller;


import io.springboot.survey.annotation.APIResponseOk;
import io.springboot.survey.annotation.Email;
import io.springboot.survey.annotation.EmptyNotNull;
import io.springboot.survey.models.TeamModel;
import io.springboot.survey.request.AddMemberRequest;
import io.springboot.survey.request.CreateTeamRequest;
import io.springboot.survey.response.*;
import io.springboot.survey.service.TeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.EMAIL_NOT_NULL;
import static io.springboot.survey.utils.Constants.NullEmptyConstant.PAGE_NOT_NULL;
import static io.springboot.survey.utils.Constants.NullEmptyConstant.STATUS_NOT_NULL;
import static io.springboot.survey.utils.Constants.NullEmptyConstant.TEAM_NAME_NOT_NULL;
import static io.springboot.survey.utils.Constants.TeamConstants.STATUS;
import static io.springboot.survey.utils.Constants.ValidationConstant.EMAIL;
import static io.springboot.survey.utils.Constants.ValidationConstant.PAGE;
import static io.springboot.survey.utils.Constants.ValidationConstant.PAGE_SIZE_10;
import static io.springboot.survey.utils.Constants.ValidationConstant.TEAM_NAME;


@RestController
@Validated
@Api("TeamManagementController")
@RequestMapping("/surveyManagement/v1")

public class TeamManagementController {

    final TeamService teamService;
    private final Logger logger= LoggerFactory.getLogger(TeamManagementController.class.getSimpleName());

    public TeamManagementController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * Create a team
     *
     * @param createTeamRequest : CreateTeamRequest.
     * @return :  ResponseEntity<ResponseMessage> --> 201 Created.
     */
    @PostMapping(value = "/team",produces = { "application/json" },consumes ={ "application/json" })
    @ApiOperation(value = "For creating a team")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseMessage> createTeam(@Valid @RequestBody CreateTeamRequest createTeamRequest) {
       logger.info("Creating team : {}",createTeamRequest);
       return teamService.createNewTeam(createTeamRequest);
    }

    /**
     * Deleting a team
     *
     * @param teamName :name of the team.
     * @param email: email of the logged in user.
     * @return : ResponseEntity<Void> --> 204 No Content.
     */
    @DeleteMapping("/team")
    @ApiOperation(value = "For deleting a team")
    @APIResponseOk()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteTeamByTeamName(@RequestParam(name = TEAM_NAME)
                                     @EmptyNotNull(message = TEAM_NAME_NOT_NULL)
                                     String teamName, @RequestParam(name = EMAIL)
                                     @EmptyNotNull(message = EMAIL_NOT_NULL)
                                     @Email String email) {
        logger.info("Fetching and deleting team {} with email {}",teamName,email);
       return teamService.deleteTeamByTeamName(teamName,email);
    }

    /**
     * For Showing all the team created by a particular user
     *
     * @param email : email of the logged in user.
     * @param page : current page number.
     * @param pageSize : number of object per page defaultSize-->10
     * @return : List<TemplateModel>
     */
    @GetMapping(value = "/teams",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For Showing all the team created by a particular user")
    public MappingJacksonValue showCreated(@RequestParam(name = EMAIL)
                                           @EmptyNotNull(message = EMAIL_NOT_NULL) @Email String email,
                                           @RequestParam(name = PAGE)
                                           @NotNull(message = PAGE_NOT_NULL) Integer page,
                                           @RequestParam(defaultValue = PAGE_SIZE_10, required = false) Integer pageSize) {

        logger.info("Fetching all the team created by user with email {}",email);
        return teamService.getCreatedTeam(email,page,pageSize);
    }


    /**
     * For Showing all the teams managed by a particular user
     *
     * @param email : email of the logged in user.
     * @param page : current page number.
     * @param pageSize : number of object per page defaultSize-->10
     * @return : List<TemplateModel>
     */
    @GetMapping(value = "/managed-teams",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For Showing all the teams managed by a particular user")
    public MappingJacksonValue showManagedTeam(@RequestParam(name = EMAIL)
                                               @EmptyNotNull(message = EMAIL_NOT_NULL) @Email String email,
                                               @RequestParam(name = PAGE)
                                               @NotNull(message = PAGE_NOT_NULL) Integer page,
                                               @RequestParam(defaultValue = PAGE_SIZE_10, required = false) Integer pageSize) {
        logger.info("Fetching all the team managed by user with email {}", email);
        return teamService.getManagedTeam(email, page, pageSize);
    }

    /**
     * For deleting a member from a team
     *
     * @param teamName :name of team.
     * @param email : email of the member to be deleted from the team.
     * @return : ResponseEntity<Void> --> 204 - No Content.
     */
    @DeleteMapping("/team/members")
    @APIResponseOk()
    @ApiOperation(value = "For deleting a member from a team")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteByUserId(@RequestParam(name = TEAM_NAME)
                                               @EmptyNotNull(message = TEAM_NAME_NOT_NULL) String teamName,
                                               @RequestParam(name = EMAIL)
                                               @EmptyNotNull(message = EMAIL_NOT_NULL)
                                               @Email String email) {
        logger.info("Fetching and deleting team member with email {} and team {}",email,teamName);
        return teamService.deleteTeamMember(teamName,email);
    }

    /**
     * Add members to an existing team
     *
     * @param addMemberRequest :AddMemberRequest
     * @return : ResponseEntity<ResponseMessage>
     */
    @PutMapping(value ="/team/members",produces = { "application/json" },consumes ={ "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "To add members to an existing team.")
    public ResponseEntity<ResponseMessage> addMember(@Valid @RequestBody AddMemberRequest addMemberRequest){

        logger.info("Adding team member : {}",addMemberRequest);
        return teamService.addMembers(addMemberRequest);
    }

    /**
     * For getting information about all the team an user belongs to
     *
     * @param email : email of the logged in user.
     * @param page : current page number.
     * @param pageSize : number of object per page defaultSize--> 10.
     * @return List<HashMap<String, String>>
     */
    @GetMapping(value ="/user/teams",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For getting information about all the team an user belongs to")
    public MappingJacksonValue getTeamInfo(@RequestParam(name = EMAIL)
                                           @EmptyNotNull(message = EMAIL_NOT_NULL) String email,
                                           @RequestParam(name = PAGE)
                                           @NotNull(message = PAGE_NOT_NULL) Integer page,
                                           @RequestParam(defaultValue = PAGE_SIZE_10, required = false) Integer pageSize) {
        logger.info("Fetching all the team of user with email {}", email);
        return teamService.getTeamInfo(email,page,pageSize);
    }

    /**
     * For Viewing a particular team created
     *
     * @param teamName : name of the team.
     * @return List<Member>
     */
    @GetMapping(value ="/team-members",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For Viewing a particular team created ")
    public List<Member> getTeamMembers(@RequestParam(name = TEAM_NAME)
                                       @EmptyNotNull(message = TEAM_NAME_NOT_NULL) String teamName) {
        logger.info("List<Member>Fetching team details of {}", teamName);
        return teamService.getTeamMembers(teamName);
    }

    /**
     * For getting information about all the team in the organisation
     *
     * @return  List<TeamModel>
     */
   //SurveyModule
    @GetMapping(value ="/survey/teams/all",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For getting information about all the team in the organisation")
    public List<TeamModel> getAllTeam() {
        logger.info("Fetching details of all the teams");
        return teamService.getAllTeams();
    }


    /**
     * For updating the project status of a team
     *
     * @param email : email of the logged in user.
     * @param teamName : name of the team.
     * @param status : new status of the team.
     * @return  ResponseEntity<ResponseMessage>
     */
    @PutMapping(value ="/team/update",produces = { "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "For updating the project status of a team")
    public ResponseEntity<ResponseMessage> updateProjectStatus(@RequestParam(name = EMAIL) @EmptyNotNull(message= EMAIL_NOT_NULL)
                                                              @Email String email, @RequestParam(name = TEAM_NAME)
                                                              @EmptyNotNull(message = TEAM_NAME_NOT_NULL) String teamName,
                                                               @RequestParam(name = STATUS)
                                                               @EmptyNotNull(message = STATUS_NOT_NULL) String status) {
        logger.info("Updating status of team {} created by user with email {}",teamName,email);
        return teamService.updateProjectStatus(email,teamName,status);
    }

    /**
     * @param email : email of the user(manager/creator).
     * @param userFilter : UserFilter.
     * @return :  List<TeamModel>
     */
    //Survey
        @PostMapping(value ="/survey/teams/filter",produces = { "application/json" },consumes = { "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "For filtering the team its name,status")
    public List<TeamModel> teamFilter(@RequestParam(name = EMAIL)
                                      @EmptyNotNull(message = EMAIL_NOT_NULL) String email,
                                      @RequestBody UserFilter userFilter) {
        logger.info("Filtering team by filter : {}",userFilter);
        return teamService.teamFilter(email, userFilter);
    }

}