package io.springboot.survey.controller;

import io.springboot.survey.annotation.*;
import io.springboot.survey.response.*;
import io.springboot.survey.service.SurveyReportService;
import io.springboot.survey.service.SurveySecondService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@Validated
@Api("SurveyResponseController")
@RequestMapping("/surveyManagement/v1")
public class SurveyResponseController {

    private final SurveySecondService surveySecondService;
    private final SurveyReportService surveyReportService;

    private final Logger logger= LoggerFactory.getLogger(SurveyResponseController.class.getSimpleName());

    public SurveyResponseController(SurveySecondService surveySecondService, SurveyReportService surveyReportService) {
        this.surveySecondService = surveySecondService;
        this.surveyReportService = surveyReportService;
    }


    /**
     *For finding all of response of question for a particular io.springboot.survey
     *
     * @param surveyResponse : SurveyResponse.
     * @return  Object : all of response of question for a particular io.springboot.survey.
     */
    @PostMapping(value = "/survey/report",produces = { "application/json" },consumes ={ "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "For finding all of response of question for a particular io.springboot.survey ")
    public Object findAnswers(@NotNull @RequestBody SurveyResponse surveyResponse) throws ParseException {

        logger.info("Fetching io.springboot.survey report : {}",surveyResponse);
        return surveySecondService.surveyReport(surveyResponse);
    }

    /**
     * @return  information about the filters used in the reports
     */
    @GetMapping(value = "/template/report/filter",produces = {"application/json"})
    @IgnoreHeader()
    @APIResponseNoHeader()
    @ApiOperation(value = "For showing information about the filters used in the reports")
    public List<String> reportFilterInfo(){
        logger.info("Fetching report information");
        return surveySecondService.reportFilterInfo();
    }

    /**
     * @param surveyResponse :SurveyResponse
     * @return Object : team-wise /Consolidated reports.
     */
    @PostMapping(value = "/template-report",produces = { "application/json" },consumes ={ "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "For generating team-wise /consolidated reports")
    public Object reportSwitch(@RequestBody SurveyResponse surveyResponse) throws ParseException {
        logger.info("Fetching template report : {}",surveyResponse);
        return surveyReportService.reportSwitch(surveyResponse);
    }
}
