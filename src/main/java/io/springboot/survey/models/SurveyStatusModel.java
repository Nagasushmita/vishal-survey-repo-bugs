package io.springboot.survey.models;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static io.springboot.survey.utils.Constants.ModelConstraintMessage.*;

/**
 * This model contains status of the response given by an user, it includes user id,io.springboot.survey id,is taken flag,
 * team id
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "survey_status")
public class SurveyStatusModel implements Serializable {

    @Id
    @Column(name ="partial_key")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "Auto generated id",example = "1")
    private int parKey;

    @Column(name = "user_id")
    @NotNull(message = USER_ID_NULL)
    @ApiModelProperty(notes = "User id",example = "1")
    private int userId;

    @Column(name = "survey_id")
    @NotNull(message = SURVEY_ID_NULL)
    @ApiModelProperty(notes = "io.springboot.survey id ",example = "1467")
    private int surveyId;

    @Column(name = "is_taken")
    @ApiModelProperty(notes = "Survey is taken or not flag",example = "true|false")
    private Boolean taken;

    @Column(name = "assigned_by")
    @NotNull(message = USER_ID_NULL)
    @ApiModelProperty(notes = "Id of the user who have assigned the io.springboot.survey",example = "1")
    private int assignedBy;

    @Column(name = "team_id")
    @NotNull(message = TEAM_ID_NULL)
    @ApiModelProperty(notes = "team id",example = "1")
    private Integer teamId;

}