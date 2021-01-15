package io.springboot.survey.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static io.springboot.survey.utils.Constants.ModelConstraintMessage.*;

/**
 *This model contains data about the user,it includes user id,role id,user name,user email,employee id,
 * active flag,gender,designation
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_details")
public class UserModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    @ApiModelProperty(notes = "Auto generated user id",example = "12")
    private int userId;

    @Column(name = "role_id")
    @NotNull(message = ROLE_ID_NULL)
    @ApiModelProperty(notes = "Role id",example = "2")
    private int roleId;

    @Column(name = "user_name",length = 50)
    @NotNull(message = USER_NAME_NULL)
    @NotEmpty(message = USER_NAME_EMPTY)
    @ApiModelProperty(notes = "User name",example = "john smith")
    private String userName;

    @Column(name = "user_email",length = 50)
    @NotNull(message = USER_EMAIL_NULL)
    @NotEmpty(message = USER_EMAIL_EMPTY)
    @ApiModelProperty(notes = "User email",example = "johnsmith@nineleaps.com")
    private String userEmail;

    @Column(name = "employee_id",length = 20)
    @NotNull(message = EMPLOYEE_ID_NULL)
    @NotEmpty(message = EMPLOYEE_ID_EMPTY)
    @ApiModelProperty(notes = "Organisation id",example = "NL-111")
    private String orgId;

    @Column(name = "active")
    @ApiModelProperty(notes = "Employee active|inactive flag",example = "true|false")
    private boolean active;

    @Column(name = "gender",length = 10)
    @NotNull(message = EMPLOYEE_GENDER_NULL)
    @NotEmpty(message = EMPLOYEE_GENDER_EMPTY)
    @ApiModelProperty(notes = "Employee gender",example = "male|female")
    private String gender;

    @Column(name = "designation",length = 20)
    @NotNull(message = EMPLOYEE_DESIGNATION_NULL)
    @NotEmpty(message = EMPLOYEE_DESIGNATION_EMPTY)
    @ApiModelProperty(notes = "Employee designation",example = "SD2|MTS2")
    private String designation;

    @Column(name = "created_on")
    @ApiModelProperty(notes = "Employee creation date",example = "1603865351")
    private long createdOn;

    @Column(name = "updated_on")
    @ApiModelProperty(notes = "Employee updation date",example = "1603865391")
    private long updatedOn;

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "created_by", referencedColumnName = "user_id")
    private List<SurveyModel> surveyModel=new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "created_by", referencedColumnName = "user_id")
    private List<TemplateModel> templateModel=new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private List<SurveyStatusModel> surveyStatusModels=new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private List<TeamMemberModel> teamMemberModels=new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private List<TeamModel> teamModels=new ArrayList<>();




}