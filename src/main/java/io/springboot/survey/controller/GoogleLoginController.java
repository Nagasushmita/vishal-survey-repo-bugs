package io.springboot.survey.controller;


import io.springboot.survey.annotation.APIResponseNoHeader;
import io.springboot.survey.annotation.Email;
import io.springboot.survey.annotation.EmptyNotNull;
import io.springboot.survey.annotation.IgnoreHeader;
import io.springboot.survey.response.AuthenticationResponse;
import io.springboot.survey.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.EMAIL_NOT_NULL;
import static io.springboot.survey.utils.Constants.ValidationConstant.EMAIL;

@RestController
@Api("GoogleLoginController")
@Validated
@RequestMapping("/surveyManagement/v1")
public class
GoogleLoginController {

    private final UserService userService;

   private final Logger logger= LoggerFactory.getLogger(GoogleLoginController.class.getSimpleName());

    public GoogleLoginController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Generate the jwt if a user is authenticated via google sign in
     * @param email : email for which jwt token is to be generated.
     * @return ResponseEntity<AuthenticationResponse> containing jwt token and other basic
     * information about the user
     */

    @IgnoreHeader()
    @APIResponseNoHeader()
    @GetMapping(value = "/login/google",produces = { "application/json" })
    @ApiOperation(value = "Generate the jwt if a user is authenticated via google sign in")
    public ResponseEntity<AuthenticationResponse> googleLogin(@RequestParam(name = EMAIL)
                                                              @EmptyNotNull(message =EMAIL_NOT_NULL)
                                                              @Email String email ) {
        logger.info("Generating jwt for email : {}",email);
        return userService.googleLogin(email);
    }

}
