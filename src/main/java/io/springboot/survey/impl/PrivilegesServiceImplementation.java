package io.springboot.survey.impl;

import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.models.PrivilegesModel;
import io.springboot.survey.models.RoleModel;
import io.springboot.survey.repository.PrivilegesRepo;
import io.springboot.survey.repository.RoleRepo;
import io.springboot.survey.request.PrivilegesRequest;
import io.springboot.survey.response.PrivilegeSet;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.service.PrivilegesService;
import io.springboot.survey.utils.Constants;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static io.springboot.survey.utils.Constants.ErrorMessageConstant.*;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.PrivilegesConstant.*;


@Component
public class PrivilegesServiceImplementation implements PrivilegesService {

    private final PrivilegesRepo privilegesRepo;
    private final RoleRepo roleRepo;
    private static final Logger logger= LoggerFactory.getLogger(PrivilegesServiceImplementation.class.getSimpleName());

    public PrivilegesServiceImplementation(PrivilegesRepo privilegesRepo, RoleRepo roleRepo) {
        this.privilegesRepo = privilegesRepo;
        this.roleRepo = roleRepo;
    }

    /**
     * Get all the privilege
     *
     * @return List<PrivilegesModel>
     */
    private List<PrivilegesModel> findAll()
    {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return privilegesRepo.findAll();
    }

    /**
     * map privileges with role
     *
     * @param privilegesRequest : PrivilegesRequest
     * @return : ResponseEntity<ResponseMessage>
     */
    @Override
    public ResponseEntity<ResponseMessage> mapPrivileges(PrivilegesRequest privilegesRequest) {
        logger.info(STARTING_METHOD_EXECUTION);
        PrivilegesModel model = new PrivilegesModel();
        ResponseMessage responseMessage =new ResponseMessage();
        String role = privilegesRequest.getRoleName();
        if(roleRepo.findByRole(role)==null)
            throw new ResourceNotFoundException(ROLE_NOT_FOUND);

            model.setRoleId(roleRepo.getRoleIdByRole(role));
            for(PrivilegeSet privilegeSet: privilegesRequest.getPrivileges()){
                switch (privilegeSet.getName()){
                    case EMPLOYEE_MANAGEMENT_PRIVILEGE:
                        model.setEmployeeManagement(privilegeSet.getValue());
                        logger.debug("EmployeeManagement privilege");
                        break;
                    case TEAM_MANAGEMENT_PRIVILEGE:
                        model.setTeamManagement(privilegeSet.getValue());
                        logger.debug("TeamManagement privilege");
                        break;
                    case SURVEY_MODULE_PRIVILEGE:
                        model.setSurveyModule(privilegeSet.getValue());
                        logger.debug("SurveyModule privilege");
                        break;
                    case TEMPLATE_MODULE_PRIVILEGE:
                        model.setTemplateModule(privilegeSet.getValue());
                        logger.debug("TemplateModule privilege");
                        break;
                    case EDIT_ROLE_PRIVILEGE:
                        model.setEditRole(privilegeSet.getValue());
                        logger.debug("EditRole privilege");
                        break;
                    case TAKE_SURVEY_PRIVILEGE:
                        model.setTakeSurvey(privilegeSet.getValue());
                        logger.debug("TakeSurvey privilege");
                        break;
                    case VIEW_TEAM:
                        model.setViewTeam(privilegeSet.getValue());
                        logger.debug("ViewTeam privilege");
                        break;
                    case TEMPLATE_REPORT:
                        model.setTemplateReport(privilegeSet.getValue());
                        logger.debug("TemplateReport privilege");
                        break;
                    default:
                }
            }
            privilegesRepo.save(model);
            logger.debug("Privileges save : {}",model);
            responseMessage.setMessage(PRIVILEGES_ADDED);
            responseMessage.setStatusCode(HttpStatus.OK.value());
             logger.info(EXITING_METHOD_EXECUTION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * @param value : boolean
     * @return PrivilegeSet
     */
    private PrivilegeSet setSurveyModulePrivilege(Boolean value){
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return new PrivilegeSet(SURVEY_MODULE_PRIVILEGE,value, SURVEY_MODULE_TEXT);
    }
    /**
     * @param value : boolean
     * @return PrivilegeSet
     */
    private PrivilegeSet setTemplateModulePrivilege(Boolean value){
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return new PrivilegeSet(TEMPLATE_MODULE_PRIVILEGE, value, TEMPLATE_MODULE_TEXT);
    }
    /**
     * @param value : boolean
     * @return PrivilegeSet
     */
    private PrivilegeSet setTeamManagementPrivilege(Boolean value) {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return new PrivilegeSet(TEAM_MANAGEMENT_PRIVILEGE,value,TEAM_MANAGEMENT_TEXT);
    }
    /**
     * @param value : boolean
     * @return PrivilegeSet
     */
    private PrivilegeSet setEmployeeManagementPrivilege(Boolean value){
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return new PrivilegeSet(EMPLOYEE_MANAGEMENT_PRIVILEGE,value,EMPLOYEE_MANAGEMENT_TEXT);
    }
    /**
     * @param value : boolean
     * @return PrivilegeSet
     */
    private PrivilegeSet setTakeSurveyPrivilege(Boolean value){
        return new PrivilegeSet(TAKE_SURVEY_PRIVILEGE,value,TAKE_SURVEY_TEXT);
    }
    /**
     * @param value : boolean
     * @return PrivilegeSet
     */
    private PrivilegeSet setEditRolePrivilege(Boolean value){
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return new PrivilegeSet(EDIT_ROLE_PRIVILEGE,value,EDIT_ROLE_TEXT);
    }
    /**
     * @param value : boolean
     * @return PrivilegeSet
     */
    private PrivilegeSet setViewTeamPrivilege(Boolean value){
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return new PrivilegeSet(VIEW_TEAM,value,VIEW_TEAM_TEXT);
    }
    /**
     * @param value : boolean
     * @return PrivilegeSet
     */
    private PrivilegeSet setTemplateReportPrivilege(Boolean value){
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return new PrivilegeSet(TEMPLATE_REPORT,value,TEMPLATE_REPORT_TEXT);
    }

    /**
     * All the roles with their respective privileges
     *
     * @return List<PrivilegesRequest>
     */
    @Override
    public List<PrivilegesRequest> showAllPrivileges() {
        logger.info(STARTING_METHOD_EXECUTION);
        List<PrivilegesRequest> allPrivileges=new ArrayList<>();
        List<PrivilegesModel> model=findAll();
        for (PrivilegesModel privilegesModel:model)
        {
            allPrivileges.add(setPrivilegeResponse(privilegesModel));
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return allPrivileges;
    }

    /**
     * Privileges of a particular role
     *
     * @param roleName : name of role
     * @return : PrivilegesRequest
     */
    @Override
    public PrivilegesRequest showPrivileges(String roleName)
    {   logger.info(STARTING_METHOD_EXECUTION);
       RoleModel roleModel= roleRepo.findByRole(roleName);
        if( roleModel!= null) {
            int roleId = roleModel.getRoleId();
            PrivilegesModel privilegesModel = privilegesRepo.findByRoleId(roleId);
            logger.info(EXITING_METHOD_EXECUTION);
            return setPrivilegeResponse(privilegesModel);
        }
        logger.debug("Role {} not found",roleName);
        logger.info(EXITING_METHOD_EXECUTION);
       throw new ResourceNotFoundException(ROLE_NOT_FOUND);
    }

    /**
     * setting PrivilegeSet from PrivilegesModel
     *
     *  @param privilegesModel :PrivilegesModel
     * @return : PrivilegesRequest
     *
     */
    private @NotNull PrivilegesRequest setPrivilegeResponse(@NotNull PrivilegesModel privilegesModel) {
        logger.info(STARTING_METHOD_EXECUTION);
        PrivilegesRequest privilegesRequest =new PrivilegesRequest();
        List<PrivilegeSet> privileges=new ArrayList<>();
        privileges.add(setSurveyModulePrivilege(privilegesModel.isSurveyModule()));
        privileges.add(setTemplateModulePrivilege(privilegesModel.isTemplateModule()));
        privileges.add(setTeamManagementPrivilege(privilegesModel.isTeamManagement()));
        privileges.add(setEmployeeManagementPrivilege(privilegesModel.isEmployeeManagement()));
        privileges.add(setTakeSurveyPrivilege(privilegesModel.isTakeSurvey()));
        privileges.add(setEditRolePrivilege(privilegesModel.isEditRole()));
        privileges.add(setViewTeamPrivilege(privilegesModel.isViewTeam()));
        privileges.add(setTemplateReportPrivilege(privilegesModel.isTemplateReport()));
        privilegesRequest.setRoleName(roleRepo.getRoleByRoleId(privilegesModel.getRoleId()));
        privilegesRequest.setPrivileges(privileges);
        logger.info(EXITING_METHOD_EXECUTION);
        return privilegesRequest;
    }

    /**
     *
     * @param privilegesModel :PrivilegesModel
     * @return List<String>
     */
    @Override
    public List<String> getPrivileges(PrivilegesModel privilegesModel)
    {
        List<String> privileges = new ArrayList<>();
        if(privilegesModel.isEditRole())
            privileges.addAll(Constants.PrivilegesConstant.getEditRole());
        if(privilegesModel.isEmployeeManagement())
            privileges.addAll(Constants.PrivilegesConstant.getEmployeeManagement());
        if(privilegesModel.isSurveyModule())
            privileges.addAll(Constants.PrivilegesConstant.getSurveyModule());
        if(privilegesModel.isTakeSurvey())
            privileges.add(TAKE_SURVEY);
        if(privilegesModel.isTeamManagement()) {
            privileges.addAll(Constants.PrivilegesConstant.getTeamManagement());
            privileges.add(COMBINED_TEAM);
        }
        if(privilegesModel.isViewTeam()) {
            privileges.add(VIEW_TEAM);
            privileges.add(COMBINED_TEAM);
        }
        if(privilegesModel.isTemplateModule())
            privileges.addAll(Constants.PrivilegesConstant.getTemplateModule());
        if(privilegesModel.isTemplateReport())
            privileges.add(TEMPLATE_REPORT);
        return privileges;
    }


}
