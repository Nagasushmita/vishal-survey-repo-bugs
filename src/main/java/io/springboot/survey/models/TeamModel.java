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
 * This model contains data about the team, it includes team id,team name,user id,manager id,project name,
 * status
 */
@Entity
@Table(name = "team")
@Getter
@Setter
@NoArgsConstructor
public class TeamModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "team_id")
    @ApiModelProperty(notes = "Auto generated team id",example = "1")
    private int teamId;

    @Column(name = "team_name",length = 50)
    @NotNull(message = TEAM_NAME_NULL)
    @NotEmpty(message = TEAM_NAME_EMPTY)
    @ApiModelProperty(notes = "Team name",example = "Amber")
    private String teamName;

    @Column(name = "user_id")
    @NotNull(message = USER_ID_NULL)
    @ApiModelProperty(notes = "Id of user who have created the team",example = "789")
    private int userId;

    @Column(name="manager_id")
    @NotNull(message = MANAGER_ID_NULL)
    @ApiModelProperty(notes = "Id of user who manages the team",example = "789")
    private int managerId;

    @Column(name = "project_name",length = 50)
    @NotNull(message = PROJECT_NAME_NULL)
    @NotEmpty(message =PROJECT_NAME_EMPTY)
    @ApiModelProperty(notes = "Project name",example = "Uber")
    private String projectName;

    @Column(name = "status",length = 15)
    @NotNull(message = STATUS_NULL)
    @NotEmpty(message =STATUS_EMPTY)
    @ApiModelProperty(notes = "Status",example = "Inactive")
    private String status;
    
    @Column(name = "created_on")
    @ApiModelProperty(notes = "date of creation",example = "1603865351")
    private long createdOn;

    @Column(name = "updated_on")
    @ApiModelProperty(notes = "date of updation",example = "1603865351")
    private long updatedOn;

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id", referencedColumnName = "team_id")
    private final List<TeamMemberModel> teamMemberModels = new ArrayList<>();

}