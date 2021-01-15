package io.springboot.survey.impl;

import io.springboot.survey.models.SurveyResponseModel;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.pojo.report.ConsolidatedReportParam;
import io.springboot.survey.pojo.report.DateFilterResponseParam;
import io.springboot.survey.pojo.report.GetAnswerResponseParam;
import io.springboot.survey.repository.*;
import io.springboot.survey.response.*;
import io.springboot.survey.service.ConsolidatedReportService;
import io.springboot.survey.utils.SpecificationModel;
import io.springboot.survey.utils.SurveyData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static io.springboot.survey.utils.Constants.CommonConstant.*;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.SurveyModuleConstants.*;

@Component
public class ConsolidatedReportServiceImpl implements ConsolidatedReportService {

    private final QuestionTypeRepo quesTypeRepo;
    private final SurveyRepo surveyRepo;
    private final QuestionRepo questionRepo;
    private final UserRepo userRepo;
    private final SurveyResponseRepo surveyResponseRepo;
    private final ResponseRepo responseRepo;
    private final SpecificationModel specificationModel;
    private int total;
    private static final Logger logger= LoggerFactory.getLogger(ConsolidatedReportServiceImpl.class.getSimpleName());

    public ConsolidatedReportServiceImpl(QuestionTypeRepo quesTypeRepo, SurveyRepo surveyRepo, QuestionRepo questionRepo, UserRepo userRepo, SurveyResponseRepo surveyResponseRepo, ResponseRepo responseRepo, SpecificationModel specificationModel) {
        this.quesTypeRepo = quesTypeRepo;
        this.surveyRepo = surveyRepo;
        this.questionRepo = questionRepo;
        this.userRepo = userRepo;
        this.surveyResponseRepo = surveyResponseRepo;
        this.responseRepo = responseRepo;
        this.specificationModel = specificationModel;
    }

    /**
     * For generating consolidated report
     *
     * @param surveyResponse : SurveyResponse
     * @return : Object.
     */
    @Override
     public Object consolidatedReports(@NotNull SurveyResponse surveyResponse) {
        logger.info(STARTING_METHOD_EXECUTION);
        int index = -1;
        final List<List<SurveyDataResponse>> surveyDataResponses = new ArrayList<>();
        final List<List<List<SurveyDataResponse>>> finalList = new ArrayList<>();
        final List<Integer> surveys = userRepo.getSurveyIdByName(surveyResponse.getTemplateName());
        final int templateId = userRepo.getTemplateIdByName(surveyResponse.getTemplateName());
        final List<String> questionList = userRepo.findAllByTemplateId(index);
        for (final String quesText : questionList) {
            final String quesType = quesTypeRepo.getQuestTypeNameByTemplateQuestion(quesText, templateId);
            if (quesType.equals(QUES_TYPE_CHECKBOX) || quesType.equals(QUESTION_RESPONSE))
                continue;
            else
                surveyResponse.setQuestionText(quesText);
            for (final Integer surveyId : surveys) {
                index++;
                final GraphListResponse graphListResponse = reportFilter(surveyResponse, surveyId);
                final List<SurveyResponseModel> surveyList = graphListResponse.getSurveyList();
                final List<Integer> responseId = graphListResponse.getResponseId();
                final int userId = surveyRepo.getCreatorById(surveyId);
                surveyDataResponses.add(consolidatedReportHelperFunction(new ConsolidatedReportParam(index, templateId, surveyResponse, surveyList, responseId, userId, questionList.size())));
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
     *
     * @param param :ConsolidatedReportParam
     *
     * @return : List<SurveyDataResponse>
     */
    private List<SurveyDataResponse> consolidatedReportHelperFunction(ConsolidatedReportParam param) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<AnswerResponse> answer = new ArrayList<>();
        List<SurveyDataResponse> responses = new ArrayList<>();
        String questionType = quesTypeRepo.getQuestTypeNameByQuestion(param.getSurveyResponse().getQuestionText(), param.getSurveyId());
        List<Integer> answerId = getAnswersId(param.getSurveyResponse(),param.getSurveyId());

        if (param.getSurveyList().isEmpty()) {
            for (Integer ansId : answerId) {
                logger.debug("Empty io.springboot.survey list");
                AnswerResponse answerResponse = new AnswerResponse();
                answerResponse.setAnswer(questionRepo.findByAnsId(ansId));
                answerResponse.setCount(3);
                answer.add(answerResponse);
            }
        } else {
            getAnswerResponse(new GetAnswerResponseParam(answerId, answer, param.getSurveyList(), param.getResponseId()));
        }
        SurveyDataResponse surveyDataResponse = new SurveyDataResponse();
        surveyDataResponse.setIndex(param.getIndex());
        surveyDataResponse.setTeam(false);
        surveyDataResponse.setTeamName((String) userRepo.getUserNameAndUserEmail(param.getUserId()).get(0));
        final SurveyData surveyData=new SurveyData();
        surveyData.getSurveyDataResponse(param.getSurveyResponse(), param.getResponseId(), param.getTeamCount(), answer, questionType, surveyDataResponse, answerId.size());
        surveyDataResponse.setMandatory(questionRepo.getMandatoryByQuesTextAndSurveyId(param.getSurveyResponse().getQuestionText(), param.getSurveyId()));
        surveyDataResponse.setTotalNumberOfResponse(total);
        responses.add(surveyDataResponse);
        logger.info(EXITING_METHOD_EXECUTION);
        return responses;

    }

    /**
     * Date filter
     *
     * @param filter : filterName --> Day(s)|Week(s)|Month(s)|Year(s).
     * @param number : number of days|week|month|year.
     * @param surveyId : surveyId
     * @return : DateFilterResponse
     */
    private DateFilterResponse filterSwitch(@NotNull String filter, int number, int surveyId) {
        logger.info(STARTING_METHOD_EXECUTION);
        DateFilterResponse surveyList;
        switch (filter) {
            case DAY_FILTER:
                logger.debug("Day filter");
                surveyList = dayFilter(number, surveyId);
                break;
            case WEEK_FILTER:
                logger.debug("Week filter");
                surveyList = weekFilter(number, surveyId);
                break;
            case MONTH_FILTER:
                logger.debug("Month filter");
                surveyList = monthFilter(number, surveyId);
                break;
            case YEAR_FILTER:
                logger.debug("Year filter");
                surveyList = yearFilter(number, surveyId);
                break;
            default:
                logger.error("Unexpected filter value  : {}",filter);
                throw new IllegalArgumentException(UNEXPECTED_VALUE + filter);
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return surveyList;
    }


    /**
     * Report Filter Function
     * filter based on designation or duration.
     *
     * @param resp : SurveyResponse
     * @param surveyId : SurveyId
     * @return : GraphListResponse
     */
    public GraphListResponse reportFilter(@NotNull SurveyResponse resp, int surveyId) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<SurveyResponseModel> surveyList = new ArrayList<>();
        DateFilterResponse dateFilterResponse;
        List<Integer> responseId = new ArrayList<>();
        long endDate = 0;
        long startDate = 0;
        if (!StringUtils.isEmpty(resp.getFilter()) && resp.getDesignation().isEmpty()) {
            logger.debug("Date filter");
            dateFilterResponse = filterSwitch(resp.getFilter(), resp.getNumber(), surveyId);
            surveyList = dateFilterResponse.getSurveyModelList();
            startDate = dateFilterResponse.getStartDate();
            endDate = dateFilterResponse.getEndDate();

        }
        if (StringUtils.isEmpty(resp.getFilter()) && !resp.getDesignation().isEmpty()) {
            logger.debug("Designation filter");
            onlyDesignation(resp,surveyId,surveyList);
            responseId=setResponseIfListIsNotEmpty(surveyList);
            Object [] dates=userRepo.getSurveyData(surveyId);
            startDate = (Long) dates[0];
            endDate = (long) dates[1];
        }
         if (!StringUtils.isEmpty(resp.getFilter()) && !resp.getDesignation().isEmpty()) {
            logger.debug("Date and designation filter");
            dateFilterResponse = filterSwitch(resp.getFilter(), resp.getNumber(), surveyId);
            surveyList = dateFilterResponse.getSurveyModelList();
            startDate = dateFilterResponse.getStartDate();
            endDate = dateFilterResponse.getEndDate();
            List<SurveyResponseModel> newSurveyList = bothFilters(resp,surveyId,surveyList);
            surveyList = newSurveyList;
            if (!newSurveyList.isEmpty())
                responseId = surveyList.stream().map(SurveyResponseModel::getResponseId).collect(Collectors.toList());

        }  if  (StringUtils.isEmpty(resp.getFilter()) && resp.getDesignation().isEmpty()){
            logger.debug("No filter");
            Object[] dates=userRepo.getSurveyData(surveyId);
            surveyList = surveyResponseRepo.findByTeamId(surveyId);
            responseId = surveyList.stream().map(SurveyResponseModel::getResponseId).collect(Collectors.toList());
            startDate = (Long) dates[0];
            endDate = (Long) dates[1];
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return new GraphListResponse(surveyList,responseId,startDate,endDate);

    }

    /**
     * Only Designation Filter.
     *
     * @param resp : SurveyResponse
     * @param surveyId : surveyId
     * @param surveyList : List<SurveyResponseModel
     */
    private void onlyDesignation(SurveyResponse resp, int surveyId, List<SurveyResponseModel> surveyList){
        logger.info(STARTING_METHOD_EXECUTION);
        List<Integer> userId = specificationModel.getAllByDesignation(resp.getCreatorEmail(), resp).stream().map(UserModel::getUserId).
                collect(Collectors.toList());
        for (Integer id : userId) {
            List<SurveyResponseModel> modelList= surveyResponseRepo.findByUserIdAndSurveyId(id, surveyId);
            if (!CollectionUtils.isEmpty(modelList))
                surveyList.addAll(modelList);
        }
        logger.info(EXITING_METHOD_EXECUTION);
    }

    /**
     *Date and designation filter
     *
     * @param resp : SurveyResponse.
     * @param surveyId : SurveyId.
     * @param surveyList : List<SurveyResponseModel>
     * @return List<SurveyResponseModel>
     */
    private List<SurveyResponseModel> bothFilters(SurveyResponse resp, int surveyId, @NotNull List<SurveyResponseModel>
            surveyList) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<SurveyResponseModel> newSurveyList = new ArrayList<>();
        List<Integer> sortedUserId = surveyList.stream().map(SurveyResponseModel::getTeamId).collect(Collectors.
                toList());
        List<Integer> userId = specificationModel.getAllByDesignation(resp.getCreatorEmail(), resp).stream().map(UserModel::getUserId).
                collect(Collectors.toList());
        for (Integer id : userId) {
            List<SurveyResponseModel> modelList=surveyResponseRepo.findByUserIdAndSurveyId(id, surveyId);
            if (modelList != null && sortedUserId.contains(id)) {
                newSurveyList.addAll(modelList);
            }
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return newSurveyList;
    }


    /**
     * Day filter
     *
     * @param number : number of days.
     * @param surveyId : surveyId
     * @return : DateFilterResponse
     */
    private DateFilterResponse dayFilter(int number,int surveyId) {
        logger.info(STARTING_METHOD_EXECUTION);
        Long creationDate=userRepo.getCreationDateBySurveyId(surveyId);
        Date date = new Date();
        Timestamp endDate = new Timestamp(date.getTime());
        Timestamp startDate = Timestamp.valueOf(endDate.toLocalDateTime().minusDays(number));
        logger.info(EXITING_METHOD_EXECUTION);
        return getDateFilterResponse(new DateFilterResponseParam(surveyId, creationDate, endDate.getTime(), startDate.getTime()));

    }

    /**
     * Week filter
     *
     * @param number : number of weeks.
     * @param surveyId : surveyId
     * @return : DateFilterResponse
     */
    private DateFilterResponse weekFilter(int number,int surveyId) {
        logger.info(STARTING_METHOD_EXECUTION);
        Long creationDate=userRepo.getCreationDateBySurveyId(surveyId);
        Date date = new Date();
        Timestamp endDate = new Timestamp(date.getTime());
        Timestamp startDate = Timestamp.valueOf(endDate.toLocalDateTime().minusWeeks(number));
        logger.info(EXITING_METHOD_EXECUTION);
        return getDateFilterResponse(new DateFilterResponseParam(surveyId, creationDate, endDate.getTime(), startDate.getTime()));
    }
    /**
     * Month filter
     *
     * @param number : number of months.
     * @param surveyId : surveyId
     * @return : DateFilterResponse
     */
    private DateFilterResponse monthFilter(int number,int surveyId) {
        logger.info(STARTING_METHOD_EXECUTION);
        Long creationDate=userRepo.getCreationDateBySurveyId(surveyId);
        Date date = new Date();
        Timestamp endDate = new Timestamp(date.getTime());
        Timestamp startDate = Timestamp.valueOf(endDate.toLocalDateTime().minusMonths(number));
        logger.info(EXITING_METHOD_EXECUTION);
        return getDateFilterResponse(new DateFilterResponseParam(surveyId, creationDate, endDate.getTime(), startDate.getTime()));
    }

    /**
     * Year filter
     *
     * @param number : number of years.
     * @param surveyId : surveyId
     * @return : DateFilterResponse
     */
    private DateFilterResponse yearFilter(int number,int surveyId) {
        logger.info(STARTING_METHOD_EXECUTION);
        Long creationDate=userRepo.getCreationDateBySurveyId(surveyId);
        Date date = new Date();
        Timestamp endDate = new Timestamp(date.getTime());
        Timestamp startDate = Timestamp.valueOf(endDate.toLocalDateTime().minusYears(number));
        logger.info(EXITING_METHOD_EXECUTION);
        return getDateFilterResponse(new DateFilterResponseParam(surveyId, creationDate, endDate.getTime(), startDate.getTime()));
    }


    /**
     * Return List of responseId for Designation filter
     *
     * @param surveyList : List<SurveyResponseModel>
     * @return : List<Integer>
     */
    private List<Integer> setResponseIfListIsNotEmpty(@NotNull List<SurveyResponseModel> surveyList){
        List<Integer> responseId=new ArrayList<>();
        logger.info(STARTING_METHOD_EXECUTION);
        if (!surveyList.isEmpty()) {
            responseId = surveyList.stream().map(SurveyResponseModel::getResponseId).collect(Collectors.toList());
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return responseId;
    }

    /**
     * Return List of answerId
     *
     * @param resp : SurveyResponse.
     * @param surveyId : SurveyId
     * @return : List<Integer>.
     */
    public  @NotNull List<Integer> getAnswersId(@NotNull SurveyResponse resp, Integer surveyId)
    {
        return questionRepo.getAnsByQuesTextAndSurveyId(resp.getQuestionText(), surveyId);
    }

    /**
     * Set answerResponse used in consolidated report helper function
     * @param param :GetAnswerResponseParam
     * @return : total
     */
    public int getAnswerResponse(GetAnswerResponseParam param) {
        logger.info(STARTING_METHOD_EXECUTION);
        for (int i=0;i<param.getSurveyList().size();i++) {
            param.getAnswer().clear();
            total = 0;
            for (Integer ansId : param.getAnswerId()) {
                if (responseRepo.findByAnswerId(ansId)!=0) {
                    AnswerResponse answerResponse = new AnswerResponse();
                    answerResponse.setAnswer(questionRepo.findByAnsId(ansId));
                    Integer count=responseRepo.findByAnswerIdAndResponseIdIsIn(ansId, param.getResponseId());
                    answerResponse.setCount(count);
                    param.getAnswer().add(answerResponse);
                    total = total + count;
                } else {
                    AnswerResponse answerResponse = new AnswerResponse();
                    answerResponse.setAnswer(questionRepo.findByAnsId(ansId));
                    answerResponse.setCount(0);
                    param.getAnswer().add(answerResponse);
                }
            }
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return total;
    }

    /**
     * DateFilterResponse for date filter.
     * @param param : DateFilterResponseParam
     * @return : DateFilterResponse
     */
    @NotNull
    private DateFilterResponse getDateFilterResponse(DateFilterResponseParam param) {
        logger.info(STARTING_METHOD_EXECUTION);
        if(param.getStartDate() < (param.getCreationDate()))
            param.setStartDate(param.getCreationDate());
        DateFilterResponse response=new DateFilterResponse();
        response.setStartDate(param.getStartDate());
        response.setEndDate(param.getEndDate());
        response.setSurveyModelList(surveyResponseRepo.findBySurveyIdAndResponseDateBetween(param.getSurveyId(),param.getStartDate(),param.getEndDate()));
        logger.info(EXITING_METHOD_EXECUTION);
        return response;
    }


}
