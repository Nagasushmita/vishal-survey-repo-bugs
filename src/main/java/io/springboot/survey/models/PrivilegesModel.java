package io.springboot.survey.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


/**
 *This model contains mapping of the privileges with role.
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "privileges")
@ApiModel(description = "The Table Contains The Mapping Of Privileges With Role")
public class PrivilegesModel implements Serializable {

    @Id
    @Column(name = "role_id")
    @ApiModelProperty(notes = "role id",example = "76")
    private int roleId;

    @Column(name = "employee_management")
    @ApiModelProperty(notes = "Employee management privilege flag",example = "true|false")
    private boolean employeeManagement;

    @Column(name = "team_management")
    @ApiModelProperty(notes = "Team management privilege flag",example = "true|false")
    private boolean teamManagement;

    @Column(name = "survey_module")
    @ApiModelProperty(notes = "Survey module privilege flag",example = "true|false")
    private boolean surveyModule;

    @Column(name = "template_module")
    @ApiModelProperty(notes = "Template module privilege flag",example = "true|false")
    private boolean templateModule;

    @Column(name = "take_survey")
    @ApiModelProperty(notes = "Take io.springboot.survey privilege flag",example = "true|false")
    private boolean takeSurvey;

    @Column(name = "edit_role")
    @ApiModelProperty(notes = "Edit role privilege flag",example = "true|false")
    private boolean editRole;

    @Column(name = "view_team")
    @ApiModelProperty(notes = "View team privilege flag",example = "true|false")
    private boolean viewTeam;

    @Column(name = "template_report")
    @ApiModelProperty(notes = "Template report privilege flag",example = "true|false")
    private boolean templateReport;

}