package io.springboot.survey.controller;

import io.springboot.survey.annotation.APIResponseCreated;
import io.springboot.survey.annotation.APIResponseForbidden;
import io.springboot.survey.annotation.APIResponseOk;
import io.springboot.survey.annotation.EmptyNotNull;
import io.springboot.survey.models.RoleModel;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.EMAIL_NOT_NULL;
import static io.springboot.survey.utils.Constants.NullEmptyConstant.ROLE_NAME_NOT_NULL;
import static io.springboot.survey.utils.Constants.ValidationConstant.EMAIL;
import static io.springboot.survey.utils.Constants.ValidationConstant.ROLE_NAME;

@RestController
@Validated
@Api("RoleController")
@RequestMapping("/surveyManagement/v1")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    private final Logger logger= LoggerFactory.getLogger(RoleController.class.getSimpleName());

    /**
     * @return all the roles.
     */
    @GetMapping(value = "/employee/roles",produces = { "application/json" })
    @APIResponseOk()
    @ApiOperation(value = "View all the roles")
    public List<RoleModel> getRoles() {
        logger.info("Fetching all roles");
        return roleService.getAllRoles();
    }

    /**
     * Add new role.
     * @param roleName : name of the role.
     * @param email: email of the logged in user.
     * @return  ResponseEntity<ResponseMessage>
     */
    @PostMapping(value = "/role",produces = { "application/json" },consumes ={ "application/json" } )
    @ApiOperation(value = "Add a new role" )
    @APIResponseCreated()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseMessage>  addRole(@RequestParam(name = ROLE_NAME)
                                                   @EmptyNotNull(message = ROLE_NAME_NOT_NULL) String roleName,
                                                    @RequestParam(name = EMAIL) @EmptyNotNull(message = EMAIL_NOT_NULL)
                                                    String email) {
       logger.info("Creating role with roleName : {}",roleName);
       return roleService.addRole(roleName,email);
    }

    /**
     * Delete a particular role.
     * @param roleName : name of the role to be deleted.
     * @return 204 - No Content.
     */
    @ApiOperation(value = "Delete a particular role")
    @DeleteMapping("/role")
    @APIResponseForbidden()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteRole(@RequestParam(name = ROLE_NAME)
                           @EmptyNotNull(message = ROLE_NAME_NOT_NULL) String roleName) {
         logger.info("Fetching and deleting role with roleName : {}",roleName);
         return roleService.deleteRole(roleName);
    }





}
