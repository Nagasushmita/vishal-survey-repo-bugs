package io.springboot.survey.controller;

import io.springboot.survey.annotation.*;
import io.springboot.survey.exception.CustomRetryException;
import io.springboot.survey.pojo.GetRequestParam;
import io.springboot.survey.request.CreateSurveyRequest;
import io.springboot.survey.request.DeleteArchiveRequest;
import io.springboot.survey.request.UserSurveyRequest;
import io.springboot.survey.response.GetSurveyResponse;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.response.SurveyResponse;
import io.springboot.survey.response.UploadFileResponse;
import io.springboot.survey.service.SurveyCrudService;
import io.springboot.survey.service.SurveyResponseService;
import io.springboot.survey.service.SurveyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.*;
import static io.springboot.survey.utils.Constants.ValidationConstant.*;


@RestController
@Validated
@Api("SurveyController")
@RequestMapping("/surveyManagement/v1")
public class SurveyController {
    private final SurveyService surveyService;
    private final SurveyCrudService surveyCrudService;
    private final SurveyResponseService surveyResponseService;

    public SurveyController(SurveyService surveyService, SurveyCrudService surveyCrudService, SurveyResponseService surveyResponseService) {
        this.surveyService = surveyService;
        this.surveyCrudService = surveyCrudService;
        this.surveyResponseService = surveyResponseService;
    }

    private final Logger logger= LoggerFactory.getLogger(SurveyController.class.getSimpleName());

    /**
     * Create a new io.springboot.survey
     * @param createSurveyRequest : CreateSurveyRequest Object
     * @return ResponseEntity<ResponseMessage> -->HttpStatus.CREATED
     */
    @PostMapping(value = "/survey/create",produces = { "application/json" },consumes ={ "application/json" })
    @ApiOperation(value = "Creating a new io.springboot.survey")
    @APIResponseCreated()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseMessage> createSurvey(@Valid @RequestBody CreateSurveyRequest createSurveyRequest) {
        logger.info("Creating Survey : {}",createSurveyRequest);
        return surveyCrudService.createSurvey(createSurveyRequest);
    }


    /**
     * Archiving a  io.springboot.survey
     * @param deleteArchiveRequest : DeleteArchiveRequest
     * @return ResponseEntity<ResponseMessage
     */
    @PutMapping(value = "/survey/archive",produces = { "application/json" },consumes ={ "application/json" })
    @ApiOperation(value = "Archiving a particular io.springboot.survey")
    @APIResponseOk()
    public ResponseEntity<ResponseMessage> archiveSurvey(@Valid @RequestBody DeleteArchiveRequest deleteArchiveRequest) {
        logger.info("Fetching and archiving Survey : {}",deleteArchiveRequest);
        return surveyCrudService.archiveSurvey(deleteArchiveRequest);
    }


    /**
     * Un-Archiving an archived io.springboot.survey
     * @param deleteArchiveRequest :DeleteArchiveRequest
     * @return : ResponseEntity<ResponseMessage
     */
    @PutMapping(value = "/survey/unarchive",produces = { "application/json" },consumes ={ "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "Unarchive an archived io.springboot.survey")
    public ResponseEntity<ResponseMessage> unarchiveSurvey(@Valid @RequestBody DeleteArchiveRequest deleteArchiveRequest) {
        logger.info("Fetching and unArchiving Survey : {}",deleteArchiveRequest);
         return surveyCrudService.unarchiveSurvey(deleteArchiveRequest);
    }

    /**
     * Deleting a particular io.springboot.survey
     *
     * @param deleteArchiveRequest : DeleteArchiveRequest.
     * @return : ResponseEntity<Void> --> No Content.
     */
    @DeleteMapping("/survey")
    @ApiOperation(value = "Deleting a particular io.springboot.survey")
    @APIResponseOk()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteSurvey(@Valid @RequestBody DeleteArchiveRequest deleteArchiveRequest) {
        logger.info("Fetching and deleting Survey : {}",deleteArchiveRequest);
       return surveyCrudService.deleteSurvey(deleteArchiveRequest);
    }

    /**
     * Taking Response of the user
     *
     * @param userSurveyRequest : UserSurveyRequest
     * @return ResponseEntity<ResponseMessage>
     */
    //Take Survey
    @PostMapping(value = "/user/submit-io.springboot.survey",produces = { "application/json" },consumes ={ "application/json" })
    @ApiOperation(value = "Taking response of the user")
    @APIResponseOk()
    public ResponseEntity<ResponseMessage> surveyResponse(@Valid @RequestBody UserSurveyRequest userSurveyRequest) {
        logger.info("Submitting io.springboot.survey response : {}",userSurveyRequest);
        return surveyResponseService.surveyResponse(userSurveyRequest);
    }


    /**
     * Showing all the question along with the answers and the question type of a particular io.springboot.survey
     *
     * @param surveyName : name of the io.springboot.survey.
     * @param creatorEmail: email of  creator.
     * @return List<GetSurveyResponse> : list of getSurveyResponse
     */
    @GetMapping(value = "/io.springboot.survey-preview",produces = { "application/json" })
    @IgnoreHeader()
    @APIResponseNoHeader()
    @ApiOperation(value = "Showing all the question along with the answers and the question type of a particular io.springboot.survey")
    public List<GetSurveyResponse> getSurvey(@org.springframework.web.bind.annotation.RequestParam(name = SURVEY_NAME)
                                            @EmptyNotNull(message = SURVEY_NOT_NULL) String surveyName,
                                            @org.springframework.web.bind.annotation.RequestParam(name = CREATOR_EMAIL)
                                            @EmptyNotNull(message = CREATOR_EMAIL_NOT_NULL)
                                            @Email String creatorEmail) {
       logger.info("Fetching io.springboot.survey with surveyName {} and creatorEmail {}",surveyName,creatorEmail);
       return surveyResponseService.getSurvey(surveyName,creatorEmail);
    }

    /**
     * For getting all the surveys created by a particular user
     *
     * @param email : email of the logged in user.
     * @param page : current page number.
     * @param pageSize : number of io.springboot.survey per page defaultSize --> 4.
     * @param sortBy : field by which sorting is to be done defaultValue --> creationDate.
     * @return : List of io.springboot.survey created by the user.
     */
    @GetMapping(value = "/surveys",produces = { "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "For getting all the surveys created by a particular user")
    public MappingJacksonValue getAllSurvey(@org.springframework.web.bind.annotation.RequestParam(name = EMAIL)
                                            @EmptyNotNull(message = EMAIL_NOT_NULL) @Email String email,
                                            @org.springframework.web.bind.annotation.RequestParam(name = PAGE) @NotNull(message = PAGE_NOT_NULL) int page,
                                            @org.springframework.web.bind.annotation.RequestParam(defaultValue =PAGE_SIZE_4,required = false) Integer pageSize,
                                            @org.springframework.web.bind.annotation.RequestParam(defaultValue = CREATION_DATE,required = false) String sortBy)
    {
        logger.info("Fetching io.springboot.survey of user with email : {}",email);
        return surveyService.getAllSurvey(new GetRequestParam(email,page,pageSize ,sortBy));
    }

    /**
     * For getting all the archived surveys of a user
     *
     * @param email : email of the logged in user.
     * @param page :  current page number.
     * @param pageSize :number of io.springboot.survey per page defaultSize --> 8.
     * @param sortBy : field by which sorting is to be done defaultValue --> creationDate
     * @return  : List of archived io.springboot.survey of a user.
     */
    @GetMapping(value = "/surveys/archive",produces = { "application/json" })
    @APIResponseOk()
    @ApiOperation("For getting all the archived surveys of a user")
    public MappingJacksonValue getArchivedSurvey(@org.springframework.web.bind.annotation.RequestParam(name = EMAIL)
                                                 @EmptyNotNull(message = EMAIL_NOT_NULL) @Email String email,
                                                 @org.springframework.web.bind.annotation.RequestParam(name = PAGE) @NotNull(message = PAGE_NOT_NULL) int page,
                                                 @org.springframework.web.bind.annotation.RequestParam(defaultValue = PAGE_SIZE_8,required = false) Integer pageSize,
                                                 @org.springframework.web.bind.annotation.RequestParam(defaultValue = CREATION_DATE,required = false) String sortBy)
    {
      logger.info("Fetching archived io.springboot.survey of user with email : {}",email);

      return surveyService.getArchivedSurvey(new GetRequestParam(email,page,pageSize,sortBy));
    }

    /**
     * For getting the io.springboot.survey using the link
     *
     * @param link : link of the io.springboot.survey.
     * @return : Survey
     */
    //Take Survey
    @GetMapping(value = "/io.springboot.survey-link",produces = { "application/json" })
    @APIResponseNoHeader()
    @IgnoreHeader()
    @ApiOperation("For getting the io.springboot.survey using the link")
    public List<GetSurveyResponse> getSurveyByLink(@org.springframework.web.bind.annotation.RequestParam(name = LINK)
                                                      @EmptyNotNull(message = LINK_NOT_NULL) String link)
    {
        logger.info("Fetching io.springboot.survey with link : {}",link);
        return surveyResponseService.getSurveyByLink(link);
    }

    /**
     * For extracting the surveyName and creatorEmail from the io.springboot.survey link
     *
     * @param link : link of the io.springboot.survey.
     * @param teamName : name of the team.
     * @return : SurveyResponse.
     */
    @GetMapping(value = "/decode-link",produces = {"application/json"})
    @IgnoreHeader()
    @APIResponseNoHeader()
    @ApiOperation("For extracting the surveyName and creatorEmail from the io.springboot.survey link")
    public SurveyResponse decode(@org.springframework.web.bind.annotation.RequestParam(name = LINK)
                         @EmptyNotNull(message = LINK_NOT_NULL) String link,
                         @org.springframework.web.bind.annotation.RequestParam(name =TEAM_NAME)
                         @EmptyNotNull(message = TEAM_NAME_NOT_NULL) String teamName)
    {
        logger.info("Fetching io.springboot.survey details with teamName {} and link {}",teamName,link);
        return surveyService.decodeLink(link,teamName);

    }

    /**
     * Uploading a file while responding to a io.springboot.survey
     *
     * @param file :MultiPartFile
     * @return : UploadFileResponse when the file is uploaded successfully.
     * @throws IOException : IOException
     * @throws CustomRetryException : CustomRetryException
     */
    //Take Survey
    @PostMapping(value = "/user/upload-file")
    @APIResponseOk()
    @ApiOperation("For uploading a file while responding to a io.springboot.survey")
    public UploadFileResponse uploadFile(@org.springframework.web.bind.annotation.RequestParam(name = FILE) @ValidFile() MultipartFile file) throws IOException {
        logger.info("Uploading file");
        return surveyService.uploadFile(file);
    }

    /**
     * @param templateName :name of template.
     * @return : information about the reports generated by using a particular template.
     */
    @GetMapping(value = "/survey/report-type",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation("For Providing the information about the reports generated by using a particular template")
    public Map<String, Integer> tooltip(@org.springframework.web.bind.annotation.RequestParam(name = TEMPLATE_NAME)
                                        @EmptyNotNull(message = TEMPLATE_NAME_NOT_NULL) @Template String templateName)
    {
        logger.info("Fetching report type information for templateName : {}",templateName);
        return surveyService.tooltip(templateName);
    }

}

