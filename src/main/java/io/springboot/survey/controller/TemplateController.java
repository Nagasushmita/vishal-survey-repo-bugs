package io.springboot.survey.controller;


import io.springboot.survey.annotation.*;
import io.springboot.survey.pojo.template.GetTemplateParam;
import io.springboot.survey.request.CreateTemplateRequest;
import io.springboot.survey.response.GetSurveyResponse;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.response.TemplateCountResponse;
import io.springboot.survey.response.TemplateInformation;
import io.springboot.survey.service.TemplateResponseService;
import io.springboot.survey.service.TemplateService;
import io.springboot.survey.service.TemplateServiceCrud;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.*;
import static io.springboot.survey.utils.Constants.TeamConstants.TEAM;
import static io.springboot.survey.utils.Constants.ValidationConstant.*;

@RestController
@Api("TemplateController")
@Validated
@RequestMapping("/surveyManagement/v1")
public class TemplateController {

    private final TemplateService templateService;

    private final TemplateServiceCrud templateServiceCrud;

    private final TemplateResponseService templateResponseService;

    private final Logger logger= LoggerFactory.getLogger(TemplateController.class.getSimpleName());

    public TemplateController(TemplateService templateService, TemplateServiceCrud templateServiceCrud, TemplateResponseService templateResponseService) {
        this.templateService = templateService;
        this.templateServiceCrud = templateServiceCrud;
        this.templateResponseService = templateResponseService;
    }

    /**
     * For creating a template
     *
     * @param createTemplateRequest : CreateTemplateRequest.
     * @return : ResponseEntity<ResponseMessage>
     */
    @PostMapping(value = "/template/create",produces = { "application/json" },consumes ={ "application/json" })
    @ApiOperation(value = "For creating a template")
    @APIResponseCreated()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseMessage> createTemplate(@Valid @RequestBody CreateTemplateRequest createTemplateRequest) {
        logger.info("Creating template : {}",createTemplateRequest);
        return templateServiceCrud.createTemplate(createTemplateRequest);
    }

    /**
     *For deleting a template
     *
     * @param templateName :name of the template
     * @return : ResponseEntity<Void> --> 204 No Content.
     */
    @DeleteMapping("/template")
    @APIResponseForbidden()
    @ApiOperation(value = "For deleting a template")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteTemplate(@RequestParam(name = TEMPLATE_NAME)
                                                       @EmptyNotNull(message = TEMPLATE_NAME_NOT_NULL)
                                                       @Template String templateName)
    {
        logger.info("Fetching and deleting template : {}",templateName);
        return templateServiceCrud.deleteTemplates(templateName);

    }

    /**
     * Archive a template
     *
     * @param templateName : name of the template.
     * @return : ResponseEntity<ResponseMessage>
     */
    @PutMapping(value = "/template/archive",produces = { "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "Archive a template")
    public ResponseEntity<ResponseMessage> archiveTemplate(@RequestParam(name = TEMPLATE_NAME)
                                                        @EmptyNotNull(message = TEMPLATE_NAME_NOT_NULL)
                                                        @Template String templateName) {
        logger.info("Fetching and archiving template : {}",templateName);
        return templateServiceCrud.archiveTemplate(templateName);
    }


    /**
     * For getting all the question and answer for a particular template.
     *
     * @param templateName : name of the template.
     * @return List<GetSurveyResponse>
     */
    @GetMapping(value = "/survey/template-preview",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For getting all the question and answer for a particular template")
    public List<GetSurveyResponse> getTemplateQuestionsAndAnswers(@RequestParam(name = TEMPLATE_NAME)
                                                                 @EmptyNotNull(message = TEMPLATE_NAME_NOT_NULL)
                                                                 @Template String templateName) {
        logger.info("Fetching the template : {}",templateName);
        return templateService.getTemplate(templateName);
    }

    /**
     * For getting all the templates
     *
     * @param email : email of the logged in user.
     * @param page : current page number.
     * @param pageSize : number of object per page defaultSize --> 8.
     * @param sortBy : field by which sorting is to be done.
     * @return : List<TemplateResponse>
     */
    //SurveyModule
    @GetMapping(value = "/survey/templates/all",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For getting all the templates")
    public MappingJacksonValue findAllTemplateByUserIdAndArchived(@RequestParam(name =EMAIL)
                                                                  @EmptyNotNull(message = EMAIL_NOT_NULL)
                                                                  @Email String email,
                                                                  @RequestParam(name = PAGE)
                                                                  @NotNull(message = PAGE_NOT_NULL) Integer page,
                                                                  @RequestParam(defaultValue = PAGE_SIZE_8,required = false) Integer pageSize,
                                                                  @RequestParam(defaultValue = CREATION_DATE,required = false) String sortBy) {
        logger.info("Fetching all the template  created by  : {}",email);
        return templateService.getAllTemplateByUserId(new GetTemplateParam(email,page,pageSize,sortBy));
    }

    /**
     * For getting all the Unarchived templates
     *
     * @param email : email of the logged in user.
     * @return : List<TemplateCountResponse>
     */
    //Survey Module
    @GetMapping(value = "/survey/templates",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For getting all the Unarchived templates")
    public List<TemplateCountResponse> getUnarchivedTemplate(@RequestParam(name =EMAIL)
                                                      @EmptyNotNull(message = EMAIL_NOT_NULL)
                                                      @Email String email) {
            logger.info("Fetching all the un-archived template  created by  : {}",email);
            return templateService.getUnarchivedTemplate(email);
    }

    /**
     * For getting all the templates created by a particular user
     *
     * @param email :email of the logged in user.
     * @param page : current page number.
     * @param pageSize :  number of object per page defaultSize --> 4.
     * @param sortBy : field by which sorting is to be done.
     * @return : List<TemplateResponse>
     */
    @GetMapping(value = "/templates",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For getting all the templates created by a particular user")
    public MappingJacksonValue findTemplateByUser(@RequestParam(name = EMAIL)
                                                  @EmptyNotNull(message = EMAIL_NOT_NULL)
                                                  @Email String email,
                                                  @RequestParam(name = PAGE)
                                                  @NotNull(message = PAGE_NOT_NULL) Integer page,
                                                  @RequestParam(defaultValue = PAGE_SIZE_4, required = false) Integer pageSize,
                                                  @RequestParam(defaultValue = CREATION_DATE, required = false) String sortBy) {
        logger.info("Fetching templates  created by  : {}",email);
        return templateService.getMyTemplate(new GetTemplateParam(email,page,pageSize,sortBy));
    }

    /**
     * For getting list of all the template that has been used to create a io.springboot.survey more than once
     *
     * @param page : current page number.
     * @param pageSize : number of object per page defaultSize --> 8.
     * @param sortBy : field by which sorting is to be done.
     * @return : List<TemplateResponse>
     */
    @GetMapping(value = "/template-report/templates/used",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For getting list of all the template that has been used to create a io.springboot.survey more than once")
    public MappingJacksonValue getUsedTemplates(@RequestParam(name = PAGE)
                                           @NotNull(message = PAGE_NOT_NULL)Integer page,
                                           @RequestParam(defaultValue = PAGE_SIZE_8,required = false) Integer pageSize,
                                           @RequestParam(defaultValue = CREATION_DATE,required = false) String sortBy) {

        logger.info("Fetching list of all the template that has been used to create a io.springboot.survey more than once");
        return templateService.getUsedTemplates(page,pageSize,sortBy);
    }

    /**
     * For getting all the response for a particular template
     *
     * @param templateName : name of the template.
     * @param team : name of the team.
     * @return : List<AllResponse> totalResponse
     */
    @GetMapping(value = "/template-report/responses",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For getting all the response for a particular template")
    public MappingJacksonValue templateResponses(@RequestParam(name = TEMPLATE_NAME)
                                               @EmptyNotNull(message = TEMPLATE_NAME_NOT_NULL)
                                                 @Template String templateName,
                                               @RequestParam(name = TEAM) boolean team) {
        logger.info("Fetching template : {} report",templateName);
        return templateResponseService.getAllTemplateResponse(templateName,team);
    }


    /**
     * Unarchive an archived template
     *
     * @param templateName : name of the template.
     * @return : ResponseMessage
     */
    @PutMapping(value = "/template/unarchive",produces = { "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "Unarchive an archived template")
    public ResponseMessage unarchiveTemplate(@RequestParam(name = TEMPLATE_NAME)
                                          @EmptyNotNull(message = TEMPLATE_NAME_NOT_NULL)
                                          @Template String templateName)
    {
        logger.info("Fetching and un-archiving template : {}",templateName);
        return templateServiceCrud.unarchiveTemplate(templateName);
    }

    /**
     * @param email : email of the logged in user.
     * @param page : current page number.
     * @param pageSize : number of object per page defaultSize --> 8.
     * @param sortBy : field by which sorting is to be done.
     * @return : List<TemplateResponse>
     */
    @GetMapping(value = "/templates/archive",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For getting all the archived templates")
    public MappingJacksonValue viewArchivedTemplate(@RequestParam(name = EMAIL)
                                                    @EmptyNotNull(message = EMAIL_NOT_NULL)
                                                    @Email String email,
                                                    @RequestParam(name = PAGE)
                                                    @NotNull(message = PAGE_NOT_NULL) Integer page,
                                                    @RequestParam(defaultValue = PAGE_SIZE_8, required = false) Integer pageSize,
                                                    @RequestParam(defaultValue = CREATION_DATE, required = false) String sortBy)
    {
        logger.info("Fetching all archived template created by: {}",email);
        return templateService.getArchivedTemplate(new GetTemplateParam(email,page,pageSize,sortBy));
    }


    /**
     * For showing the information about a template
     *
     * @param templateName : name of the template
     * @return : TemplateInformation
     */
    @GetMapping(value = "/template-report/details",produces = {"application/json"})
    @APIResponseOk()
    @ApiOperation(value = "For showing the information about a template")
    public TemplateInformation templateInformation(@RequestParam(name = TEMPLATE_NAME)
                                                   @EmptyNotNull(message = TEMPLATE_NAME_NOT_NULL)
                                                   @Template String templateName)
    {
        logger.info("Fetching the information of template : {}",templateName);
        return templateService.templateInformation(templateName);
    }

}
