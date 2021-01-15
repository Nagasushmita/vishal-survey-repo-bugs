package io.springboot.survey.models;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static io.springboot.survey.utils.Constants.ModelConstraintMessage.TEAM_ID_NULL;
import static io.springboot.survey.utils.Constants.ModelConstraintMessage.USER_ID_NULL;

/**
 * This model contains information about the members of the team, it includes team id,team member id,user id
 */
@Embeddable
@Entity
@Table(name = "team_member")
@Getter
@Setter
@NoArgsConstructor
public class TeamMemberModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "team_member_id")
    @ApiModelProperty(notes = "Auto generated id",example = "1")
    private int id;

    @Column(name = "team_id")
    @NotNull(message = TEAM_ID_NULL)
    @ApiModelProperty(notes = "team id",example = "251")
    private int teamId;

    @Column(name = "user_id")
    @NotNull(message = USER_ID_NULL)
    @ApiModelProperty(notes = "User id",example = "1345")
    private int userId;
}