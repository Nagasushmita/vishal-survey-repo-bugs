package io.springboot.survey.impl;

import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.models.SurveyModel;
import io.springboot.survey.pojo.survey.impl.DashboardPaginationParam;
import io.springboot.survey.repository.*;
import io.springboot.survey.response.StatusFilteredResponse;
import io.springboot.survey.response.StatusResponse;
import io.springboot.survey.response.SurveyPagination;
import io.springboot.survey.service.SurveyDashboardService;
import io.springboot.survey.utils.DynamicFiltering;
import io.springboot.survey.utils.Pagination;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import javax.persistence.Tuple;
import java.sql.Timestamp;
import java.util.*;

import static io.springboot.survey.utils.Constants.CommonConstant.ASSIGNED_TO;
import static io.springboot.survey.utils.Constants.CommonConstant.PAGE_REQUIRED;
import static io.springboot.survey.utils.Constants.ErrorMessageConstant.NO_SURVEYS_FOUND;
import static io.springboot.survey.utils.Constants.FilterConstants.STATUS_RESPONSE_FILTER;
import static io.springboot.survey.utils.Constants.FilterConstants.SURVEY_PAGINATION_FILTER;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.SurveyModuleConstants.*;
import static io.springboot.survey.utils.Constants.ValidationConstant.CREATION_DATE;
import static io.springboot.survey.utils.Constants.ValidationConstant.SURVEY_NAME;

@Component
public class SurveyDashboardServiceImpl implements SurveyDashboardService {

    final SurveyRepo surveyRepo;
    final UserRepo userRepo;
    final SurveyStatusRepo surveyStatusRepo;
    final TeamRepo teamRepo;
    final SurveyResponseRepo surveyResponseRepo;

    private static final Logger logger = LoggerFactory.getLogger(SurveyDashboardServiceImpl.class.getSimpleName());

    public SurveyDashboardServiceImpl(SurveyRepo surveyRepo, UserRepo userRepo, SurveyStatusRepo surveyStatusRepo, TeamRepo teamRepo, SurveyResponseRepo surveyResponseRepo) {
        this.surveyRepo = surveyRepo;
        this.userRepo = userRepo;
        this.surveyStatusRepo = surveyStatusRepo;
        this.teamRepo = teamRepo;
        this.surveyResponseRepo = surveyResponseRepo;
    }


    /**
     * Return details that are required for HR dashboard graph
     *
     * @param email : email of the logged in user.
     * @throws ResourceNotFoundException : if
     * surveyRepo.findAllByCreatorUserIdAndAndExpirationDateBetweenAndArchivedFalse() return empty list.
     * @return : List<StatusResponse>
     */
    @Override
    public MappingJacksonValue hrDashboardGraph(String email) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<StatusResponse> finalList= new ArrayList<>();
        Date date = new Date();
        Timestamp startDate = new Timestamp(date.getTime());
        Timestamp endDate = Timestamp.valueOf(startDate.toLocalDateTime().plusWeeks(2));
        List<SurveyModel> surveyModels = surveyRepo.findAllByCreatorUserIdAndAndExpirationDateBetweenAndArchivedFalse
                (userRepo.getUserIdByUserEmail(email),startDate.getTime(),endDate.getTime());
        for (SurveyModel surveyModel : surveyModels)
        {
            StatusResponse statusResponse= new StatusResponse();
            statusResponse.setSurveyName(surveyModel.getSurveyName());
            statusResponse.setCreationDate(surveyModel.getCreationDate());
            statusResponse.setExpirationDate(surveyModel.getExpirationDate());
            statusResponse.setSurveyTakenCount(surveyStatusRepo.getSizeBySurveyIdAndTaken(surveyModel.getSurveyId(), true));
            statusResponse.setSurveyPendingCount(surveyStatusRepo.getSizeBySurveyIdAndTaken(surveyModel.getSurveyId(), false));
            statusResponse.setAssignedTo(surveyStatusRepo.getStatusModelSizeById(surveyModel.getSurveyId()));
            finalList.add(statusResponse);
        }
        if (finalList.isEmpty()) {
            logger.debug("No io.springboot.survey found for user with email {}",email);
            throw new ResourceNotFoundException(NO_SURVEYS_FOUND);
        }
        finalList.sort(Comparator.comparing(StatusResponse::getExpirationDate));

        DynamicFiltering dynamicFiltering = new DynamicFiltering();
        Set<String> fields=new HashSet<>(Arrays.asList(SURVEY_NAME,CREATION_DATE,EXPIRATION_DATE,
                ASSIGNED_TO,SURVEY_PENDING_COUNT,SURVEY_TAKEN_COUNT));
        logger.info(EXITING_METHOD_EXECUTION);
        return dynamicFiltering.dynamicObjectFiltering(finalList,fields,STATUS_RESPONSE_FILTER);
    }

    /**
     * Return details that are required for the manager dashboards cards.
     *
     * @param email : email of the logged in user.
     * @return : Map<String, Integer>
     */
    @Override
    public Map<String, Integer> managerDashboard(String email) {
        logger.info(STARTING_METHOD_EXECUTION);
        Date date = new Date();
        int userId = userRepo.getUserIdByUserEmail(email);
        int size =surveyRepo.getCountByCreatedAndArchived(userId,false);
        Timestamp endDate = new Timestamp(date.getTime());
        Timestamp startDate = Timestamp.valueOf(endDate.toLocalDateTime().minusDays(7));
        Map<String, Integer> count = new HashMap<>();
        count.put(SURVEY_CREATED_BY_ME, size);
        count.put(SURVEY_THIS_WEEK_BY_ME, surveyRepo.getCountByCreatorUserIdAndCreationDate(userId, startDate, endDate));
        count.put(AVERAGE_RESPONSE_PER_SURVEY, surveyStatusRepo.getSizeByAssignedBy(userId) / ((size == 0) ? size : 1));
        count.put(TEAM_MANAGED_BY_ME,teamRepo.getSizeByManagerId(userId));
        logger.info(EXITING_METHOD_EXECUTION);
        return count;
    }

    /**
     * Return details about all the io.springboot.survey that are created in this week
     *
     * @param page : current page number.
     * @param pageSize : number of object per page.
     * @throws ResourceNotFoundException : if surveyRepo.findAllByCreationDateBetween(startDate, endDate)
     * return empty list
     * @return : List<StatusFilteredResponse>
     */
    @Override
    public MappingJacksonValue surveysInWeek(int page, Integer pageSize) {
        logger.info(STARTING_METHOD_EXECUTION);
        Date date = new Date();
        Timestamp endDate = new Timestamp(date.getTime());
        Pagination pagination = new Pagination();
        SurveyPagination surveyPagination = new SurveyPagination();
        Timestamp startDate = Timestamp.valueOf(endDate.toLocalDateTime().minusDays(7));
        List<StatusFilteredResponse> surveyData = surveyData(surveyRepo.findAllByCreationDateBetween(startDate.getTime(), endDate.getTime()));
        if(surveyData.isEmpty()) {
            logger.debug(NO_SURVEYS_FOUND);
            throw new ResourceNotFoundException(NO_SURVEYS_FOUND);
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return getSurveyPagination(new DashboardPaginationParam(page, pageSize, pagination, surveyPagination, surveyData));

    }

    /**
     * Return details about all the io.springboot.survey that are created in this week by a particular user
     *
     * @param email : email of logged in user.
     * @param page : current page number.
     * @param pageSize : number of object per page.
     * @throws ResourceNotFoundException : if surveyRepo.findAllByCreatorUserIdAndAndCreationDateBetween() return
     * empty list.
     * @return : List<StatusFilteredResponse>
     */
    @Override
    public MappingJacksonValue mySurveysInWeek(String email, Integer page, Integer pageSize) {
        logger.info(STARTING_METHOD_EXECUTION);
        Date date = new Date();
        Pagination pagination = new Pagination();
        SurveyPagination surveyPagination = new SurveyPagination();
        Timestamp endDate = new Timestamp(date.getTime());
        Timestamp startDate = Timestamp.valueOf(endDate.toLocalDateTime().minusDays(7));
        List<StatusFilteredResponse> surveyData = surveyData(surveyRepo.findAllByCreatorUserIdAndAndCreationDateBetween
                (userRepo.getUserIdByUserEmail(email), startDate.getTime(), endDate.getTime()));
        if (surveyData.isEmpty()) {
            logger.debug(NO_SURVEYS_FOUND);
            throw new ResourceNotFoundException(NO_SURVEYS_FOUND);
        }
           logger.info(EXITING_METHOD_EXECUTION);
        return getSurveyPagination(new DashboardPaginationParam(page, pageSize, pagination, surveyPagination, surveyData));

    }

    /**
     * Return details that are required for the HR dashboards cards.
     *
     * @param email : email of logged in user.
     * @return : Map<String, Integer>.
     */
    @Override
    public Map<String, Integer> hrDashboardInfo(String email) {
        logger.info(STARTING_METHOD_EXECUTION);
        Date date = new Date();
        int size = surveyRepo.getSize();
        Timestamp endDate = new Timestamp(date.getTime());
        Timestamp startDate = Timestamp.valueOf(endDate.toLocalDateTime().minusDays(7));
        Map<String, Integer> count = new HashMap<>();
        count.put(TOTAL_SURVEY_CREATED, size);
        count.put(SURVEY_CREATED_THIS_WEEK, surveyRepo.getSizeByCreationDateBetween(startDate.getTime(), endDate.getTime()));
        count.put(SURVEY_CREATED_BY_ME, surveyRepo.getCountByCreatedAndArchived(
                userRepo.getUserIdByUserEmail(email),false));
        count.put(AVERAGE_RESPONSE_PER_SURVEY, surveyResponseRepo.getSize() / ((size == 0) ? 1 : size));
        logger.info(EXITING_METHOD_EXECUTION);
        return count;
    }

    /**
     * Return a list of all the io.springboot.survey that have been created in the organisation
     *
     * @param page : current page number.
     * @param pageSize : number of object per page.
     * @return : List<StatusFilteredResponse>.
     */
    @Override
    public MappingJacksonValue totalSurveys(int page, Integer pageSize) {
        logger.info(STARTING_METHOD_EXECUTION);
        Pagination pagination = new Pagination();
        SurveyPagination surveyPagination = new SurveyPagination();
        List<StatusFilteredResponse> surveyModels = surveyData(surveyRepo.findAll());
        if (surveyModels.isEmpty()) {
            logger.debug(NO_SURVEYS_FOUND);
            throw new ResourceNotFoundException(NO_SURVEYS_FOUND);
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return getSurveyPagination(new DashboardPaginationParam(page, pageSize, pagination, surveyPagination, surveyModels));
    }

    /**
     * Return the required member variable from SurveyPagination.
     * @param param :DashboardPaginationParam
     * @return MappingJacksonValue
     */
    @NotNull
    private MappingJacksonValue getSurveyPagination(DashboardPaginationParam param) {
        logger.info(STARTING_METHOD_EXECUTION);
        param.getSurveyModels().sort(Comparator.comparing(StatusFilteredResponse::getCreationDate).reversed());
        int d = ((param.getSurveyModels().size()) % param.getPageSize());
        int q = ((param.getSurveyModels().size()) / param.getPageSize());
        param.getSurveyPagination().setResponsesList(param.getPagination().surveyPagination(param.getSurveyModels(), param.getPage(), param.getPageSize()));
        param.getSurveyPagination().setPageRequired(((d == 0) ? q : q + 2));
        logger.info(EXITING_METHOD_EXECUTION);
        return getResponseListDynamicObject(param.getSurveyPagination());
    }

    /**
     *
     * @param surveyPagination : SurveyPagination
     * @return : MappingJacksonValue
     */
    private MappingJacksonValue getResponseListDynamicObject(SurveyPagination surveyPagination)
    {   logger.info(STARTING_METHOD_EXECUTION);
        Set<String> filter = new HashSet<>(Arrays.asList(RESPONSE_LIST,PAGE_REQUIRED));
        DynamicFiltering dynamicFiltering = new DynamicFiltering();
        logger.info(EXITING_METHOD_EXECUTION);
        return dynamicFiltering.dynamicObjectFiltering(surveyPagination,filter,SURVEY_PAGINATION_FILTER);
    }


    /**
     * Return Survey Data
     *
     * @param list : List<SurveyModel>
     * @return List<StatusFilteredResponse>
     */
    private List<StatusFilteredResponse> surveyData(@NotNull List<SurveyModel> list) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<StatusFilteredResponse> info = new ArrayList<>();
        for (SurveyModel surveyModel : list) {
            StatusFilteredResponse surveyInfo = new StatusFilteredResponse();
            surveyInfo.setSurveyName(surveyModel.getSurveyName());
            Tuple user=userRepo.getUserNameAndUserEmail(surveyModel.getCreatorUserId());
            surveyInfo.setAssignedBy((String) user.get(0));
            surveyInfo.setAssignedByEmail((String) user.get(1));
            surveyInfo.setCreationDate(surveyModel.getCreationDate());
            surveyInfo.setExpirationDate(surveyModel.getExpirationDate());
            surveyInfo.setAssignedTo(surveyStatusRepo.getStatusModelSizeById(surveyModel.getSurveyId()));
            info.add(surveyInfo);
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return info;
    }


}
