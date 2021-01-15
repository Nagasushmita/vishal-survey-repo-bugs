package io.springboot.survey.controller;


import io.springboot.survey.annotation.APIResponseNoHeader;
import io.springboot.survey.annotation.APIResponseOk;
import io.springboot.survey.annotation.EmptyNotNull;
import io.springboot.survey.annotation.IgnoreHeader;
import io.springboot.survey.request.PrivilegesRequest;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.service.PrivilegesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.ROLE_NAME_NOT_NULL;
import static io.springboot.survey.utils.Constants.ValidationConstant.ROLE_NAME;

@RestController
@Validated
@Api("PrivilegesController")
@RequestMapping("/surveyManagement/v1")
public class PrivilegesController {

    final PrivilegesService privilegesService;

    public PrivilegesController( PrivilegesService privilegesService) {
        this.privilegesService = privilegesService;
    }

    private final Logger logger= LoggerFactory.getLogger(PrivilegesController.class.getSimpleName());

    /**
     * Map privileges to the roles
     * @param privilegesRequest PrivilegesRequest object
     * @return ResponseEntity<ResponseMessage>
     */
    @PostMapping(value = "/role/privileges",produces = { "application/json" },consumes ={ "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "Map privileges to the roles")
    public ResponseEntity<ResponseMessage> mappingPrivileges(@Valid @RequestBody PrivilegesRequest privilegesRequest) {

        logger.info("Mapping privilege : {}",privilegesRequest);
        return privilegesService.mapPrivileges(privilegesRequest);
    }

    /**
     * Show all the roles with their privileges
     *
     * @return all the role with their privileges
     */
    @GetMapping(value = "/all-roles",produces = { "application/json" })
    @IgnoreHeader()
    @APIResponseNoHeader()
    @ApiOperation(value = "Show all the roles with their privileges")
    public List<PrivilegesRequest> findAll()
    {
        logger.info("Fetching all roles with their privileges");
        return privilegesService.showAllPrivileges();
    }

    /**
     * Show privileges of a particular role
     * @param roleName :name of role
     * @return privileges associated with the role
     */
    @GetMapping(value = "/employee/role/privileges",produces = { "application/json" })
    @APIResponseNoHeader()
    @IgnoreHeader()
    @ApiOperation(value = "Show privileges of a particular role")
    public PrivilegesRequest getPrivilegeByRoleName(@RequestParam(name = ROLE_NAME)
                                                      @EmptyNotNull(message = ROLE_NAME_NOT_NULL) String roleName)
    {
        logger.info("Fetching privileges of role : {}",roleName);
        return privilegesService.showPrivileges(roleName);
    }
}