package io.springboot.survey.impl;

import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.mapper.SurveyByNameAndCreatorId;
import io.springboot.survey.mapper.SurveyStatusDto;
import io.springboot.survey.mapper.SurveyTakenDto;
import io.springboot.survey.models.SurveyModel;
import io.springboot.survey.models.SurveyStatusModel;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.pojo.survey.controller.GetSurveyInfoParam;
import io.springboot.survey.pojo.survey.controller.SurveyInfoParam;
import io.springboot.survey.pojo.survey.impl.FindByCreatorUserIdParam;
import io.springboot.survey.pojo.survey.impl.PaginationParam;
import io.springboot.survey.pojo.survey.impl.PendingTakenParam;
import io.springboot.survey.pojo.survey.impl.SurveyPaginationParam;
import io.springboot.survey.repository.*;
import io.springboot.survey.response.*;
import io.springboot.survey.service.SurveyStatusService;
import io.springboot.survey.utils.DynamicFiltering;
import io.springboot.survey.utils.Pagination;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import javax.persistence.Tuple;
import java.util.*;

import static io.springboot.survey.utils.Constants.CommonConstant.N_A;
import static io.springboot.survey.utils.Constants.CommonConstant.PAGE_REQUIRED;
import static io.springboot.survey.utils.Constants.ErrorMessageConstant.*;
import static io.springboot.survey.utils.Constants.FilterConstants.STATUS_RESPONSE_FILTER;
import static io.springboot.survey.utils.Constants.FilterConstants.SURVEY_PAGINATION_FILTER;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.SurveyModuleConstants.*;
import static io.springboot.survey.utils.Constants.ValidationConstant.*;

@Component
public  class  SurveyStatusServiceImpl implements SurveyStatusService {

    private final UserRepo userRepo;
    private final SurveyRepo surveyRepo;
    private final QuestionRepo questionRepo;
    private final SurveyStatusRepo surveyStatusRepo;
    private final SurveyResponseRepo surveyResponseRepo;
    private final TemplateRepo templateRepo;
    private final RoleRepo roleRepo;

    private static final Logger logger = LoggerFactory.getLogger(SurveyStatusServiceImpl.class.getSimpleName());
    public SurveyStatusServiceImpl(UserRepo userRepo, SurveyRepo surveyRepo, QuestionRepo questionRepo, SurveyStatusRepo surveyStatusRepo, SurveyResponseRepo surveyResponseRepo, TemplateRepo templateRepo, RoleRepo roleRepo) {
        this.userRepo = userRepo;
        this.surveyRepo = surveyRepo;
        this.questionRepo = questionRepo;
        this.surveyStatusRepo = surveyStatusRepo;
        this.surveyResponseRepo = surveyResponseRepo;
        this.templateRepo = templateRepo;
        this.roleRepo = roleRepo;
    }


    /**
     * Return info about all the io.springboot.survey taken by an user
     *
     * @param email : email of logged in user.
     * @param page : current page number.
     * @param pageSize : number of object per page.
     * @return :  List<StatusFilteredResponse>
     * @throws ResourceNotFoundException : if userRepo.getSurveyStatusModel() returns empty list.
     */
    @Override
    public MappingJacksonValue getSurveyTakenInfo(String email, int page, Integer pageSize) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<StatusFilteredResponse> responses = new ArrayList<>();
        List<SurveyStatusModel> model = userRepo.getSurveyStatusModel(email,true);
        if (model.isEmpty()) {
            logger.debug("No io.springboot.survey taken by user with email : {}",email);
            logger.info(EXITING_METHOD_EXECUTION);
            throw new ResourceNotFoundException(NO_TAKEN_SURVEY);
        }
        for (SurveyStatusModel surveyStatusModel : model) {
            StatusFilteredResponse statusResponse = new StatusFilteredResponse();
            SurveyTakenDto surveyTakenDto=userRepo.getSurveyInfo
                    (surveyStatusModel.getSurveyId(),surveyStatusModel.getAssignedBy(),surveyStatusModel.getUserId());
            statusResponse.setAssignedBy(surveyTakenDto.getUserName());
            statusResponse.setAssignedByEmail(surveyTakenDto.getUserEmail());
            statusResponse.setSurveyName(surveyTakenDto.getSurveyName());
            statusResponse.setCreationDate(surveyTakenDto.getCreationDate());
            statusResponse.setTimestamp(surveyTakenDto.getResponseDate());
            statusResponse.setNoOfQuestion(surveyTakenDto.getNoOfQuestion());
            if (surveyStatusModel.getTeamId() != -1) {
                statusResponse.setTeamName(userRepo.getTeamNameByTeamId(surveyStatusModel.getTeamId()));
            } else {
                statusResponse.setTeamName(N_A);
            }
            responses.add(statusResponse);
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return getResponseListDynamicObject(getSurveyPagination(new SurveyPaginationParam(page, pageSize, responses, model)));
    }

    /**
     * @param surveyPagination : SurveyPagination
     * @return MappingJacksonValue
     */
    private MappingJacksonValue getResponseListDynamicObject(SurveyPagination surveyPagination)
    {
        logger.info(STARTING_METHOD_EXECUTION);
        Set<String> filter = new HashSet<>(Arrays.asList(RESPONSE_LIST,PAGE_REQUIRED));
        DynamicFiltering dynamicFiltering = new DynamicFiltering();
        logger.info(EXITING_METHOD_EXECUTION);
        return dynamicFiltering.dynamicObjectFiltering(surveyPagination,filter,SURVEY_PAGINATION_FILTER);
    }

    /**
     * Survey Pagination
     * @param surveyPaginationParam :SurveyPaginationParam
     * @return  SurveyPagination
     */
    @NotNull
    private SurveyPagination getSurveyPagination(SurveyPaginationParam surveyPaginationParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        Pagination pagination = new Pagination();
        int d = ((surveyPaginationParam.getModel().size()) / surveyPaginationParam.getPageSize());
        int q = ((surveyPaginationParam.getModel().size()) % surveyPaginationParam.getPageSize());
        SurveyPagination surveyPagination = new SurveyPagination();
        surveyPagination.setResponsesList(pagination.surveyPagination(surveyPaginationParam.getResponses(), surveyPaginationParam.getPage(), surveyPaginationParam.getPageSize()));
        surveyPagination.setPageRequired(((d == 0) ? q : q + 1));
        logger.info(EXITING_METHOD_EXECUTION);
        return surveyPagination;
    }

    /**
     * Return info about all the io.springboot.survey yet to be taken by an user
     *
     * @param email : email of logged in user.
     * @param page : current page number.
     * @param pageSize: number of object per page.
     * @return : List<StatusFilteredResponse>
     */
    @Override
    public MappingJacksonValue getSurveyPendingInfo(String email, int page, Integer pageSize) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<SurveyStatusModel> model = userRepo.getSurveyStatusModel(email, false);
        if(model.isEmpty()) {
            logger.debug("No io.springboot.survey pending for user with email : {}",email);
            logger.info(EXITING_METHOD_EXECUTION);
            throw new ResourceNotFoundException(NO_PENDING_SURVEY);
        }
        Pagination pagination = new Pagination();
        SurveyPagination surveyPagination = new SurveyPagination();
        int d = ((model.size()) % pageSize);
        int q = ((model.size()) / pageSize);
        surveyPagination.setPageRequired(((d == 0) ? q : q + 1));
        surveyPagination.setResponsesList(pagination.surveyPagination(pendingSurveyDetails(email), page, pageSize));
        logger.info(EXITING_METHOD_EXECUTION);
        return getResponseListDynamicObject(surveyPagination);
    }

    /**
     * Survey details
     *
     * @param email : email of logged in user.
     * @return List<StatusFilteredResponse>
     */
    private List<StatusFilteredResponse> pendingSurveyDetails(String email) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<StatusFilteredResponse> responses = new ArrayList<>();
        List<SurveyStatusModel> model = userRepo.getSurveyStatusModel(email, false);
        List<Map<String, Integer>> surveyLists = new ArrayList<>();
        for (SurveyStatusModel surveyStatusModel : model) {
            Map<String, Integer> surveyList = new HashMap<>();
            surveyList.put(SURVEY_ID, surveyStatusModel.getSurveyId());
            surveyList.put(TEAM_ID, surveyStatusModel.getTeamId());
            surveyLists.add(surveyList);
        }
        for (Map<String, Integer> survey : surveyLists) {
            SurveyModel surveyModel = surveyRepo.findBySurveyId(survey.get(SURVEY_ID));
            StatusFilteredResponse statusResponse = new StatusFilteredResponse();
            Tuple user=userRepo.getUserNameAndUserEmail(surveyModel.getCreatorUserId());
            statusResponse.setAssignedBy((String) user.get(1));
            statusResponse.setAssignedByEmail((String) user.get(0));
            statusResponse.setSurveyName(surveyModel.getSurveyName());
            statusResponse.setSurveyDescription(surveyModel.getSurveyDesc());
            statusResponse.setCreationDate(surveyModel.getCreationDate());
            statusResponse.setNoOfQuestion(questionRepo.getSizeBySurveyId(surveyModel.getSurveyId()));
            statusResponse.setExpirationDate(surveyModel.getExpirationDate());
            if (survey.get(TEAM_ID) != -1) {
                statusResponse.setTeamName(userRepo.getTeamNameByTeamId(survey.get(TEAM_ID)));
            } else {
                statusResponse.setTeamName(N_A);
            }
            responses.add(statusResponse);
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return responses;
    }

    /**
     * Return info about all the io.springboot.survey created by a particular user
     *
     * @param surveyInfoParam:GetSurveyInfoParam
     * @return : List<StatusFilteredResponse>
     * @throws ResourceNotFoundException : if surveyRepo.getCountByCreatedAndArchived() returns 0.
     */
    @Override
    public MappingJacksonValue getSurveyInfo(GetSurveyInfoParam surveyInfoParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<StatusFilteredResponse> responses = new ArrayList<>();
        int userId = userRepo.getUserIdByUserEmail(surveyInfoParam.getCreatorEmail());
        List<SurveyModel> models = findByCreatorUserId(new FindByCreatorUserIdParam(userId, surveyInfoParam.getPage(), surveyInfoParam.getPageSize(), surveyInfoParam.getSortBy(),false));
        Integer modelListSize=surveyRepo.getCountByCreatedAndArchived(userId,false);
        if(modelListSize.equals(0)) {
            logger.debug("Not io.springboot.survey found by user with userId : {}",userId);
            logger.info(EXITING_METHOD_EXECUTION);
            throw new ResourceNotFoundException(NO_SURVEYS_FOUND);
        }
        for (SurveyModel surveyModel : models) {
            StatusFilteredResponse statusResponse = new StatusFilteredResponse();
            statusResponse.setSurveyName(surveyModel.getSurveyName());
            statusResponse.setCreationDate(surveyModel.getCreationDate());
            statusResponse.setAssignedTo(surveyStatusRepo.getStatusModelSizeById(surveyModel.getSurveyId()));
            statusResponse.setExpirationDate(surveyModel.getExpirationDate());
            statusResponse.setSurveyDescription(surveyModel.getSurveyDesc());
            statusResponse.setSurveyPendingCount(
                    surveyStatusRepo.getSizeBySurveyIdAndTaken(surveyModel.getSurveyId(), false));
            statusResponse.setSurveyTakenCount(
                    surveyStatusRepo.getSizeBySurveyIdAndTaken(surveyModel.getSurveyId(), true));
            responses.add(statusResponse);
        }
        responses.sort(Comparator.comparing(StatusFilteredResponse::getCreationDate).reversed());
        SurveyPagination surveyPagination = new SurveyPagination();
        int d = ((modelListSize) % surveyInfoParam.getPageSize());
        int q = ((modelListSize) / surveyInfoParam.getPageSize());
        surveyPagination.setResponsesList(responses);
        surveyPagination.setPageRequired(((d == 0) ? q : q + 1));
        logger.info(EXITING_METHOD_EXECUTION);
        return getResponseListDynamicObject(surveyPagination);
    }

    /**
     * Return info about taken/pending user for  io.springboot.survey.
     *
     * @param surveyId : surveyId
     * @param surveyStatusModels : List<SurveyStatusDto>
     * @param isPending : pending/taken --> boolean.
     * @return : List<PendingTakenResponse>
     */
    private   List<PendingTakenResponse> pendingTakenResponseList(int surveyId, List<SurveyStatusDto> surveyStatusModels, boolean isPending) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<PendingTakenResponse> responseList = new ArrayList<>();
        for (SurveyStatusDto model : surveyStatusModels) {
            PendingTakenResponse response = new PendingTakenResponse();
            Tuple user = userRepo.getUserNameAndUserEmail(model.getUserId());
            response.setUserName((String) user.get(0));
            int teamId = model.getTeamId();
            if (!isPending) {
                if (teamId != -1) {
                    response.setTimestamp((Long) surveyResponseRepo.getResponseIdAndTimestamp(
                            model.getUserId(), surveyId, model.getTeamId())[0]);
                } else {
                    response.setTimestamp((Long) surveyResponseRepo.getResponseIdAndTimestamp(
                            model.getUserId(), surveyId, -1)[0]);
                }
            }
            if (teamId != -1) {
                response.setTeamName(userRepo.getTeamNameByTeamId(teamId));
            } else
                response.setTeamName(N_A);

            response.setEmail((String) user.get(1));
            response.setOrgId((String) user.get(2));
            response.setExpirationDate((Long) userRepo.getSurveyData(surveyId)[1]);
            responseList.add(response);
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return responseList;
    }

    /**
     * Return info about pending users for a particular io.springboot.survey
     * @param surveyInfoParam:SurveyInfoParam
     * @return  List<PendingTakenResponse>
     * @throws  ResourceNotFoundException : if surveyStatusRepo.getSurveyByIdAndTaken() returns empty list.
     * @throws ResourceNotFoundException : if  userRepo.getSurveyDataByNameAndId() returns null.
     */
    @Override
    public MappingJacksonValue getSurveyInfoPending(SurveyInfoParam surveyInfoParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        if (userRepo.getSurveyDataByNameAndId(surveyInfoParam.getSurveyName(),surveyInfoParam.getCreatorEmail(),false) != null) {
            int surveyId = userRepo.getSurveyDataByNameAndId(surveyInfoParam.getSurveyName(),surveyInfoParam.getCreatorEmail(),false).getSurveyId();
            List<SurveyStatusDto> surveyStatusModels = surveyStatusRepo.getSurveyByIdAndTaken(surveyId, false);
            if(surveyStatusModels.isEmpty()) {
                logger.debug("No pending user for io.springboot.survey : {}",surveyInfoParam.getSurveyName());
                logger.info(EXITING_METHOD_EXECUTION);
                throw new ResourceNotFoundException(NO_PENDING_USER_SURVEY);
            }
            logger.info(EXITING_METHOD_EXECUTION);
            return getPendingTakenResponse(new PendingTakenParam(surveyInfoParam.getPage(), surveyInfoParam.getPageSize(), surveyId, surveyStatusModels));
        }
        logger.debug(SURVEY_NOT_FOUND_DEBUG,surveyInfoParam.getSurveyName());
        logger.info(EXITING_METHOD_EXECUTION);
        throw new ResourceNotFoundException(SURVEY_NOT_FOUND);
    }

    /**
     *
     * @param pendingTakenParam :PendingTakenParam
     * @return MappingJacksonValue
     *
     */
    @NotNull
    private MappingJacksonValue getPendingTakenResponse(PendingTakenParam pendingTakenParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<PendingTakenResponse> responseList = pendingTakenResponseList(pendingTakenParam.getSurveyId(), pendingTakenParam.getSurveyStatusModels(), true);
        logger.info(EXITING_METHOD_EXECUTION);
        return getPagination(new PaginationParam(pendingTakenParam.getPage(), pendingTakenParam.getPageSize(), pendingTakenParam.getSurveyStatusModels(), responseList));
    }

    /**
     * Pagination
     * @param paginationParam :PaginationParam
     * @return MappingJacksonValue
     */
    @NotNull
    private MappingJacksonValue getPagination(PaginationParam paginationParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        Pagination pagination = new Pagination();
        int d = ((paginationParam.getSurveyStatusModels().size()) % paginationParam.getPageSize());
        int q = ((paginationParam.getSurveyStatusModels().size()) / paginationParam.getPageSize());
        SurveyPagination surveyPagination = new SurveyPagination();
        surveyPagination.setPendingTakenResponses(pagination.surveyPagination(paginationParam.getResponseList(), paginationParam.getPage(), paginationParam.getPageSize()));
        surveyPagination.setPageRequired(((d == 0) ? q : q + 1));
        DynamicFiltering dynamicFiltering = new DynamicFiltering();
        Set<String> filter= new HashSet<>(Arrays.asList(PENDING_TAKEN_RESPONSE,PAGE_REQUIRED));
        logger.info(EXITING_METHOD_EXECUTION);
        return dynamicFiltering.dynamicObjectFiltering(surveyPagination,filter,SURVEY_PAGINATION_FILTER);
    }

    /**
     * Return info about the user(s) who have taken a particular io.springboot.survey
     *
     * @param surveyInfoParam:SurveyInfoParam
     * @return  List<PendingTakenResponse>
     * @throws ResourceNotFoundException : if surveyStatusRepo.getSurveyByIdAndTaken() returns empty list.
     * @throws ResourceNotFoundException : if userRepo.getSurveyDataByNameAndId() returns null.
     */
    @Override
    public MappingJacksonValue getSurveyInfoTaken(SurveyInfoParam surveyInfoParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        if (userRepo.getSurveyDataByNameAndId(surveyInfoParam.getSurveyName(), surveyInfoParam.getCreatorEmail(),false)!= null) {
            int surveyId = userRepo.getSurveyDataByNameAndId(surveyInfoParam.getSurveyName(), surveyInfoParam.getCreatorEmail(),false).getSurveyId();
            List<SurveyStatusDto> surveyStatusModels = surveyStatusRepo.getSurveyByIdAndTaken(surveyId, true);
            if(surveyStatusModels.isEmpty()) {
                logger.debug("No taken user for io.springboot.survey : {}",surveyInfoParam.getSurveyName());
                logger.info(EXITING_METHOD_EXECUTION);
                throw new ResourceNotFoundException(NO_TAKEN_USER_SURVEY);
            }
            List<PendingTakenResponse> responseList = pendingTakenResponseList(surveyId, surveyStatusModels, false);
            return getPagination(new PaginationParam(surveyInfoParam.getPage(), surveyInfoParam.getPageSize(), surveyStatusModels, responseList));
        }
        logger.debug(SURVEY_NOT_FOUND_DEBUG,surveyInfoParam.getSurveyName());
        logger.info(EXITING_METHOD_EXECUTION);
        throw new ResourceNotFoundException(SURVEY_NOT_FOUND);
    }

    /**
     * Return info about the users assigned with a particular io.springboot.survey
     * @param surveyInfoParam:SurveyInfoParam
     * @return  List<PendingTakenResponse>
     * @throws ResourceNotFoundException : if userRepo.getSurveyById() returns empty list.
     * @throws ResourceNotFoundException : if userRepo.getSurveyDataByNameAndId() returns null.
     */
    @Override
    public MappingJacksonValue getSurveyInfoAssigned(SurveyInfoParam surveyInfoParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        SurveyByNameAndCreatorId survey= userRepo.getSurveyDataByNameAndId(surveyInfoParam.getSurveyName(),surveyInfoParam.getCreatorEmail(),false);
        if (survey!= null) {
            int surveyId = survey.getSurveyId();
            List<SurveyStatusDto> surveyStatusModels = userRepo.getSurveyById(surveyId);
            if(surveyStatusModels.isEmpty()) {
                logger.debug("No assigned user for io.springboot.survey : {}",surveyInfoParam.getSurveyName());
                logger.info(EXITING_METHOD_EXECUTION);
                throw new ResourceNotFoundException(NO_ASSIGNED_USER_SURVEY);
            }
            return getPendingTakenResponse(new PendingTakenParam(surveyInfoParam.getPage(), surveyInfoParam.getPageSize(), surveyId, surveyStatusModels));
        }
        logger.debug(SURVEY_NOT_FOUND_DEBUG,surveyInfoParam.getSurveyName());
        logger.info(EXITING_METHOD_EXECUTION);
        throw new ResourceNotFoundException(SURVEY_NOT_FOUND);
    }

    /**
     * Returns all the information for a io.springboot.survey
     *
     * @param surveyName : name of the io.springboot.survey.
     * @param creatorEmail : email of logged in user.
     * @return statusResponse.
     */
    @Override
    public MappingJacksonValue surveyInformation(String surveyName, String creatorEmail) {
        logger.info(STARTING_METHOD_EXECUTION);
        int userId=userRepo.getUserIdByUserEmail(creatorEmail);
        if(userRepo.getSurveyDataByNameAndId(surveyName,creatorEmail,false)==null)
            throw new ResourceNotFoundException(SURVEY_NOT_FOUND);
        int surveyId = userRepo.getSurveyDataByNameAndId(surveyName,creatorEmail,false).getSurveyId();
        SurveyModel surveyModel = surveyRepo.findBySurveyNameAndCreatorUserIdAndArchivedFalse(surveyName, userId);
        List<EmployeeDetails> employeeDetails = new ArrayList<>();
        List<SurveyStatusDto> surveyStatusModels = userRepo.getSurveyById(surveyId);
        for (SurveyStatusDto surveyStatusModel : surveyStatusModels) {
            EmployeeDetails employeeDetails1 = new EmployeeDetails();
            UserModel userModel= userRepo.findByUserId(surveyStatusModel.getUserId());
            employeeDetails1.setName(userModel.getUserName());
            employeeDetails1.setEmail(userModel.getUserEmail());
            employeeDetails1.setDesignation(userModel.getDesignation());
            employeeDetails1.setEmpId(userModel.getOrgId());
            employeeDetails.add(employeeDetails1);
        }
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setSurveyName(surveyName);
        if (!surveyModel.getTemplateId().equals(1)) {
            statusResponse.setTemplateName(templateRepo.getTemplateNameById(surveyModel.getTemplateId()));
        }
        statusResponse.setCreationDate(surveyModel.getCreationDate());
        statusResponse.setExpirationDate(surveyModel.getExpirationDate());
        statusResponse.setSurveyDescription(surveyModel.getSurveyDesc());
        statusResponse.setSurveyPendingCount(surveyStatusRepo.getSizeBySurveyIdAndTaken(surveyModel.getSurveyId(), false));
        statusResponse.setSurveyTakenCount(surveyStatusRepo.getSizeBySurveyIdAndTaken(surveyModel.getSurveyId(), true));
        statusResponse.setNoOfQuestion(questionRepo.getSizeBySurveyId(surveyId));
        statusResponse.setEmployeeDetails(employeeDetails);
        DynamicFiltering dynamicFiltering= new DynamicFiltering();
        Set<String> fields= new HashSet<>(Arrays.asList(CREATION_DATE,SURVEY_NAME,EXPIRATION_DATE,
                SURVEY_DESCRIPTION,SURVEY_PENDING_COUNT,NUMBER_OF_QUESTION,EMPLOYEE_DETAILS,TEMPLATE_NAME,SURVEY_TAKEN_COUNT));
        logger.info(EXITING_METHOD_EXECUTION);
        return dynamicFiltering.dynamicObjectFiltering(statusResponse,fields,STATUS_RESPONSE_FILTER);
    }

    /**
     * Return info about all the active io.springboot.survey(s) for an user
     *
     * @param email : email of logged in user.
     * @return : List<StatusFilteredResponse>
     * @throws ResourceNotFoundException : if  List<StatusFilteredResponse> is empty.
     */
    @Override
    public List<StatusFilteredResponse> getActiveSurvey(String email) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<StatusFilteredResponse> surveyPendingInfo = pendingSurveyDetails(email);
        long currentTime = System.currentTimeMillis();
        surveyPendingInfo.removeIf(statusResponse -> currentTime > statusResponse.getExpirationDate());
        if(surveyPendingInfo.isEmpty()) {
            logger.debug("No active io.springboot.survey for user with email : {}",email);
            logger.info(EXITING_METHOD_EXECUTION);
            throw new ResourceNotFoundException(NO_ACTIVE_SURVEY);
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return surveyPendingInfo;
    }


    /**
     * Return info about the all the assigned io.springboot.survey for a user
     *
     * @param email : email of logged in user.
     * @param page : current page number.
     * @param pageSize : number of object per page.
     * @return : List<StatusFilteredResponse>
     * @throws ResourceNotFoundException : if serRepo.getSurveyStatusModelByEmail() returns empty list.
     */
    @Override
    public MappingJacksonValue totalAssignedSurveys(String email, int page, Integer pageSize)
    {
        logger.info(STARTING_METHOD_EXECUTION);
        List<StatusFilteredResponse> info = new ArrayList<>();
        List<SurveyStatusModel> list = userRepo.getSurveyStatusModelByEmail(email);
        if(list.isEmpty()) {
            logger.debug("No assigned io.springboot.survey for user with email : {}",email);
            logger.info(EXITING_METHOD_EXECUTION);
            throw new ResourceNotFoundException(NO_SURVEYS_FOUND);
        }
        for (SurveyStatusModel surveyStatusModel : list) {
            StatusFilteredResponse surveyInfo = new StatusFilteredResponse();
            int surveyId = surveyStatusModel.getSurveyId();
            Object[] surveyModel= userRepo.getSurveyData(surveyId);
            surveyInfo.setSurveyName((String) surveyModel[2]);
            Object [] user=userRepo.getUserNameAndEmailBySurveyId(surveyId);
            surveyInfo.setAssignedBy((String) user[0]);
            surveyInfo.setAssignedByEmail((String) user[1]);
            surveyInfo.setCreationDate((Long) surveyModel[0]);
            surveyInfo.setExpirationDate((Long) surveyModel[1]);
            surveyInfo.setAssignedTo(surveyStatusRepo.getStatusModelSizeById(surveyId));
            surveyInfo.setTaken(surveyStatusModel.getTaken());
            if (surveyStatusModel.getTeamId() != -1) {
                surveyInfo.setTeamName(userRepo.getTeamNameByTeamId(surveyStatusModel.getTeamId()));
            } else {
                surveyInfo.setTeamName(N_A);
            }
            info.add(surveyInfo);
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return getResponseListDynamicObject(getSurveyPagination(new SurveyPaginationParam(page, pageSize, info, list)));
    }

    /**
     * Return information such as io.springboot.survey taken,pending of a particular user
     * @param email : email of logged in user.
     * @return : List<AssigneeInformationResponse>
     */
    @Override
    public List<AssigneeInformationResponse> surveyAssigneeInfo(String email) {
        logger.info(STARTING_METHOD_EXECUTION);
        int userId = userRepo.getUserIdByUserEmail(email);
        Set<Integer> assignedByUserId =surveyStatusRepo.getAssignedByUserId(userId);
        Set<String> roles = new HashSet<>();
        for (Integer id : assignedByUserId) {
            roles.add(roleRepo.getRoleByRoleId(userRepo.getRoleIdByUserId(id)));
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return informationResponses(roles, assignedByUserId, userId);

    }

    /**
     * @param roles :  Set<String>
     * @param integerSet : List<Integer> assignedByUserId
     * @param userId : userId
     * @return : List<AssigneeInformationResponse>
     */
  private  List<AssigneeInformationResponse> informationResponses(@NotNull Set<String> roles, Set<Integer> integerSet, int userId) {
      logger.info(STARTING_METHOD_EXECUTION);
        List<AssigneeInformationResponse> assigneeInformationResponses = new ArrayList<>();
        for (String role : roles) {
            int totalCount = 0;
            int pendingCount = 0;
            int takenCount = 0;
            AssigneeInformationResponse assigneeInformationResponse = new AssigneeInformationResponse();
            assigneeInformationResponse.setRole(role);
            for (Integer integer : integerSet) {
                totalCount += surveyStatusRepo.getSizeByUserIdAndAssignedBy(userId, integer);
                pendingCount += surveyStatusRepo.getSizeByUserIdAndAssignedByAndTaken(userId, integer,false);
                takenCount += surveyStatusRepo.getSizeByUserIdAndAssignedByAndTaken(userId, integer,true);
             }
            assigneeInformationResponse.setPendingCount(pendingCount);
            assigneeInformationResponse.setTakenCount(takenCount);
            assigneeInformationResponse.setTotalCount(totalCount);
            assigneeInformationResponses.add(assigneeInformationResponse);
        }
        assigneeInformationResponses.sort(Comparator.comparing(AssigneeInformationResponse::getTotalCount).reversed());
        logger.info(EXITING_METHOD_EXECUTION);
        return assigneeInformationResponses;
    }


    /**
     * Return list of io.springboot.survey(s) created by an user.
     * @param param :FindByCreatorUserIdParam
     * @return List<SurveyModel>
     */
    private List<SurveyModel> findByCreatorUserId(FindByCreatorUserIdParam param) {
        logger.info(STARTING_METHOD_EXECUTION);
        if (param.getPage() < 0)
            param.setPage(0);
        PageRequest paging = PageRequest.of(param.getPage(), param.getPageSize(), Sort.by(param.getSortBy()).descending());
        logger.info(EXITING_METHOD_EXECUTION);
        return surveyRepo.findByCreatorUserIdAndArchived(param.getUserId(), paging, false).getContent();

    }

}
