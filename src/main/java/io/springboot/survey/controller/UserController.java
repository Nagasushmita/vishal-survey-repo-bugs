package io.springboot.survey.controller;

import io.springboot.survey.annotation.*;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.pojo.GetRequestParam;
import io.springboot.survey.pojo.user.DynamicSearchParam;
import io.springboot.survey.pojo.user.GetAllParam;
import io.springboot.survey.request.AddUserRequest;
import io.springboot.survey.request.ModifyUserRequest;
import io.springboot.survey.request.UpdateUsersRequest;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.response.UserFilter;
import io.springboot.survey.service.UserService;
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
import java.util.HashMap;
import java.util.List;

import static io.springboot.survey.utils.Constants.CommonConstant.USER_NAME;
import static io.springboot.survey.utils.Constants.NullEmptyConstant.*;
import static io.springboot.survey.utils.Constants.TeamConstants.ACTIVE;
import static io.springboot.survey.utils.Constants.ValidationConstant.*;

@Validated
@RestController
@Api(value = "userController")
@RequestMapping("/surveyManagement/v1")
public class UserController {

    private final UserService userService;
    private final Logger logger= LoggerFactory.getLogger(UserController.class.getSimpleName());

    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * For Adding a new user in the central database
     *
     * @param addUserRequest : AddUserRequest.
     * @return : ResponseEntity<ResponseMessage>
     */
    @PostMapping(value = "/employee",produces = { "application/json" },consumes ={ "application/json" })
    @APIResponseCreated()
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "For Adding a new user in the central database")
    public ResponseEntity<ResponseMessage> addUser(@Valid @RequestBody AddUserRequest addUserRequest) {
        logger.info("Creating user(s) : {}",addUserRequest);
        return userService.addUser(addUserRequest);
    }

    /**
     * For deleting an user from the central database
     *
     * @param modifyUserRequest :ModifyUserRequest.
     * @param email : email of the logged in user.
     * @return : ResponseEntity<Void> --> 204 No Content.
     */
    @DeleteMapping("/employee")
    @APIResponseOk()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "For deleting an user from the central database")
    public ResponseEntity<Void> deleteUser(@Valid @RequestBody ModifyUserRequest modifyUserRequest,
                                        @RequestParam(EMAIL)
                                        @EmptyNotNull(message = EMAIL_NOT_NULL)
                                        @Email String email) {
        logger.info("Fetching and deleting user(s) : {}",modifyUserRequest);
       return userService.deleteUsers(modifyUserRequest,email);
    }

    /**
     * For soft deleting an user from the central database
     *
     * @param modifyUserRequest :ModifyUserRequest.
     * @return : ResponseEntity<ResponseMessage>
     */
    @PutMapping(value = "/employee/disable",produces = { "application/json" },consumes ={ "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "For deleting an user from the central database")
    public ResponseEntity<ResponseMessage> softDelete(@RequestBody @Valid ModifyUserRequest modifyUserRequest) {
        logger.info("Fetching and disabling user(s) : {}",modifyUserRequest);
      return userService.softDeleteUser(modifyUserRequest);
    }

    /**
     * For updating the role of a particular user
     *
     * @param updateUsersRequest : UpdateUsersRequest.
     * @return :  ResponseEntity<ResponseMessage
     */
    @PutMapping(value = "/employee/update",produces = { "application/json" },consumes ={ "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "For updating the role of a particular user")
    public ResponseEntity<ResponseMessage> updateUser(@Valid @RequestBody UpdateUsersRequest updateUsersRequest) {
        logger.info("Fetching and updating user(s) : {}",updateUsersRequest);
        return userService.updateRole(updateUsersRequest);
    }

    /**
     * @param email : email of the logged in user.
     * @param page : current page number.
     * @param pageSize : number of object per page defaultSize --> 10.
     * @param sortBy :  field by which sorting is to be done.
     * @return :  List<HashMap<String, String>> 
     */
    @GetMapping(value = "/employees",produces = { "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "For viewing all the employee")
    public List<HashMap<String, String>> viewUser(@RequestParam(name = EMAIL)
                                                             @EmptyNotNull(message = EMAIL_NOT_NULL) @Email String email,
                                                             @RequestParam(name = PAGE)
                                                             @NotNull(message = PAGE_NOT_NULL) Integer page,
                                                             @RequestParam(defaultValue = PAGE_SIZE_10, required = false) Integer pageSize,
                                                             @RequestParam(defaultValue = USER_NAME, required = false) String sortBy) {
        logger.info("Fetching all the users except user with email {}",email);
        return userService.viewUser(new GetRequestParam(email, page, pageSize, sortBy));
    }


    /**
     * For importing users to database in bulk
     *
     * @param addUserRequest : AddUserRequest.
     * @return : ResponseEntity<ResponseMessage>
     */
    @PostMapping(value = "/employees/import",produces = { "application/json" },consumes ={ "application/json" })
    @APIResponseCreated()
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "For importing users to database in bulk")
    public ResponseEntity<ResponseMessage> importUsers(@Valid @RequestBody AddUserRequest addUserRequest) {
        logger.info("Creating user(s) via excel : {}",addUserRequest);
        return userService.importUsers(addUserRequest);
    }

    /**
     * For Searching users in the database by their name
     *
     * @param email : email of the logged in user.
     * @param name : name or pattern to search.
     * @param active : search activeUser or InactiveUsers.
     * @param pageSize : number of object per page defaultSize --> 10.
     * @return : List<HashMap<String, String>>
     */
    @GetMapping(value = "/employee/search", produces = { "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "For Searching users in the database by their name")
    public List<HashMap<String, String>> dynamicSearch(@RequestParam(name = EMAIL) @EmptyNotNull(message = EMAIL_NOT_NULL)
                                                            @Email String email,
                                                            @RequestParam(name = NAME)
                                                            @EmptyNotNull(message = NAME_NOT_NULL) String name,
                                                            @RequestParam(name = ACTIVE) Boolean active,
                                                            @RequestParam(defaultValue = PAGE_SIZE_10, required = false) Integer pageSize) {
        logger.info( "Searching users in the database by their name");
        return userService.dynamicSearch(new DynamicSearchParam(email, name, pageSize, active));
    }


    /**
     * Get all the employee by their role
     *
     * @param role : name of the role.
     * @param email : email of the logged in user.
     * @return : List<UserModel>
     */
    @GetMapping(value = "/employees/filter/role",produces = { "application/json" })
    @IgnoreHeader()
    @APIResponseNoHeader()
    @ApiOperation(value = "Get all the employee by their role")
    public List<UserModel> getAllUsersByRole(@RequestParam(name = ROLE) String role,
                                             @RequestParam(name = EMAIL)
                                             @EmptyNotNull(message = EMAIL_NOT_NULL)@Email String email) {
        logger.info( "fetching all the users with role :{}",role);
        return userService.getAllUserByRole(role, email);
    }

    /**
     * To enable the disabled user again
     *
     * @param modifyUserRequest : ModifyUserRequest.
     * @return : ResponseEntity<ResponseMessage>
     */
    @PutMapping(value = "/employee/enable",produces = { "application/json" },consumes ={ "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "To enable the disabled user again.")
    public ResponseEntity<ResponseMessage> enableUser(@Valid @RequestBody ModifyUserRequest modifyUserRequest) {
        logger.info("Fetching and enabling user(s) : {}",modifyUserRequest);
        return userService.enableUser(modifyUserRequest);
    }

    /**
     * @param email : email of the logged in user.
     * @param page : current page number.
     * @param pageSize : number of object per page defaultSize --> 10.
     * @param sortBy :  field by which sorting is to be done.
     * @return : List<HashMap<String, String>>
     */
    @GetMapping(value = "/employees/disabled",produces = { "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "For viewing disabled employees")
    public List<HashMap<String, String>> viewDisabledUsers(@RequestParam(name = EMAIL)
                                                           @EmptyNotNull(message = EMAIL_NOT_NULL) @Email String email,
                                                           @RequestParam(name = PAGE)
                                                           @NotNull(message = PAGE_NOT_NULL) Integer page,
                                                           @RequestParam(defaultValue = PAGE_SIZE_10, required = false) Integer pageSize,
                                                           @RequestParam(defaultValue = USER_NAME, required = false) String sortBy) {
        logger.info("Fetching all disabled user(s)");
        return userService.viewDisabledUser(new GetRequestParam(email, page, pageSize, sortBy));
    }

    /**
     * @param email : email of the logged in user.
     * @param userFilter : UserFilter
     * @param page : current page number.
     * @param pageSize :  number of object per page defaultSize --> 10.
     * @param sortBy : field by which sorting is to be done.
     * @return : List<HashMap<String, String>>
     */
    //SurveyModule
    @PostMapping(value = "/survey/users/filter",produces = { "application/json" },consumes ={ "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "For getting the user by designation,gender,role and project")
    public MappingJacksonValue getAll(@RequestParam(name = EMAIL)
                                      @EmptyNotNull(message = EMAIL_NOT_NULL) @Email String email,
                                      @RequestBody(required = false) UserFilter userFilter,
                                      @RequestParam(name = PAGE) @NotNull(message = PAGE_NOT_NULL) Integer page,
                                      @RequestParam(defaultValue = PAGE_SIZE_10, required = false) Integer pageSize,
                                      @RequestParam(defaultValue = USER_NAME, required = false) String sortBy) {
        logger.info("Fetching the user(s) with filter : {}",userFilter);
        return userService.getAll(new GetAllParam(email, userFilter, page, pageSize, sortBy));
    }

    /**
     * For getting information about the filter that are used while assigning the io.springboot.survey to the users
     *
     * @return Object.
     */
    @GetMapping(value = "/filter/info",produces = { "application/json" })
    @IgnoreHeader()
    @APIResponseNoHeader()
    @ApiOperation(value = "For getting information about the filter that are used while assigning the io.springboot.survey to the users ")
    public Object getFilterInfo() {
        logger.info("Fetching information about filter");
        return userService.getFilterInfo();
    }

}