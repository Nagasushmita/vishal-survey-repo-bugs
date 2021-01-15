package io.springboot.survey.impl;

import io.springboot.survey.exception.BadRequestException;
import io.springboot.survey.exception.ForbiddenException;
import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.models.RoleModel;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.RoleRepo;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.springboot.survey.utils.Constants.CommonConstant.*;
import static io.springboot.survey.utils.Constants.ErrorMessageConstant.*;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;

@Component
public class RoleServiceImplementation implements RoleService {

    private final RoleRepo roleRepo;

    private final UserRepo userRepo;

    private static final Logger logger= LoggerFactory.getLogger(RoleServiceImplementation.class.getSimpleName());

    public RoleServiceImplementation(RoleRepo roleRepo, UserRepo userRepo) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
    }


    /**
     * Get all the roles
     *
     * @return  List<RoleModel>
     */
    @Override
    public List<RoleModel> getAllRoles() {

        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return roleRepo.findAll();
    }

    /**
     * Add a new role
     *
     * @param roleName : name of role.
     * @param email : email of logged in user
     * @return  ResponseEntity<ResponseMessage>
     */
    @Override
    public ResponseEntity<ResponseMessage> addRole(String roleName, String email) {
    logger.info(STARTING_METHOD_EXECUTION);
    ResponseMessage responseMessage = new ResponseMessage();
    RoleModel roleModel=roleRepo.findByRole(roleName);
    if (roleModel == null) {
        RoleModel obj = new RoleModel();
        obj.setRole(roleName);
        obj.setCreatedOn(System.currentTimeMillis());
        obj.setCreatedBy(userRepo.getUserIdByUserEmail(email));
        roleRepo.save(obj);
        logger.debug("Role saved : {}",roleName);
        responseMessage.setMessage(ROLE_ADDED);
        responseMessage.setStatusCode(HttpStatus.CREATED.value());
        logger.info(EXITING_METHOD_EXECUTION);
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }
    responseMessage.setMessage(ROLE_ALREADY_EXIST);
    responseMessage.setStatusCode(HttpStatus.CONFLICT.value());
    logger.debug("Role {} already exits",roleName);
    logger.info(EXITING_METHOD_EXECUTION);
    return new ResponseEntity<>(responseMessage, HttpStatus.CONFLICT);
    }

    /**
     * Delete a role
     *
     * @param roleName : name of role
     * @return ResponseEntity<Void> --> 204 No Content
     */
    @Override
    public ResponseEntity<Void> deleteRole(String roleName) {
        logger.info(STARTING_METHOD_EXECUTION);
        switch (roleName) {
            case HR:
            case MANAGER:
            case EMPLOYEE:
                    throw new ForbiddenException(ROLE_CANNOT_BE_DELETED);
            default:
            RoleModel roleModel = roleRepo.findByRole(roleName);
            if (roleModel == null) {
                List<UserModel> userModelList = userRepo.findByRoleId(roleRepo.getRoleIdByRole(roleName));
                long d;
                    d = roleRepo.deleteByRole(roleName);
                if (d == 1) {
                    for (UserModel list : userModelList) {
                        list.setRoleId(roleRepo.getRoleIdByRole(EMPLOYEE));
                        userRepo.save(list);
                    }
                    logger.info(EXITING_METHOD_EXECUTION);
                    return ResponseEntity.noContent().build();
                } else {
                    logger.debug("Role {} not deleted",roleName);
                    logger.info(EXITING_METHOD_EXECUTION);
                   throw new BadRequestException(ERROR_MESSAGE);
                }
            }
                logger.debug("Role {} not found",roleName);
                logger.info(EXITING_METHOD_EXECUTION);
                throw new ResourceNotFoundException(ROLE_NOT_FOUND);
        }
    }
}

