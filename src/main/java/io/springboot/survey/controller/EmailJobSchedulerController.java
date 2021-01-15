package io.springboot.survey.controller;

import io.springboot.survey.annotation.APIResponseOk;
import io.springboot.survey.request.ScheduleEmailRequest;
import io.springboot.survey.response.ScheduleEmailResponse;
import io.springboot.survey.service.SchedulingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@Api(value = "EmailJobSchedulerController")
@Validated
@RequestMapping("/surveyManagement/v1")
public class EmailJobSchedulerController {

    private final SchedulingService schedulingService;

    private final Logger logger= LoggerFactory.getLogger(EmailJobSchedulerController.class.getSimpleName());
    public EmailJobSchedulerController(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }


    /**
     * Schedule a mail.
     * @param scheduleEmailRequest MailRequest
     * @return ResponseEntity<ScheduleEmailResponse> --> that the io.springboot.survey has been successfully scheduled.
     */
    @PostMapping(value = "/survey/schedule",produces = { "application/json" },consumes ={ "application/json" })
    @ApiOperation("API to schedule a mail")
    @APIResponseOk()
    public ResponseEntity<ScheduleEmailResponse> scheduleEmail(@Valid @RequestBody ScheduleEmailRequest scheduleEmailRequest) {

        logger.info("Scheduling Mail : {}",scheduleEmailRequest);
        return schedulingService.scheduleEmail(scheduleEmailRequest);
    }











}
