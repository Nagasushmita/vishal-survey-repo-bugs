package io.springboot.survey.impl;

import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.mapper.SurveyResponseDto;
import io.springboot.survey.models.QuestionModel;
import io.springboot.survey.models.SurveyResponseModel;
import io.springboot.survey.pojo.report.GetAnswerResponseParam;
import io.springboot.survey.repository.*;
import io.springboot.survey.response.*;
import io.springboot.survey.service.ConsolidatedReportService;
import io.springboot.survey.service.SurveySecondService;
import io.springboot.survey.service.SurveyUserResponseService;
import io.springboot.survey.utils.DynamicFiltering;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static io.springboot.survey.utils.Constants.CommonConstant.*;
import static io.springboot.survey.utils.Constants.ErrorMessageConstant.SURVEY_NOT_FOUND;
import static io.springboot.survey.utils.Constants.FilterConstants.ALL_RESPONSE_FILTER;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.SurveyModuleConstants.*;
import static io.springboot.survey.utils.Constants.ValidationConstant.NAME;
import static io.springboot.survey.utils.Constants.ValidationConstant.TEAM_NAME;

@Component
public class SurveySecondServiceImplementation implements SurveySecondService {


    private final QuestionRepo questionRepo;
    private final SurveyResponseRepo surveyResponseRepo;
    private final QuestionTypeRepo quesTypeRepo;
    private final SurveyStatusRepo surveyStatusRepo;
    private final UserRepo userRepo;
    private final ConsolidatedReportService consolidatedReportService;
    private final SurveyUserResponseService surveyUserResponseService;

    private static final Logger logger= LoggerFactory.getLogger(SurveySecondServiceImplementation.class.getSimpleName());


    public SurveySecondServiceImplementation(QuestionRepo questionRepo, SurveyResponseRepo surveyResponseRepo, QuestionTypeRepo quesTypeRepo, SurveyStatusRepo surveyStatusRepo, UserRepo userRepo, ConsolidatedReportService consolidatedReportService, SurveyUserResponseService surveyUserResponseService) {
        this.questionRepo = questionRepo;
        this.surveyResponseRepo = surveyResponseRepo;
        this.quesTypeRepo = quesTypeRepo;
        this.surveyStatusRepo = surveyStatusRepo;
        this.userRepo = userRepo;
        this.consolidatedReportService = consolidatedReportService;
        this.surveyUserResponseService = surveyUserResponseService;
    }


    /**
     * Survey report helper function
     * 
     * @param resp:SurveyResponse
     * @param surveyId : surveyId
     * @return  List<SurveyDataResponse>
     * @throws ParseException:ParseException
     */
    private List<SurveyDataResponse> getResponse(SurveyResponse resp, Integer surveyId) throws ParseException {
        logger.info(STARTING_METHOD_EXECUTION);
        List<SurveyDataResponse> responses = new ArrayList<>();
        List<AnswerResponse> answer = new ArrayList<>();
        String quesType = quesTypeRepo.getQuestTypeNameByQuestion(resp.getQuestionText(), surveyId);
        List<Integer> answerId = consolidatedReportService.getAnswersId(resp,surveyId);
        GraphListResponse graphListResponse = consolidatedReportService.reportFilter(resp, surveyId);
        List<SurveyResponseModel> surveyList = graphListResponse.getSurveyList();
        List<Integer> responseId = graphListResponse.getResponseId();
        if (surveyList.isEmpty()) {
            logger.debug("Survey list is empty");
            logger.info(EXITING_METHOD_EXECUTION);
            return Collections.emptyList();
        }
       int total=consolidatedReportService.getAnswerResponse(new GetAnswerResponseParam(answerId, answer, surveyList, responseId));
        SurveyDataResponse surveyDataResponse = new SurveyDataResponse();
        surveyDataResponse.setQuestionText(resp.getQuestionText());
        surveyDataResponse.setAnswerResponses(answer);
        surveyDataResponse.setQuesType(quesType);
        surveyDataResponse.setNumberOfAnswer(answerId.size());
        surveyDataResponse.setNumberOfUserResponse(responseId.size());
        surveyDataResponse.setTotalNumberOfResponse(total);
        surveyDataResponse.setMandatory(questionRepo.getMandatoryByQuesTextAndSurveyId(resp.getQuestionText(), surveyId));
        surveyDataResponse.setStartDate(graphListResponse.getStartDate());
        surveyDataResponse.setEndDate(graphListResponse.getEndDate());
        responses.add(surveyDataResponse);
        logger.info(EXITING_METHOD_EXECUTION);
        return responses;
    }

    /**
     * Returns information about the type of filter avaiable in reports.
     *
     * @return : List<String>
     */
    @Override
    public List<String> reportFilterInfo() {
        logger.info(STARTING_METHOD_EXECUTION);
        List<String> info = new ArrayList<>();
        info.add(DAY_FILTER);
        info.add(WEEK_FILTER);
        info.add(MONTH_FILTER);
        info.add(YEAR_FILTER);
        logger.info(EXITING_METHOD_EXECUTION);
        return info;
    }

    /**
     * Return totalSurvey,surveyTaken,surveyPending count for a particular user.
     *
     * @param email : email of logged in user.
     * @return : Map<String, Integer>
     */
    @Override
    public Map<String, Integer> getCount(String email) {
        logger.info(STARTING_METHOD_EXECUTION);
        Map<String, Integer> count = new HashMap<>();
        count.put(TOTAL_SURVEY, userRepo.getSurveyStatusModelByEmailSize(email));
        count.put(SURVEY_TAKEN, userRepo.getSurveyStatusModelSize(email,true));
        count.put(SURVEY_LEFT, userRepo.getSurveyStatusModelSize(email,false));
        logger.info(EXITING_METHOD_EXECUTION);
        return count;
    }

    /**
     *Return all the respone given by different users for a particular io.springboot.survey
     *
     * @param surveyName :surveyName.
     * @param creatorEmail : email of logged in user.
     * @return : List<AllResponse>
     * @throws ResourceNotFoundException : if userRepo.getSurveyDataByNameAndId() returns null.
     */
    @Override
    public MappingJacksonValue getAllResponse(String surveyName, String creatorEmail) {
        logger.info(STARTING_METHOD_EXECUTION);
        if (userRepo.getSurveyDataByNameAndId(surveyName,creatorEmail,false) != null) {
            logger.info(EXITING_METHOD_EXECUTION);
            return getAllResponseHelperFunction(surveyName,creatorEmail);
        }
        logger.debug("Survey {} does not exist",surveyName);
        logger.info(EXITING_METHOD_EXECUTION);
        throw new ResourceNotFoundException(SURVEY_NOT_FOUND);
    }

    /**
     * Get all response helper function
     *
     * @param surveyName : surveyName.
     * @param creatorEmail: email of logged in user.
     * @return  List<AllResponse>
     */
    private MappingJacksonValue getAllResponseHelperFunction(String surveyName, String creatorEmail) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<AllResponse> totalResponse = new ArrayList<>();
        int surveyId = userRepo.getSurveyDataByNameAndId(surveyName, creatorEmail,false).getSurveyId();

        Set<Integer> listOfIds = surveyStatusRepo.getUserIdByIdAndTaken(surveyId, true);
        List<Integer> uniqueQuestion=questionRepo.getQuestIdBySurveyId(surveyId);
        for (Integer userId : listOfIds) {
            List<SurveyResponseDto> surveyResponseModels = surveyResponseRepo.getResponseIdAndTeamId(userId, surveyId);
            for (SurveyResponseDto surveyResponseDto : surveyResponseModels) {
                AllResponse allResponse = new AllResponse();
                allResponse.setUserName((String) userRepo.getUserNameAndUserEmail(userId).get(0));
                allResponse.setQuestionResponses(surveyUserResponseService.getUserResponse(uniqueQuestion, surveyId, surveyResponseDto.getResponseId()));
                if (surveyResponseDto.getTeamId() == -1) {
                    allResponse.setTeamName(userRepo.getTeamNameByTeamId(surveyResponseDto.getTeamId()));
                } else {
                    allResponse.setTeamName(NAME);
                }
                totalResponse.add(allResponse);
            }
        }
        DynamicFiltering dynamicFiltering = new DynamicFiltering();
        Set<String>filters = new HashSet<>(Arrays.asList(USER_NAME,TEAM_NAME,QUESTION_RESPONSE));
        logger.info(EXITING_METHOD_EXECUTION);
        return dynamicFiltering.dynamicObjectFiltering(totalResponse,filters,ALL_RESPONSE_FILTER);
    }


    /**
     * Report of a particular io.springboot.survey
     *
     * @param surveyResponse :SurveyResponse
     * @return : io.springboot.survey Report
     * @throws ParseException : ParseException
     */
    @Override
    public Object surveyReport(@NotNull SurveyResponse surveyResponse) throws ParseException {
        logger.info(STARTING_METHOD_EXECUTION);
        List<List<SurveyDataResponse>> finalList = new ArrayList<>();
        int surveyId = userRepo.getSurveyDataByNameAndId(surveyResponse.getSurveyName(), surveyResponse.getCreatorEmail(),false).getSurveyId();
        List<String> questionList = questionRepo.findAllBySurveyId(surveyId).stream().map(QuestionModel::getQuesText)
                .collect(Collectors.toList());
        for (String quesText : questionList) {
            String quesType = quesTypeRepo.getQuestTypeNameByQuestion(quesText, surveyId);
            if (quesType.equals(QUES_TYPE_FILE) | quesType.equals(QUES_TYPE_TEXT))
                continue;
            else
                surveyResponse.setQuestionText(quesText);
            finalList.add(getResponse(surveyResponse, surveyId));
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return finalList;
    }


}


