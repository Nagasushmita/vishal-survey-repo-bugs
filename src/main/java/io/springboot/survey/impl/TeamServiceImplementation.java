package io.springboot.survey.impl;

import io.springboot.survey.exception.APIException;
import io.springboot.survey.exception.BadRequestException;
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
import io.springboot.survey.response.PaginationResponse;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.response.UserFilter;
import io.springboot.survey.service.TeamService;
import io.springboot.survey.specification.SpecificationService;
import io.springboot.survey.utils.DynamicFiltering;
import io.springboot.survey.utils.Pagination;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import javax.persistence.Tuple;
import java.util.*;

import static io.springboot.survey.utils.Constants.ApiResponseConstant.BAD_REQUEST;
import static io.springboot.survey.utils.Constants.ApiResponseConstant.INTERNAL_SERVER_ERROR;
import static io.springboot.survey.utils.Constants.CommonConstant.PAGE_REQUIRED;
import static io.springboot.survey.utils.Constants.CommonConstant.TEAM_COUNT;
import static io.springboot.survey.utils.Constants.ErrorMessageConstant.ADDED;
import static io.springboot.survey.utils.Constants.ErrorMessageConstant.EMAIL_RESPONSE;
import static io.springboot.survey.utils.Constants.FilterConstants.PAGINATION_FILTER;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.TeamConstants.*;
import static io.springboot.survey.utils.Constants.ValidationConstant.*;

@Component
public class TeamServiceImplementation implements TeamService {

    private final TeamRepo teamRepo;
    private final TeamMemberRepo teamMemberRepo;
    private final UserRepo userRepo;
    private final SpecificationService specificationService;
    private static final Logger logger= LoggerFactory.getLogger(TeamServiceImplementation.class.getSimpleName());

    public TeamServiceImplementation(TeamRepo teamRepo, TeamMemberRepo teamMemberRepo, UserRepo userRepo, SpecificationService specificationService) {
        this.teamRepo = teamRepo;
        this.teamMemberRepo = teamMemberRepo;
        this.userRepo = userRepo;
        this.specificationService = specificationService;
    }

    /**
     * Create a team
     *
     * @param createTeamRequest :CreateTeamRequest
     * @return : ResponseEntity<ResponseMessage>
     */
    @Override
    public ResponseEntity<ResponseMessage> createNewTeam(CreateTeamRequest createTeamRequest) {
        logger.info(STARTING_METHOD_EXECUTION);
        String teamName = createTeamRequest.getTeamName();
        List<String> emailList = createTeamRequest.getEmailList();
        if (teamRepo.findByTeamName(teamName) == null) {
            TeamModel model = new TeamModel();
            model.setTeamName(teamName);
            model.setUserId(userRepo.getUserIdByUserEmail(createTeamRequest.getCreatorEmail()));
            model.setManagerId(userRepo.getUserIdByUserEmail(createTeamRequest.getManagerEmail()));
            model.setStatus(createTeamRequest.getStatus());
            model.setProjectName(createTeamRequest.getProjectName());
            model.setCreatedOn(System.currentTimeMillis());
            teamRepo.save(model);
            logger.debug("Team created :{}",model);
            for (String member : emailList) {
                TeamMemberModel memberModel = new TeamMemberModel();
                memberModel.setTeamId(teamRepo.getTeamIdByNameAndId(teamName,userRepo.getUserIdByUserEmail(createTeamRequest.getCreatorEmail())));
                memberModel.setUserId(userRepo.getUserIdByUserEmail(member));
                teamMemberRepo.save(memberModel);
                logger.debug("TeamMember created :{}",memberModel);
            }
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(TEAM_CREATED);
            responseMessage.setStatusCode(HttpStatus.CREATED.value());
            logger.info(EXITING_METHOD_EXECUTION);

            return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
        } else {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(TEAM_ALREADY_CREATED);
            responseMessage.setStatusCode(HttpStatus.CONFLICT.value());
            logger.debug("Team {} already exist",createTeamRequest.getTeamName());
            logger.info(EXITING_METHOD_EXECUTION);
            return new ResponseEntity<>(responseMessage, HttpStatus.CONFLICT);
        }
    }

    /**
     * Delete team
     *
     * @param teamName : teamName.
     * @param email : email of logged in user.
     * @return : ResponseEntity<Void>
     * @throws BadRequestException : if teamRepo.deleteByTeamNameAndUserId() does not return 1.
     * @throws ResourceNotFoundException: if  teamRepo.getTeamIdByNameAndId() returns null.
     */
    @Override
    public ResponseEntity<Void> deleteTeamByTeamName(String teamName, String email)  {
        logger.info(STARTING_METHOD_EXECUTION);
        try {
            Integer teamModel = teamRepo.getTeamIdByNameAndId(teamName, userRepo.getUserIdByUserEmail(email));
            if (teamModel !=null) {
                long r = teamRepo.deleteByTeamNameAndUserId(teamName,
                        userRepo.getUserIdByUserEmail(email));
                if (r != 1) {
                    logger.info(EXITING_METHOD_EXECUTION);
                    throw new BadRequestException(BAD_REQUEST);
                }
                return ResponseEntity.noContent().build();
            } else {
                logger.debug(TEAM_NOT_FOUND_DEBUG,teamName);
                logger.info(EXITING_METHOD_EXECUTION);
                throw new ResourceNotFoundException(TEAM_NOT_FOUND);
            }
        } catch (Exception ex)
        {
            logger.error(DELETE_TEAM_ERROR,ex);
            throw new APIException(DELETE_TEAM_ERROR);
        }
    }


    /**
     * Return all the team created by a particular user
     *
     * @param email : email of logged in user.
     * @param page : current page number.
     * @param pageSize : number of object per page.
     * @return List<TeamModel>.
     * @throws ResourceNotFoundException : if teamRepo.findByUserId () returns empty list.
     */
    @Override
    public MappingJacksonValue getCreatedTeam(String email, Integer page, Integer pageSize) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<TeamModel>list= teamRepo.findByUserId(userRepo.getUserIdByUserEmail(email));
        if(list.isEmpty()) {
            logger.debug("No created team of user with email {} found",email);
            logger.info(EXITING_METHOD_EXECUTION);
            throw new ResourceNotFoundException(CREATED_TEAM_NOT_FOUND);
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return getFilteredObject(getTeamPagination(page, pageSize, list));
    }

    /**
     * @param paginationResponse : PaginationResponse
     * @return : MappingJacksonValue
     */
      private MappingJacksonValue getFilteredObject(PaginationResponse paginationResponse)
      {
          logger.info(STARTING_METHOD_EXECUTION);
          Set<String> fields=new HashSet<>(Arrays.asList(TEAM_MODEL_LIST,PAGE_REQUIRED));
          DynamicFiltering dynamicFiltering=new DynamicFiltering();
          logger.info(EXITING_METHOD_EXECUTION);
          return dynamicFiltering.dynamicObjectFiltering(paginationResponse,fields,PAGINATION_FILTER);
      }

    /**
     * Return all the team managed by a particular user
     *
     * @param email : email of logged in user.
     * @param page : current page number.
     * @param pageSize : number of object per page.
     * @return List<TeamModel>.
     * @throws ResourceNotFoundException : if teamRepo.findByManagerId () returns empty list.
     */
    @Override
    public MappingJacksonValue getManagedTeam(String email, Integer page, Integer pageSize) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<TeamModel> list= teamRepo.findByManagerId(userRepo.getUserIdByUserEmail(email));
        if (list.isEmpty()) {
            logger.debug("No managed team of user with email {} found",email);
            logger.info(EXITING_METHOD_EXECUTION);
            throw new ResourceNotFoundException(MANAGED_TEAM_NOT_FOUND);
        }
       else {
            logger.info(EXITING_METHOD_EXECUTION);
            return getFilteredObject(getTeamPagination(page, pageSize, list));
        }
    }

    /**
     * Pagination for team
     *
     * @param page : current page number.
     * @param pageSize : number of object per page.
     * @param list : List<TeamModel>
     * @return : PaginationResponse
     */
    @NotNull
    private PaginationResponse getTeamPagination(Integer page, Integer pageSize, @NotNull List<TeamModel> list) {
        logger.info(STARTING_METHOD_EXECUTION);
        PaginationResponse paginationResponse = new PaginationResponse();
        Pagination pagination = new Pagination();
        int d = ((list.size()) % pageSize);
        int q = ((list.size()) / pageSize);
        paginationResponse.setPageRequired(((d == 0) ? q : q + 1));
        paginationResponse.setTeamModelList(pagination.surveyPagination(list,page,pageSize));
        logger.info(EXITING_METHOD_EXECUTION);
        return paginationResponse;
    }

    /**
     * For deleting a member from a team
     *
     * @param teamName : teamName.
     * @param email : email of member to be deleted.
     * @return : ResponseEntity<Void> -- No Content 204.
     * @throws  ResourceNotFoundException : if  teamRepo.findByTeamName() returns null.
     */
    @Override
    public ResponseEntity<Void> deleteTeamMember(String teamName, String email) {
        logger.info(STARTING_METHOD_EXECUTION);
       TeamModel teamModel= teamRepo.findByTeamName(teamName);
       if (teamModel != null) {
           try {
              teamMemberRepo.deleteByUserIdAndTeamId((userRepo.getUserIdByUserEmail(email)),
                      teamRepo.getCreatorUserId(teamName));
           }
           catch (Exception ex)
           {
               logger.error("Error occurred while deleting teamMember :: ",ex);
               throw new APIException(INTERNAL_SERVER_ERROR);
           }
           teamModel.setUpdatedOn(System.currentTimeMillis());
           logger.info(EXITING_METHOD_EXECUTION);
           return ResponseEntity.noContent().build();
       }
        logger.debug(TEAM_NOT_FOUND_DEBUG,teamName);
        logger.info(EXITING_METHOD_EXECUTION);
        throw new ResourceNotFoundException(TEAM_NOT_FOUND);
    }

    /**
     * Return all the information of a particular team
     * @param teamName : teamName.
     * @return List<Member>.
     * @throws ResourceNotFoundException : if teamRepo.getTeamMemberByTeamName() returns empty list.
     * @throws ResourceNotFoundException : if teamRepo.findByTeamName() returns null.
     */
    @Override
    public List<Member> getTeamMembers(String teamName){
        logger.info(STARTING_METHOD_EXECUTION);
        List<Member> teamMembers = new ArrayList<>();
        if(teamRepo.findByTeamName(teamName)!=null) {
        List<Integer> members = teamRepo.getTeamMemberByTeamName(teamName);
        if (!members.isEmpty()) {
            for (Integer member : members) {
                Member memberObj = new Member();
                UserModel user=userRepo.findByUserId(member);
                Tuple creator=userRepo.getUserNameAndUserEmail(teamRepo.getCreatorUserId(teamName));
                Tuple manager=userRepo.getUserNameAndUserEmail(teamRepo.getManagerUserId(teamName));
                memberObj.setName(user.getUserName());
                memberObj.setTeamName(teamName);
                memberObj.setEmail(user.getUserEmail());
                memberObj.setOrgId(user.getOrgId());
                memberObj.setCreatorName((String) creator.get(0));
                memberObj.setCreatorEmail((String) creator.get(1));
                memberObj.setManagerName((String) manager.get(0));
                memberObj.setManagerEmail((String) manager.get(1));
                memberObj.setDesignation(user.getDesignation());
                memberObj.setGender(user.getGender());
                teamMembers.add(memberObj);
            }
            logger.info(EXITING_METHOD_EXECUTION);
            return teamMembers;
        } else {
            logger.debug("Team member for team {} not found",teamName);
            logger.info(EXITING_METHOD_EXECUTION);
            throw new ResourceNotFoundException(TEAM_MEMBER_NOT_FOUND);
        }
        }
            logger.debug(TEAM_NOT_FOUND_DEBUG,teamName);
            logger.info(EXITING_METHOD_EXECUTION);
            throw new ResourceNotFoundException(TEAM_NOT_FOUND);
    }

    /**
     * Condition while adding members
     *
     * @param notAdded : email of not added members.
     * @return :  ResponseEntity<ResponseMessage>
     */
    private ResponseEntity<ResponseMessage> addMemberCondition(@NotNull List<String> notAdded)
    {
        logger.info(STARTING_METHOD_EXECUTION);
        ResponseMessage responseMessage = new ResponseMessage();
        if (notAdded.isEmpty())
        {
            responseMessage.setMessage(ADDED);
            responseMessage.setStatusCode(HttpStatus.OK.value());
        }
        else
        {
            String listString = String.join(", ", notAdded);
            responseMessage.setMessage(EMAIL_RESPONSE + listString + ALREADY_EXIST_TEAM);
            logger.debug("Member with email {} already exist in team",listString);
            responseMessage.setStatusCode(HttpStatus.CONFLICT.value());
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);

    }

    /**
     * Add member(s) to team
     *
     * @param addMemberRequest : AddMemberRequest
     * @return : ResponseEntity<ResponseMessage>
     * @throws ResourceNotFoundException : if teamRepo.getTeamIdByNameAndId() returns null.
     */
    @Override
    public ResponseEntity<ResponseMessage> addMembers(AddMemberRequest addMemberRequest) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<String> notAdded = new ArrayList<>();
        Integer teamId = teamRepo.getTeamIdByNameAndId(addMemberRequest.getTeamName(),
                userRepo.getUserIdByUserEmail(addMemberRequest.getCreatorEmail()));
        if (teamId!= null) {
            for (String member : addMemberRequest.getMemberList()) {
                if (teamMemberRepo.findByTeamIdAndAndUserId(teamId, userRepo.getUserIdByUserEmail(member)) == null) {
                    TeamMemberModel teamMemberModel = new TeamMemberModel();
                    teamMemberModel.setTeamId(teamId);
                    teamMemberModel.setUserId(userRepo.getUserIdByUserEmail(member));
                    teamMemberRepo.save(teamMemberModel);
                } else {
                    notAdded.add(member);
                }
            }
            logger.info(EXITING_METHOD_EXECUTION);
            return addMemberCondition(notAdded);
        } else
            logger.debug(TEAM_NOT_FOUND_DEBUG,addMemberRequest.getTeamName());
             logger.info(EXITING_METHOD_EXECUTION);
            throw new ResourceNotFoundException(TEAM_NOT_FOUND);

    }

    /**
     * Return information about all the team an user belongs to
     *
     * @param email : email of logged in user.
     * @param page : current page number.
     * @param pageSize : number of object per page.
     * @return :  List<HashMap<String, String>>
     * @throws ResourceNotFoundException : if  teamMemberRepo.findByUserId() returns empty list.
     */
    @Override
    public MappingJacksonValue getTeamInfo(String email, int page, Integer pageSize) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<Integer> teamIds = teamMemberRepo.findByUserId(userRepo.getUserIdByUserEmail(email));
        if(teamIds.isEmpty()) {
            logger.debug("No team found for user with email : {}",email);
            logger.info(EXITING_METHOD_EXECUTION);
            throw new ResourceNotFoundException(TEAM_NOT_FOUND);
        }
        List<HashMap<String,String>> maps=new ArrayList<>();
        List<TeamModel> teamModels = new ArrayList<>();
        for (Integer teamId : teamIds) {
            teamModels.add(teamRepo.findByTeamId(teamId));
        }
        for (TeamModel teamModel:teamModels){
            HashMap<String,String> map=new HashMap<>();
            Tuple creator=userRepo.getUserNameAndUserEmail(teamModel.getUserId());
            Tuple manager=userRepo.getUserNameAndUserEmail(teamModel.getManagerId());
            map.put(TEAM_NAME,teamModel.getTeamName());
            map.put(CREATOR_NAME, (String) creator.get(0));
            map.put(CREATOR_EMAIL, (String) creator.get(1));
            map.put(MANAGER_NAME, (String) manager.get(0));
            map.put(MANAGER_EMAIL, (String) manager.get(1));
            map.put(TEAM_COUNT,Integer.toString(teamIds.size()));
            map.put(MEMBER_COUNT, Integer.toString(teamMemberRepo.getCountByTeamId(teamModel.getTeamId())));
            map.put(PROJECT_NAME,teamModel.getProjectName());
            map.put(PROJECT_STATUS,teamModel.getStatus());
            maps.add(map);
        }
        Pagination pagination= new Pagination();
        int d = ((teamModels.size()) % pageSize);
        int q = ((teamModels.size()) / pageSize);
        PaginationResponse paginationResponse= new PaginationResponse();
        paginationResponse.setHashMapList(pagination.surveyPagination(maps,page,pageSize));
        paginationResponse.setPageRequired(((d == 0) ? q : q + 1));
        Set<String> fields=new HashSet<>(Arrays.asList(HASH_MAP_LIST,PAGE_REQUIRED));
        DynamicFiltering dynamicFiltering=new DynamicFiltering();
        logger.info(EXITING_METHOD_EXECUTION);
        return dynamicFiltering.dynamicObjectFiltering(paginationResponse,fields,PAGINATION_FILTER);
    }

    /**
     * Return all the teams
     *
     * @return :List<TeamModel>
     * @throws ResourceNotFoundException :teamRepo.findAll() returns empty list.
     */
    @Override
    public List<TeamModel> getAllTeams() {
        logger.info(STARTING_METHOD_EXECUTION);
        List<TeamModel> allTeams=teamRepo.findAll();
        if(allTeams.isEmpty()) {
            logger.debug("No team found");
            logger.info(EXITING_METHOD_EXECUTION);
            throw new ResourceNotFoundException(TEAM_NOT_FOUND);
        }
        else {
            logger.info(EXITING_METHOD_EXECUTION);
            return allTeams;
        }
    }

    /**
     *  For updating the project status of a team
     *
     * @param email : email of logged in user.
     * @param teamName : name of team.
     * @param status : status of team
     * @return : ResponseEntity<ResponseMessage>
     */
    @Override
    public ResponseEntity<ResponseMessage> updateProjectStatus(String email, String teamName, String status){
        logger.info(STARTING_METHOD_EXECUTION);
        ResponseMessage responseMessage =new ResponseMessage();
            TeamModel teamModel = teamRepo.findByTeamNameAndUserId(teamName,
                    userRepo.getUserIdByUserEmail(email));
            if(teamModel!=null) {
                teamModel.setStatus(status);
                teamModel.setUpdatedOn(System.currentTimeMillis());
                teamRepo.save(teamModel);
                logger.debug("Team updated : {}",teamModel);
                responseMessage.setStatusCode(HttpStatus.OK.value());
                responseMessage.setMessage(STATUS_UPDATED);
                logger.info(EXITING_METHOD_EXECUTION);
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            }
                logger.debug("Team {} not found",teamName);
                logger.info(EXITING_METHOD_EXECUTION);
                throw new ResourceNotFoundException(TEAM_NOT_FOUND);

    }

    /**
     * Details about  teamFilter
     *
     * @param email : email of logged in user.
     * @param userFilter : UserFilter
     * @return : List<TeamModel>
     */
    @Override
    public List<TeamModel> teamFilter(String email,UserFilter userFilter) {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return teamRepo.findAll(specificationService.getTeamFilter(email,userFilter));

    }
}



