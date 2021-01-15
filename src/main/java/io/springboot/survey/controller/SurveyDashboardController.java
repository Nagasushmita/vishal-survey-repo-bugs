package io.springboot.survey.controller;

import io.springboot.survey.annotation.APIResponseOk;
import io.springboot.survey.annotation.Email;
import io.springboot.survey.annotation.EmptyNotNull;
import io.springboot.survey.service.SurveyDashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Map;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.EMAIL_NOT_NULL;
import static io.springboot.survey.utils.Constants.NullEmptyConstant.PAGE_NOT_NULL;
import static io.springboot.survey.utils.Constants.ValidationConstant.*;


@RestController
@Validated
@Api("SurveyDashboardController")
@RequestMapping("/surveyManagement/v1")
public class SurveyDashboardController {

    final SurveyDashboardService surveyDashboardService;
    private final Logger logger= LoggerFactory.getLogger(SurveyDashboardController.class.getSimpleName());

    public SurveyDashboardController(SurveyDashboardService surveyDashboardService) {
        this.surveyDashboardService = surveyDashboardService;
    }

    /**
     * For showing information needed for the HR dashboard cards
     *
     * @param email :email of the logged in user.
     * @return : Map<String,Integer> --> Containing information required for HR dashboard cards.
     */
    //HR Dashboard
    @GetMapping(value = "/hrDashboard",produces = {"application/json"})
    @ApiOperation(value = "For showing information needed for the HR dashboard cards")
    public Map<String, Integer> hrDashboardInfo(@RequestParam(name = EMAIL)
                                                @EmptyNotNull(message = EMAIL_NOT_NULL)
                                                @Email String email)
    {
        logger.info("Fetching HR dashboard card information of user with email : {}",email);
        return surveyDashboardService.hrDashboardInfo(email);
    }

    /**
     * For showing information needed for the HR dashboard graph
     *
     * @param email : email of the logged in user.
     * @return : StatusResponse : information needed for the HR dashboard graph
     */
    //HR and ManagerDashboard
    @GetMapping(value = "/hrDashboardGraph",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For showing information needed for the HR dashboard graph")
    public MappingJacksonValue hrDashboardGraph(@RequestParam(name = EMAIL)
                                                @EmptyNotNull(message = EMAIL_NOT_NULL)
                                                @Email String email)
    {
        logger.info("Fetching HR dashboard graph information of user with email : {}",email);
        return surveyDashboardService.hrDashboardGraph(email);
    }

    /**
     * For showing information needed for the Manager dashboard
     *
     * @param email : email of the logged in user.
     * @return Map<String, Integer> -->  Containing information required for manager dashboard cards.
     */
    //ManagerDashboard
    @GetMapping(value = "/managerDashboard",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For showing information needed for the Manager dashboard.")
    public Map<String, Integer> manager(@RequestParam(name = EMAIL)
                                        @EmptyNotNull(message = EMAIL_NOT_NULL)
                                        @Email String email)
    {
        logger.info("Fetching manager dashboard information of user with email : {}",email);
        return surveyDashboardService.managerDashboard(email);
    }

    /**
     * HR dashboard total surveys card.
     *
     * @param page : current page number.
     * @param pageSize : Number of object per page defaultSize--> 10.
     * @return : List<StatusResponseFilteredResponse>
     */
    //HR Dashboard
    @GetMapping(value = "/totalSurveys",produces = {"application/json"})
    @ApiOperation(value = "API for HR dashboard total surveys card")
    public MappingJacksonValue totalSurveys(@RequestParam(name = PAGE)
                                            @NotNull(message = PAGE_NOT_NULL) Integer page,
                                            @RequestParam(defaultValue = PAGE_SIZE_10,required = false) Integer pageSize){
        logger.info("Fetching total io.springboot.survey created");
        return surveyDashboardService.totalSurveys(page,pageSize);
    }

    /**
     * HR dashboard surveys this week card
     *
     * @param page :current page number.
     * @param pageSize :Number of object per page defaultSize--> 10.
     * @return List<StatusResponseFilteredResponse>
     */
    //HR Dashboard
    @GetMapping(value = "/surveysThisWeek",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "API for HR dashboard surveys this week card")
    public MappingJacksonValue surveysInWeek(@RequestParam(PAGE)
                                             @NotNull(message = PAGE_NOT_NULL) Integer page,
                                             @RequestParam(defaultValue =PAGE_SIZE_10,required = false) Integer pageSize){
        logger.info("Fetching io.springboot.survey created in past one week");
        return surveyDashboardService.surveysInWeek(page,pageSize);
    }

    /**
     * Manager dashboard surveys created by me this week card
     *
     * @param email : email of the logged in user.
     * @param page : current page number.
     * @param pageSize : Number of object per page defaultSize--> 10.
     * @return : List<StatusResponseFilteredResponse>
     */
    //Manager Dashboard
    @GetMapping(value = "/mySurveysThisWeek",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "API for Manager dashboard surveys created by me this week card")
    public MappingJacksonValue surveysInWeek(@RequestParam(name = EMAIL)
                                             @EmptyNotNull(message = EMAIL_NOT_NULL)
                                             @Email String email,
                                             @RequestParam(name = PAGE)
                                             @NotNull(message = PAGE_NOT_NULL) Integer page,
                                             @RequestParam(defaultValue = PAGE_SIZE_10, required = false) Integer pageSize)
    {
        logger.info("Fetching io.springboot.survey created in past one week by user with email : {}",email);
        return surveyDashboardService.mySurveysInWeek(email,page,pageSize);
    }


}
