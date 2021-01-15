package io.springboot.survey.impl;

import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.mapper.SurveyStatusDto;
import io.springboot.survey.models.AnswerModel;
import io.springboot.survey.models.SurveyResponseModel;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.pojo.report.TeamDateFilterParam;
import io.springboot.survey.pojo.report.TeamFilterSwitchParam;
import io.springboot.survey.pojo.report.TeamReportAnswerParam;
import io.springboot.survey.pojo.report.TeamReportParam;
import io.springboot.survey.repository.*;
import io.springboot.survey.response.*;
import io.springboot.survey.service.SurveyTeamReportService;
import io.springboot.survey.utils.SpecificationModel;
import io.springboot.survey.utils.SurveyData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static io.springboot.survey.utils.Constants.CommonConstant.QUES_TYPE_FILE;
import static io.springboot.survey.utils.Constants.CommonConstant.QUES_TYPE_TEXT;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.SurveyModuleConstants.*;


@Component
public class SurveyTeamReportServiceImpl implements SurveyTeamReportService {

    final SurveyResponseRepo surveyResponseRepo;
    final QuestionTypeRepo quesTypeRepo;
    final AnswerRepo answerRepo;
    final QuestionRepo questionRepo;
    final ResponseRepo responseRepo;
    final UserRepo userRepo;
    final SpecificationModel specificationModel;
    private int total;
    private static final Logger logger= LoggerFactory.getLogger(SurveyTeamReportServiceImpl.class.getSimpleName());
    public SurveyTeamReportServiceImpl(SurveyResponseRepo surveyResponseRepo, QuestionTypeRepo quesTypeRepo, AnswerRepo answerRepo, QuestionRepo questionRepo, ResponseRepo responseRepo, UserRepo userRepo, SpecificationModel specificationModel) {
        this.surveyResponseRepo = surveyResponseRepo;
        this.quesTypeRepo = quesTypeRepo;
        this.answerRepo = answerRepo;
        this.questionRepo = questionRepo;
        this.responseRepo = responseRepo;
        this.userRepo = userRepo;
        this.specificationModel = specificationModel;
    }

    /**
     * For generating team wise report
     *
     * @param surveyResponse : SurveyResponse
     * @return : Object.
     * @throws ResourceNotFoundException : if surveyId is empty.
     */
    @Override
    public Object teamReport(@NotNull SurveyResponse surveyResponse) {
        logger.info(STARTING_METHOD_EXECUTION);
        int index = -1;
        List<Integer> surveys = userRepo.getSurveyIdByName(surveyResponse.getTemplateName());
        int templateId = userRepo.getTemplateIdByName(surveyResponse.getTemplateName());
        List<List<SurveyDataResponse>> surveyDataResponses = new ArrayList<>();
        List<List<List<SurveyDataResponse>>> finalList = new ArrayList<>();
        Set<Integer> teamId = new HashSet<>();
        List<Integer> surveyId = new ArrayList<>();
        for (Integer id : surveys) {
            teamId.addAll(userRepo.getSurveyById(id).stream().map(SurveyStatusDto::getTeamId).collect(Collectors.toSet()));
        }
        List<String> questionList = userRepo.findAllByTemplateId(templateId);
        for (String quesText : questionList) {
            String quesType = quesTypeRepo.getQuestTypeNameByTemplateQuestion(quesText, templateId);
            if (quesType.equals(QUES_TYPE_FILE) || quesType.equals(QUES_TYPE_TEXT))
                continue;
            else
                surveyResponse.setQuestionText(quesText);
            for (Integer team : teamId) {
                finalList.clear();
                index++;
                surveyId.clear();
                surveyId.addAll(surveyResponseRepo.getSurveyIdByTeamId(team));
                surveyId.removeIf(in -> !surveys.contains(in));
                if(surveyId.isEmpty())
                    throw new ResourceNotFoundException(NO_SURVEY_FOUND);
                GraphListResponse graphListResponse = teamReportFilter(surveyResponse, surveyId, team);
                List<SurveyResponseModel> surveyResponseModels = graphListResponse.getSurveyList();
                List<Integer> responseId = graphListResponse.getResponseId();
                surveyDataResponses.add(teamReportHelperFunction(new TeamReportParam(index, surveyResponse, surveyResponseModels, responseId, team, teamId.size(), surveyId)));
            }
            finalList.add(surveyDataResponses);
        }
        if(finalList.isEmpty())
            return finalList;
        logger.info(EXITING_METHOD_EXECUTION);
        return finalList.get(0);

    }
    /**
     * Consolidated report helper function
     * @param teamReportParam:TeamReportParam
     * @return : List<SurveyDataResponse>
     */

    private List<SurveyDataResponse> teamReportHelperFunction(TeamReportParam teamReportParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<AnswerModel> modelList = new ArrayList<>();
        List<Integer> answerId = new ArrayList<>();
        List<AnswerResponse> answer = new ArrayList<>();
        Set<String> answerText = new HashSet<>();
        for (Integer id : teamReportParam.getSurveyId()) {
            modelList.addAll(answerRepo.findByQuesId(questionRepo.findByQuesTextAndSurveyId(teamReportParam.getSurveyResponse().getQuestionText(), id)));
        }
        String quesType = quesTypeRepo.getQuestTypeNameByQuestion(teamReportParam.getSurveyResponse().getQuestionText(), teamReportParam.getSurveyId().get(0));

        for (AnswerModel answerModel : modelList) {
            answerId.add(answerModel.getAnsId());
            answerText.add(answerModel.getAnsText());
        }

        if (teamReportParam.getSurveyList().isEmpty()) {
            logger.debug("Empty io.springboot.survey list");
            total = 0;
            for (String text : answerText) {
                AnswerResponse answerResponse = new AnswerResponse();
                answerResponse.setAnswer(text);
                answerResponse.setCount(0);
                answer.add(answerResponse);
            }
        } else {
            answer=teamReportAnswerResponse(new TeamReportAnswerParam(teamReportParam.getSurveyList(),answerText,answerId,teamReportParam.getResponseId()));
        }
        String teamName;
        if (teamReportParam.getTeamId() != -1)
            teamName = userRepo.getTeamNameByTeamId(teamReportParam.getTeamId());
        else
            teamName = OTHERS;
        List<SurveyDataResponse> responses = new ArrayList<>();
        SurveyDataResponse surveyDataResponse = new SurveyDataResponse();
        surveyDataResponse.setIndex(teamReportParam.getIndex());
        surveyDataResponse.setTeamName(teamName);
        surveyDataResponse.setTeam(true);
        final SurveyData surveyData=new SurveyData();
        surveyData.getSurveyDataResponse(teamReportParam.getSurveyResponse(), teamReportParam.getResponseId(), teamReportParam.getTeamCount(), answer, quesType, surveyDataResponse, answerText.size());
        surveyDataResponse.setTotalNumberOfResponse(total);
        surveyDataResponse.setMandatory(questionRepo.getMandatoryByQuesTextAndSurveyId(teamReportParam.getSurveyResponse().getQuestionText(), teamReportParam.getSurveyId().get(0)));
        responses.add(surveyDataResponse);
        logger.info(EXITING_METHOD_EXECUTION);
        return responses;

    }

    /**
     * Report Filter Function  for team report
     * filter based on designation or duration.
     *
     * @param resp : SurveyResponse
     * @param surveyId : List of io.springboot.survey id.
     * @param team :teamId
     * @return : GraphListResponse
     */
    private GraphListResponse teamReportFilter(SurveyResponse resp, @NotNull List<Integer> surveyId, int team)
    {
        logger.info(STARTING_METHOD_EXECUTION);
        List<SurveyResponseModel> surveyList = new ArrayList<>();
        List<Integer> responseId=new ArrayList<>();
        DateFilterResponse dateFilterResponse;
        long startDate = 0;
        long endDate = 0;
        List<Long> creationDateList= new ArrayList<>();
        List<Long> expirationDateList= new ArrayList<>();

        for(Integer id:surveyId)
        {
            Object[] dates=userRepo.getSurveyData(id);
            creationDateList.add((long) dates[0]);
            expirationDateList.add((long) dates[1]);
        }
        if (!StringUtils.isEmpty(resp.getFilter()) && resp.getDesignation().isEmpty()) {
            logger.debug("Date filter");
            dateFilterResponse=teamFilterSwitch(new TeamFilterSwitchParam(resp.getFilter(),resp.getNumber(),surveyId,team));
            surveyList=dateFilterResponse.getSurveyModelList();
            startDate=dateFilterResponse.getStartDate();
            endDate=dateFilterResponse.getEndDate();
            responseId=setResponseIfListIsNotEmpty(surveyList);
        }
         if (StringUtils.isEmpty(resp.getFilter()) && !resp.getDesignation().isEmpty())
        {
            logger.debug("Designation filter");
            teamReportFilterA(resp,surveyId,surveyList);
            responseId=setResponseIfListIsNotEmpty(surveyList);
            startDate= Collections.min(creationDateList);
            endDate=Collections.max(expirationDateList);
        }
         if (!StringUtils.isEmpty(resp.getFilter()) && !resp.getDesignation().isEmpty()) {
            logger.debug("Date and designation filter");
            dateFilterResponse=teamFilterSwitch(new TeamFilterSwitchParam(resp.getFilter(),resp.getNumber(),surveyId,team));
            surveyList=dateFilterResponse.getSurveyModelList();
            startDate=dateFilterResponse.getStartDate();
            endDate=dateFilterResponse.getEndDate();
            List<SurveyResponseModel> newSurveyList=teamReportFilterB(resp,surveyId,surveyList);
            surveyList = newSurveyList;
            if (newSurveyList.isEmpty())
                responseId = surveyList.stream().map(SurveyResponseModel::getResponseId).collect(Collectors.toList());

        }
        if (StringUtils.isEmpty(resp.getFilter()) && resp.getDesignation().isEmpty()) {
            logger.debug("No filter");
            for(Integer id:surveyId)
            {
                List<SurveyResponseModel> modelList=surveyResponseRepo.findByTeamIdAndSurveyId(team, id);
                responseId.addAll(modelList.stream().map(SurveyResponseModel::getResponseId).collect(Collectors.toList()));
                surveyList.addAll(modelList);
            }
            startDate=Collections.max(creationDateList);
            endDate=Collections.max(expirationDateList);
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return new GraphListResponse(surveyList,responseId,startDate,endDate);

    }
    /**
     * Date filter
     * @param switchParam:TeamFilterSwitchParam
     * @return : DateFilterResponse
     */

    private DateFilterResponse teamFilterSwitch(TeamFilterSwitchParam switchParam)
    {
        logger.info(STARTING_METHOD_EXECUTION);
        DateFilterResponse surveyList;
        switch (switchParam.getFilter())
        {
            case DAY_FILTER:
                logger.debug("Day filter");
                surveyList = teamDayFilter(switchParam.getNumber(), switchParam.getSurveyId(),switchParam.getTeamId());
                break;
            case WEEK_FILTER:
                logger.debug("Week filter");
                surveyList=teamWeekFilter(switchParam.getNumber(),switchParam.getSurveyId(),switchParam.getTeamId());
                break;
            case MONTH_FILTER:
                logger.debug("Month filter");
                surveyList=teamMonthFilter(switchParam.getNumber(),switchParam.getSurveyId(),switchParam.getTeamId());
                break;
            case YEAR_FILTER:
                logger.debug("Year filter");
                surveyList=teamYearFilter(switchParam.getNumber(),switchParam.getSurveyId(),switchParam.getTeamId());
                break;
            default:
                logger.error("Unexpected filter value  : {}",switchParam.getFilter());
                throw new IllegalStateException(UNEXPECTED_VALUE + switchParam.getFilter());
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return surveyList;
    }
    /**
     * Team Day filter
     *
     * @param number : number of days.
     * @param surveyId : list of surveyId
     * @param teamId :teamId
     * @return : DateFilterResponse
     */
    private DateFilterResponse teamDayFilter(int number,List<Integer> surveyId,int teamId)
    {
        logger.info(STARTING_METHOD_EXECUTION);
        List<SurveyResponseModel> surveyResponseModels = new ArrayList<>();
        Long creationDate =teamFilterHelperFunction(surveyId);
        Date date = new Date();
        Timestamp endDate = new Timestamp(date.getTime());
        Timestamp startDate = Timestamp.valueOf(endDate.toLocalDateTime().minusDays(number));
        logger.info(EXITING_METHOD_EXECUTION);
        return getDateFilterResponse(new TeamDateFilterParam(surveyId, teamId, surveyResponseModels, creationDate, endDate.getTime(), startDate.getTime()));
    }

    /**
     * DateFilterResponse for date filter.
     * @param filterParam:TeamDateFilterParam
     * @return : DateFilterResponse
     */
    @NotNull
    private DateFilterResponse getDateFilterResponse(TeamDateFilterParam filterParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        if(filterParam.getStartDate() < filterParam.getCreationDate())
            filterParam.setStartDate(filterParam.getCreationDate());
        DateFilterResponse response=new DateFilterResponse();
        response.setStartDate(filterParam.getStartDate());
        response.setEndDate(filterParam.getEndDate());
        for(Integer id:filterParam.getSurveyId())
        {
            filterParam.getSurveyResponseModels().addAll(surveyResponseRepo.findBySurveyIdAndTeamIdAndResponseDateBetween(id,filterParam.getTeamId(),filterParam.getStartDate(),filterParam.getEndDate()));
        }
        response.setSurveyModelList(filterParam.getSurveyResponseModels());
        logger.info(EXITING_METHOD_EXECUTION);
        return response;
    }

    /**
     * Team Week filter
     *
     * @param number : number of weeks.
     * @param surveyId : list of surveyId
     * @param teamId :teamId
     * @return : DateFilterResponse
     */
    private DateFilterResponse teamWeekFilter(int number,List<Integer> surveyId,int teamId)
    {
        logger.info(STARTING_METHOD_EXECUTION);
        List<SurveyResponseModel> surveyResponseModels = new ArrayList<>();
        Date date = new Date();
        Timestamp endDate = new Timestamp(date.getTime());
        Long creationDate=teamFilterHelperFunction(surveyId);
        Timestamp startDate = Timestamp.valueOf(endDate.toLocalDateTime().minusWeeks(number));
        logger.info(EXITING_METHOD_EXECUTION);
        return getDateFilterResponse(new TeamDateFilterParam(surveyId, teamId, surveyResponseModels, creationDate, endDate.getTime(), startDate.getTime()));
    }

    /**
     * Team Month filter
     *
     * @param number : number of months.
     * @param surveyId : list of surveyId
     * @param teamId :teamId
     * @return : DateFilterResponse
     */
    private DateFilterResponse teamMonthFilter(int number,List<Integer> surveyId,int teamId)
    {
        logger.info(STARTING_METHOD_EXECUTION);
        List<SurveyResponseModel> surveyResponseModels = new ArrayList<>();
        Long creationDate =teamFilterHelperFunction(surveyId);
        Date date = new Date();
        Timestamp endDate = new Timestamp(date.getTime());
        Timestamp startDate = Timestamp.valueOf(endDate.toLocalDateTime().minusMonths(number));
        logger.info(EXITING_METHOD_EXECUTION);
        return getDateFilterResponse(new TeamDateFilterParam(surveyId, teamId, surveyResponseModels, creationDate, endDate.getTime(), startDate.getTime()));
    }
    /**
     * Team Week filter
     *
     * @param number : number of weeks.
     * @param surveyId : list of surveyId
     * @param teamId :teamId
     * @return : DateFilterResponse
     */

    private DateFilterResponse teamYearFilter(int number,List<Integer> surveyId,int teamId)
    {
        logger.info(STARTING_METHOD_EXECUTION);
        List<SurveyResponseModel> surveyResponseModels = new ArrayList<>();
        Long creationDate = teamFilterHelperFunction(surveyId);
        Date date = new Date();
        Timestamp endDate = new Timestamp(date.getTime());
        Timestamp startDate = Timestamp.valueOf(endDate.toLocalDateTime().minusYears(number));
        logger.info(EXITING_METHOD_EXECUTION);
        return getDateFilterResponse(new TeamDateFilterParam(surveyId, teamId, surveyResponseModels, creationDate, endDate.getTime(), startDate.getTime()));
    }


    /**
     * @param surveyId : list of surveyId.
     * @return : Timestamp
     */
    private Long teamFilterHelperFunction(@NotNull List<Integer> surveyId)
    {
        logger.info(STARTING_METHOD_EXECUTION);
        List<Long> creationDateList= new ArrayList<>();
        for(Integer id:surveyId)
        {
            creationDateList.add(userRepo.getCreationDateBySurveyId(id));
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return Collections.min(creationDateList);
    }


    /**
     * Designation filter
     * @param resp :SurveyResponse.
     * @param surveyId : list of surveyId.
     * @param surveyList :  List<SurveyResponseModel>
     */
    private void teamReportFilterA(SurveyResponse resp, List<Integer> surveyId, List<SurveyResponseModel> surveyList){
        logger.info(STARTING_METHOD_EXECUTION);
        List<Integer> userId=specificationModel.getAllByDesignation(resp.getCreatorEmail(),resp).stream().map(UserModel::getUserId).collect(Collectors.toList());
        for (Integer id : userId) {
            for (Integer surId : surveyId) {
                List<SurveyResponseModel> modelList=surveyResponseRepo.findByUserIdAndSurveyId(id, surId);
                if (!CollectionUtils.isEmpty(modelList))
                    surveyList.addAll(modelList);
            }
        }
        logger.info(EXITING_METHOD_EXECUTION);
    }

    /**
     * Date and designation filter
     *
     * @param resp : SurveyResponse.
     * @param surveyId : list of surveyId.
     * @param surveyList : List<SurveyResponseModel>
     * @return List<SurveyResponseModel>
     */
    private List<SurveyResponseModel> teamReportFilterB(SurveyResponse resp, List<Integer> surveyId, @NotNull List<SurveyResponseModel> surveyList) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<Integer> sortedUserId = surveyList.stream().map(SurveyResponseModel::getUserId).collect(Collectors.toList());
        List<Integer> userId=specificationModel.getAllByDesignation(resp.getCreatorEmail(),resp).stream().map(UserModel::getUserId).collect(Collectors.toList());
        List<SurveyResponseModel> newSurveyList = new ArrayList<>();
        for (Integer id : userId) {
            for(Integer surId:surveyId) {
                List<SurveyResponseModel>  modelList=surveyResponseRepo.findByUserIdAndSurveyId(id, surId);
                if (!CollectionUtils.isEmpty(modelList) && sortedUserId.contains(id))
                {
                    newSurveyList.addAll(modelList);
                }
            }
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return newSurveyList;

    }

    /**
     * Set answerResponse used in consolidated report helper function
     * @param answerParam:TeamReportAnswerParam
     * @return  List<AnswerResponse>
     */
    private List<AnswerResponse> teamReportAnswerResponse(TeamReportAnswerParam answerParam)
    {
        logger.info(STARTING_METHOD_EXECUTION);
        List<AnswerResponse> answer=new ArrayList<>();
        for (int i=0;i<answerParam.getSurveyList().size();i+=2) {
            answer.clear();
            total = 0;
            for (String text : answerParam.getAnswerText()) {
                List<Integer> ansId = answerRepo.findByAnsText(text);
                ansId.removeIf(in -> !answerParam.getAnswerId().equals(in));
                if (responseRepo.findByAnswerIdIsIn(ansId)!=0) {
                    AnswerResponse answerResponse = new AnswerResponse();
                    answerResponse.setAnswer(text);
                    Integer count=responseRepo.findByAnswerIdIsInAndResponseIdIsIn(ansId, answerParam.getResponseId());
                    answerResponse.setCount(count);
                    answer.add(answerResponse);
                    total = total +ansId.size();
                } else {
                    logger.debug("Answer id not in : {}",ansId);
                    AnswerResponse answerResponse = new AnswerResponse();
                    answerResponse.setAnswer(text);
                    answerResponse.setCount(0);
                    answer.add(answerResponse);
                }
            }
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return answer;
    }

    /**
     * Set responseId in Date filter and Designation filter
     *
     * @param surveyList : List<SurveyResponseModel>
     * @return List<Integer>
     */
    private List<Integer> setResponseIfListIsNotEmpty(@NotNull List<SurveyResponseModel> surveyList){
        logger.info(STARTING_METHOD_EXECUTION);
        List<Integer> responseId=new ArrayList<>();
        if (!surveyList.isEmpty())
            responseId = surveyList.stream().map(SurveyResponseModel::getResponseId).collect(Collectors.toList());
        logger.info(EXITING_METHOD_EXECUTION);
        return responseId;
    }

}
