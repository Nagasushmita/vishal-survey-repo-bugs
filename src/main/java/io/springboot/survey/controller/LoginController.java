package io.springboot.survey.controller;

import io.springboot.survey.annotation.APIResponseNoHeader;
import io.springboot.survey.annotation.Email;
import io.springboot.survey.annotation.EmptyNotNull;
import io.springboot.survey.annotation.IgnoreHeader;
import io.springboot.survey.request.OtpRequest;
import io.springboot.survey.response.AuthenticationResponse;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.EMAIL_NOT_NULL;
import static io.springboot.survey.utils.Constants.ValidationConstant.EMAIL;


@RestController
@Api("LoginController")
@Validated
@RequestMapping("/surveyManagement/v1")
public class LoginController {

    private final UserService userService;

    private final Logger logger= LoggerFactory.getLogger(LoginController.class.getSimpleName());

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Checking if the user is present in the database and sending otp if present.
     * @param email : email to which the otp is to be sent.
     * @return ResponseEntity<ResponseMessage>
     */
    @GetMapping(value = "/login/email",produces = { "application/json" })
    @IgnoreHeader()
    @APIResponseNoHeader()
    @ApiOperation(value = "Checking if the user is present in the database")
    public ResponseEntity<ResponseMessage> sendEmail(@RequestParam(name = EMAIL)
                                                  @EmptyNotNull(message = EMAIL_NOT_NULL)
                                                  @Email String email) {
       logger.info("Checking if user with email {} is present",email);
       return userService.sendEmail(email);
    }

    /**
     * Checking the otp if matched then generate jwt token for the user
     * @param otp : otp sent to the email of user.
     * @return ResponseEntity<AuthenticationResponse>
     */
    @PostMapping(value = "/login/email/otp",produces = { "application/json" },consumes ={ "application/json" })
    @IgnoreHeader()
    @APIResponseNoHeader()
    @ApiOperation(value = "Checking the otp if matched then generate jwt token for the user")
    public ResponseEntity<AuthenticationResponse> checkOtp(@Valid @RequestBody OtpRequest otp) {
        logger.info("Checking otp : {}",otp);
        return userService.userAuthentication(otp);
    }
    }

