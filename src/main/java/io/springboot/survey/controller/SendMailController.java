package io.springboot.survey.controller;

import io.springboot.survey.annotation.APIResponseOk;
import io.springboot.survey.request.EmailRequest;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.service.MailService;
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
@Api("SendMailController")
@Validated
@RequestMapping("/surveyManagement/v1")
public class SendMailController {

    private final MailService mailService;
    private final Logger logger= LoggerFactory.getLogger(SendMailController.class.getSimpleName());

    public SendMailController(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * To send reminder email to those who have not filled their surveys
     * @param emailRequest :Object of EmailRequest.
     * @return ResponseEntity<ResponseMessage>
     */
    @PostMapping(value = "/survey/reminder",produces = { "application/json" },consumes ={ "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "To send reminder email to those who have not filled their surveys")
    public ResponseEntity<ResponseMessage> sendReminderEmail(@Valid @RequestBody EmailRequest emailRequest) {

        logger.info("Sending reminder email : {}",emailRequest);
       return mailService.sendEmail(emailRequest,true);
    }

    /**
     * To send mails to notify that a new io.springboot.survey has been assigned.
     * @param emailRequest : Object of EmailRequest.
     * @return ResponseEntity<ResponseMessage>
     */
    @APIResponseOk()
    @PostMapping(value = "/survey/send",produces = { "application/json" },consumes ={ "application/json" })
    @ApiOperation(value = "To send mails to notify that a new io.springboot.survey has been assigned.")
    public ResponseEntity<ResponseMessage> sendEmail(@Valid @RequestBody EmailRequest emailRequest) {
        logger.info("Sending email : {}",emailRequest);
        return mailService.sendEmail(emailRequest,false);
    }
}


