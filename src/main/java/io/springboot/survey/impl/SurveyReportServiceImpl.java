package io.springboot.survey.impl;

import io.springboot.survey.repository.SurveyRepo;
import io.springboot.survey.repository.SurveyStatusRepo;
import io.springboot.survey.repository.TemplateRepo;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.response.SurveyResponse;
import io.springboot.survey.service.ConsolidatedReportService;
import io.springboot.survey.service.SurveyReportService;
import io.springboot.survey.service.SurveyTeamReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;

@Component
public class SurveyReportServiceImpl implements SurveyReportService {

    final SurveyRepo surveyRepo;
    final TemplateRepo templateRepo;
    final SurveyStatusRepo surveyStatusRepo;
    final SurveyTeamReportService surveyTeamReportService;
    final ConsolidatedReportService consolidatedReportService;
    final UserRepo userRepo;

    private static final Logger logger=LoggerFactory.getLogger(SurveyReportServiceImpl.class.getSimpleName());

    public SurveyReportServiceImpl(SurveyRepo surveyRepo, TemplateRepo templateRepo, SurveyStatusRepo surveyStatusRepo, SurveyTeamReportService surveyTeamReportService, ConsolidatedReportService consolidatedReportService, UserRepo userRepo) {
        this.surveyRepo = surveyRepo;
        this.templateRepo = templateRepo;
        this.surveyStatusRepo = surveyStatusRepo;
        this.surveyTeamReportService = surveyTeamReportService;
        this.consolidatedReportService = consolidatedReportService;
        this.userRepo = userRepo;
    }


    /**
     * Return the report based on whether the io.springboot.survey was assigned to teams or to individuals.
     * if
     * teamId set is empty i.e io.springboot.survey was assigned to individuals hence
     * return consolidatedReportService.consolidatedReports(surveyResponse)
     * else
     * return surveyTeamReportService.teamReport(surveyResponse)
     *
     * @param surveyResponse : SurveyResponse.
     * @return : Object.
     * @throws ParseException : consolidatedReportService.consolidatedReports() throws parse exception.
     */
    @Override
    public Object reportSwitch(@NotNull SurveyResponse surveyResponse) throws ParseException {
        logger.info(STARTING_METHOD_EXECUTION);
        Set<Integer> teamId = new HashSet<>();
        List<Integer> surveys = surveyRepo.findSurveysByTemplateId(
                templateRepo.findByTemplateName(surveyResponse.getTemplateName()).getTemplateId());
        for (Integer id : surveys) {
            teamId.addAll(surveyStatusRepo.findBySurveyId(id));
        }
        teamId.removeIf(id -> id == -2);
        if (teamId.isEmpty()) {
            logger.info(EXITING_METHOD_EXECUTION);
            return consolidatedReportService.consolidatedReports(surveyResponse);
        } else {
            logger.info(EXITING_METHOD_EXECUTION);
            return surveyTeamReportService.teamReport(surveyResponse);
        }
    }




}
