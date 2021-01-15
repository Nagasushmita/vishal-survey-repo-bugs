package io.springboot.survey.controller;

import io.springboot.survey.annotation.APIResponseOk;
import io.springboot.survey.annotation.Email;
import io.springboot.survey.annotation.EmptyNotNull;
import io.springboot.survey.pojo.survey.controller.GetSurveyInfoParam;
import io.springboot.survey.pojo.survey.controller.SurveyInfoParam;
import io.springboot.survey.request.UserRequest;
import io.springboot.survey.response.AssigneeInformationResponse;
import io.springboot.survey.response.QuestionResponse;
import io.springboot.survey.response.StatusFilteredResponse;
import io.springboot.survey.service.SurveyResponseService;
import io.springboot.survey.service.SurveySecondService;
import io.springboot.survey.service.SurveyStatusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.*;
import static io.springboot.survey.utils.Constants.ValidationConstant.*;

@RestController
@Api("SurveyStatusController")
@Validated
@RequestMapping("/surveyManagement/v1")
public class SurveyStatusController {

    final SurveySecondService surveySecondService;

    final SurveyResponseService surveyResponseService;

    final SurveyStatusService surveyStatusService;

    private final Logger logger= LoggerFactory.getLogger(SurveyStatusController.class.getSimpleName());

    public SurveyStatusController(SurveySecondService surveySecondService, SurveyResponseService surveyResponseService, SurveyStatusService surveyStatusService) {
        this.surveySecondService = surveySecondService;
        this.surveyResponseService = surveyResponseService;
        this.surveyStatusService = surveyStatusService;
    }


    /**
     * Getting the number of total,pending,taken io.springboot.survey for a particular user
     *
     * @param email : email of the logged in user.
     * @return : Map<String, Integer> :total,pending,taken io.springboot.survey for a particular user.
     */
    @GetMapping(value = "/user/dashboard",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "Getting the number of total,pending,taken io.springboot.survey for a particular user")
    public Map<String, Integer> getCount(@RequestParam(value = EMAIL)
                                         @EmptyNotNull(message = EMAIL_NOT_NULL)
                                         @Email String email)
    {
      logger.info("Fetching details for dashboard of user with email : {}",email);
      return surveySecondService.getCount(email);
    }

    /**
     * For showing info about all the io.springboot.survey taken by an user
     *
     * @param email : email of the logged in user.
     * @param page : current page number.
     * @param pageSize : number of object per page defaultSize --> 10.
     * @return :  List<StatusResponseFilteredResponse>
     */
    @GetMapping(value = "/user/surveys/taken",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For showing info about all the io.springboot.survey taken by an user")
    public MappingJacksonValue surveyTaken(@RequestParam(name = EMAIL)
                                           @EmptyNotNull(message = EMAIL_NOT_NULL)
                                           @Email String email,
                                           @RequestParam(name = PAGE)
                                           @NotNull(message = PAGE_NOT_NULL) Integer page,
                                           @RequestParam(defaultValue = PAGE_SIZE_10, required = false) Integer pageSize)
    {
        logger.info("Fetching details for io.springboot.survey taken by user with email : {}",email);
        return surveyStatusService.getSurveyTakenInfo(email, page,pageSize);
    }

    /**
     * For showing info about all the io.springboot.survey yet to be taken by an user
     *
     * @param email : email of the logged in user.
     * @param page : current page number.
     * @param pageSize : number of object per page defaultSize --> 10.
     * @return : List<StatusResponseFilteredResponse>
     */
    @GetMapping(value = "/user/surveys/pending",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For showing info about all the io.springboot.survey yet to be taken by an user")
    public MappingJacksonValue surveyPending(@RequestParam(name = EMAIL)
                                             @EmptyNotNull(message = EMAIL_NOT_NULL)
                                             @Email String email,
                                             @RequestParam(name = PAGE)
                                             @NotNull(message = PAGE_NOT_NULL) Integer page,
                                             @RequestParam(defaultValue = PAGE_SIZE_10, required = false) Integer pageSize)
    {
        logger.info("Fetching details for io.springboot.survey pending of user with email : {}",email);
        return surveyStatusService.getSurveyPendingInfo(email, page,pageSize);
    }

    /**
     * For showing info about all the active io.springboot.survey(s) for an user
     *
     * @param email : email of the logged in user.
     * @return List<StatusResponseFilteredResponse>
     */
    @GetMapping(value = "/user/surveys/active",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For showing info about all the active io.springboot.survey for an user")
    public List<StatusFilteredResponse> activeSurvey(@RequestParam(name = EMAIL)
                                                @EmptyNotNull(message = EMAIL_NOT_NULL)
                                                @Email String email)
    {
        logger.info("Fetching details for active io.springboot.survey of user with email : {}",email);
        return surveyStatusService.getActiveSurvey(email);
    }

    /**
     * For showing info about all the io.springboot.survey created by a particular user.
     *
     * @param creatorEmail : email of the logged in user.
     * @param page : current page number.
     * @param pageSize : number of object per page defaultSize --> 10.
     * @param sortBy : field by which sorting is to be done.
     * @return : List<StatusResponseFilteredResponse>
     */
    @GetMapping(value = "/surveys/info",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For showing info about all the io.springboot.survey created by a particular user")
    public MappingJacksonValue getSurveyInfo(@RequestParam(name = CREATOR_EMAIL)
                                             @EmptyNotNull(message = CREATOR_EMAIL_NOT_NULL)
                                             @Email(message = INVALID_CREATOR_EMAIL) String creatorEmail,
                                             @RequestParam(name = PAGE)
                                             @NotNull(message = PAGE_NOT_NULL) Integer page,
                                             @RequestParam(defaultValue = PAGE_SIZE_10,required = false) Integer pageSize,
                                             @RequestParam(defaultValue = CREATION_DATE,required = false) String sortBy)
    {
        logger.info("Fetching details of all io.springboot.survey by user with email : {}",creatorEmail);
        GetSurveyInfoParam surveyInfoParam=new GetSurveyInfoParam(creatorEmail,page,pageSize,sortBy);
        return surveyStatusService.getSurveyInfo(surveyInfoParam);
    }

    /**
     * For showing info about pending users for a particular io.springboot.survey.
     *
     * @param surveyName : name of the io.springboot.survey.
     * @param creatorEmail : email of the logged in user.
     * @param page : current page number.
     * @param pageSize :  number of object per page defaultSize --> 10.
     * @return List<PendingTakenResponse>
     */
    @GetMapping(value = "/survey/pending/info",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For showing info about pending users for a particular io.springboot.survey")
    public MappingJacksonValue getSurveyInfoPending(@RequestParam(name = SURVEY_NAME)
                                                    @EmptyNotNull(message = SURVEY_NOT_NULL)
                                                                String surveyName,
                                                    @RequestParam(name = CREATOR_EMAIL)
                                                    @EmptyNotNull(message = CREATOR_EMAIL_NOT_NULL)
                                                    @Email(message = INVALID_CREATOR_EMAIL) String creatorEmail,
                                                    @RequestParam(name = PAGE)
                                                    @NotNull(message = PAGE_NOT_NULL) Integer page,
                                                    @RequestParam(defaultValue = PAGE_SIZE_10, required = false) Integer pageSize) {
        logger.info("Fetching details about pending user for io.springboot.survey by user with email : {}",creatorEmail);
        SurveyInfoParam surveyInfoParam=new SurveyInfoParam(surveyName,creatorEmail,page,pageSize);
        return surveyStatusService.getSurveyInfoPending(surveyInfoParam);
    }

    /**
     * For showing info about the user(s) who have taken a particular io.springboot.survey
     *
     * @param surveyName : name of the io.springboot.survey.
     * @param creatorEmail : email of the logged in user.
     * @param page : current page number.
     * @param pageSize :  number of object per page defaultSize --> 10.
     * @return List<PendingTakenResponse>
     */

    @GetMapping(value = "survey/taken/info",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For showing info about io.springboot.survey taken for a particular io.springboot.survey")
    public MappingJacksonValue getSurveyInfoTaken(@RequestParam(name = SURVEY_NAME)
                                     @EmptyNotNull(message = SURVEY_NOT_NULL)  String surveyName,
                                     @RequestParam(name=CREATOR_EMAIL)
                                     @EmptyNotNull(message = CREATOR_EMAIL_NOT_NULL)
                                     @Email(message = INVALID_CREATOR_EMAIL) String creatorEmail,
                                     @RequestParam(name = PAGE)
                                     @NotNull(message = PAGE_NOT_NULL) Integer page,
                                     @RequestParam(defaultValue = PAGE_SIZE_10,required = false) Integer pageSize) {

        logger.info("Fetching details about taken user for io.springboot.survey by user with email : {}",creatorEmail);
        SurveyInfoParam surveyInfoParam=new SurveyInfoParam(surveyName,creatorEmail,page,pageSize);
        return surveyStatusService.getSurveyInfoTaken(surveyInfoParam);
    }

    /**
     * For showing info about the users assigned with a particular io.springboot.survey
     *
     * @param surveyName : name of the io.springboot.survey.
     * @param creatorEmail : email of the logged in user.
     * @param page : current page number.
     * @param pageSize :  number of object per page defaultSize --> 10.
     * @return List<PendingTakenResponse>
     */

    @GetMapping(value = "survey/assigned/users",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For showing info about the users assigned with a particular io.springboot.survey")
    public MappingJacksonValue getSurveyInfoAssigned(@RequestParam(name = SURVEY_NAME)
                                                     @EmptyNotNull(message = SURVEY_NOT_NULL)
                                                                 String surveyName,
                                                     @RequestParam(name = CREATOR_EMAIL)
                                                     @EmptyNotNull(message = CREATOR_EMAIL_NOT_NULL)
                                                     @Email(message = INVALID_CREATOR_EMAIL) String creatorEmail,
                                                     @RequestParam(name = PAGE)
                                                     @NotNull(message = PAGE_NOT_NULL) Integer page,
                                                     @RequestParam(defaultValue = PAGE_SIZE_10, required = false) Integer pageSize) {
        logger.info("Fetching details about assigned user for io.springboot.survey by user with email : {}",creatorEmail);
        SurveyInfoParam surveyInfoParam=new SurveyInfoParam(surveyName,creatorEmail,page,pageSize);
        return surveyStatusService.getSurveyInfoAssigned(surveyInfoParam);
    }

    /**
     * For showing the response of a particular user for a io.springboot.survey
     *
     * @param userRequest : UserRequest
     * @return :  List<QuestionResponse>
     */
    @PostMapping(value = "/user/response",produces = { "application/json" },consumes ={ "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "For showing the response of a particular user for a io.springboot.survey")
    public List<QuestionResponse> getUserResponse(@Valid @RequestBody UserRequest userRequest) {
        logger.info("Fetching io.springboot.survey response of  user : {}",userRequest);
        return surveyResponseService.getUserResponse(userRequest);
    }

    /**
     * For showing all the response for a io.springboot.survey
     *
     * @param surveyName : name of the io.springboot.survey.
     * @param creatorEmail : email of the logged in user.
     * @return List<AllResponse>
     */
    @GetMapping(value = "/survey/responses",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For showing all the response for a io.springboot.survey")
    public MappingJacksonValue getAllResponse(@RequestParam(name = SURVEY_NAME)
                                            @EmptyNotNull(message = SURVEY_NOT_NULL) String surveyName,
                                           @RequestParam(name = CREATOR_EMAIL)
                                            @EmptyNotNull(message = CREATOR_EMAIL_NOT_NULL)
                                            @Email(message = INVALID_CREATOR_EMAIL) String creatorEmail)
    {
        logger.info("Fetching all response of {} by user with email {}",surveyName,creatorEmail);
        return surveySecondService.getAllResponse(surveyName,creatorEmail);
    }

    /**
     * For showing the information such as io.springboot.survey taken,pending of a particular user
     *
     * @param email : email of the logged in user.
     * @return : List<AssigneeInformationResponse>
     */
    @GetMapping(value = "/user/assignee-info",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For showing the information such as io.springboot.survey taken,pending of a particular user")
    public List<AssigneeInformationResponse> surveyAssigneeInfo(@RequestParam(name = EMAIL)
                                                                @EmptyNotNull(message = EMAIL_NOT_NULL)
                                                                @Email String email)
    {
        logger.info("Fetching pending and taken io.springboot.survey of user with email : {}",email);
        return surveyStatusService.surveyAssigneeInfo(email);
    }


    /**
     *  employee dashboard total surveys card
     *
     * @param email : email of the logged in user.
     * @param page : current page number.
     * @param pageSize : number of object per page defaultSize --> 10.
     * @return List<StatusResponseFilteredResponse>
     */
    @GetMapping(value = "/user/surveys/assigned",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "API for employee dashboard total surveys card")
    public MappingJacksonValue totalAssignedSurveys(@RequestParam(name = EMAIL)
                                                     @EmptyNotNull(message = EMAIL_NOT_NULL)
                                                     @Email String email,
                                                    @RequestParam(name = PAGE)
                                                     @NotNull(message = PAGE_NOT_NULL) Integer page,
                                                    @RequestParam(defaultValue = PAGE_SIZE_10, required = false) Integer pageSize){
        logger.info("Fetching assigned io.springboot.survey of user with email : {}",email);
        return surveyStatusService.totalAssignedSurveys(email,page,pageSize);
    }

    /**
     * For showing all the information for a io.springboot.survey
     *
     * @param surveyName : name of the io.springboot.survey.
     * @param creatorEmail : email of the logged in user.
     * @return :  StatusResponse
     */
    @GetMapping(value = "/survey/info",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For showing all the information for a io.springboot.survey")
    public MappingJacksonValue surveyInformation(@RequestParam(name = SURVEY_NAME)
                                                 @EmptyNotNull(message = SURVEY_NOT_NULL) String surveyName,
                                                 @RequestParam(name = CREATOR_EMAIL)
                                                 @EmptyNotNull(message = CREATOR_EMAIL_NOT_NULL)
                                                 @Email(message = INVALID_CREATOR_EMAIL) String creatorEmail) {
        logger.info("Fetching information of {} by user with email {}",surveyName,creatorEmail);
        return surveyStatusService.surveyInformation(surveyName,creatorEmail);
    }



}
