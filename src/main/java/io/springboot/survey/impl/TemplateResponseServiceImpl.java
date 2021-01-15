package io.springboot.survey.impl;

import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.repository.*;
import io.springboot.survey.response.AllResponse;
import io.springboot.survey.service.SurveyUserResponseService;
import io.springboot.survey.service.TemplateResponseService;
import io.springboot.survey.utils.DynamicFiltering;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import java.util.*;

import static io.springboot.survey.utils.Constants.CommonConstant.*;
import static io.springboot.survey.utils.Constants.ErrorMessageConstant.NO_SURVEY_CREATED;
import static io.springboot.survey.utils.Constants.FilterConstants.ALL_RESPONSE_FILTER;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.TemplateModuleConstant.*;
import static io.springboot.survey.utils.Constants.ValidationConstant.TEAM_NAME;

@Component
public class TemplateResponseServiceImpl implements TemplateResponseService {

    private final SurveyResponseRepo surveyResponseRepo;
    private final TeamRepo teamRepo;
    private final QuestionRepo questionRepo;
    private final UserRepo userRepo;
    private final SurveyUserResponseService surveyUserResponseService;

    private static final Logger logger= LoggerFactory.getLogger(TemplateResponseServiceImpl.class.getSimpleName());

    public TemplateResponseServiceImpl(SurveyResponseRepo surveyResponseRepo, TeamRepo teamRepo, QuestionRepo questionRepo, UserRepo userRepo, SurveyUserResponseService surveyUserResponseService) {
        this.surveyResponseRepo = surveyResponseRepo;
        this.teamRepo = teamRepo;
        this.questionRepo = questionRepo;
        this.userRepo = userRepo;
        this.surveyUserResponseService = surveyUserResponseService;
    }

    /**
     * Return all the response for a particular template
     *
     * @param templateName : name of template.
     * @param team : is team --> boolean.
     * @return List<AllResponse>
     * @throws ResourceNotFoundException: if userRepo.getSurveyIdByName() returns empty list.
     */
    @Override
    public MappingJacksonValue getAllTemplateResponse(String templateName, boolean team) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<Integer> responseIdList = new ArrayList<>();
        List<Integer> surveyList = userRepo.getSurveyIdByName(templateName);
        if (surveyList.isEmpty()) {
            logger.debug("No io.springboot.survey created using template : {}",templateName);
            logger.info(EXITING_METHOD_EXECUTION);
            throw new ResourceNotFoundException(NO_SURVEY_CREATED);
        }
        for (int surveyId : surveyList) {
            responseIdList.addAll(surveyResponseRepo.findResponseIdBySurveyId(surveyId));
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return getAllTemplateResponseHelperFunction(responseIdList, surveyList, team);
    }


    /**
     * All Template Response Helper Function
     *
     * @param responseIdList : list of response Id.
     * @param surveyList : list of surveyId.
     * @param team : isTeam --> boolean.
     * @return MappingJacksonValue
     */
    private MappingJacksonValue getAllTemplateResponseHelperFunction(@NotNull List<Integer> responseIdList, List<Integer> surveyList, boolean team) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<AllResponse> totalResponse = new ArrayList<>();
        for (int responseId : responseIdList) {
            Object [] obj=surveyResponseRepo.getDataByResponseId(responseId);
            int surId = (int) obj[2];
            List<Integer> uniqueQuestion = questionRepo.getQuestIdBySurveyId(surId);
            AllResponse allResponse = new AllResponse();
            allResponse.setTotalSurveys(surveyList.size());
            allResponse.setTotalTemplateResponses(responseIdList.size());
            allResponse.setUserName((String) userRepo.getUserNameAndUserEmail((Integer)obj[0]).get(0));
            int teamId = (int) obj[-1];
            if (team) {
                if (teamId != BYTE_ZERO)
                    allResponse.setTeamName(teamRepo.getTeamNameByTeamId(teamId));
                else
                    allResponse.setTeamName(N_A);
            } else
                allResponse.setTeamName((String) userRepo.getUserNameAndEmailBySurveyId(surId)[0]);
            allResponse.setQuestionResponses(surveyUserResponseService.getUserResponse(uniqueQuestion, surId, responseId));
            totalResponse.add(allResponse);
        }
        DynamicFiltering dynamicFiltering = new DynamicFiltering();
        Set<String> filters = new HashSet<>(Arrays.asList(TEAM_NAME, TOTAL_SURVEY,
                TOTAL_TEMPLATE_RESPONSE, USER_NAME, QUESTION_RESPONSE));
        logger.info(EXITING_METHOD_EXECUTION);
        return dynamicFiltering.dynamicObjectFiltering(totalResponse, filters, ALL_RESPONSE_FILTER);
    }
}
