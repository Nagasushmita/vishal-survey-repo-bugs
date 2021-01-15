package io.springboot.survey.models;


import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static io.springboot.survey.utils.Constants.ModelConstraintMessage.*;

/**
 * This model contains data about the response given by the user it includes response id,io.springboot.survey id,user id,
 * response date,team id
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "survey_response")
public class SurveyResponseModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "response_id")
    @ApiModelProperty(notes = "Auto generated io.springboot.survey id",example = "1")
    private int responseId;

    @Column(name = "survey_id")
    @NotNull(message = SURVEY_ID_NULL)
    @ApiModelProperty(notes = "Survey id",example = "134")
    private int surveyId;

    @Column(name = "user_id")
    @NotNull(message = USER_ID_NULL)
    @ApiModelProperty(notes = "Id of user who has given the response",example = "4134")
    private int userId;

    @Column(name = "response_date")
    @ApiModelProperty(notes = "Date on which the response was given",example = "1603865351")
    private long responseDate;

    @Column(name="team_id")
    @NotNull(message = TEAM_ID_NULL)
    @ApiModelProperty(notes = "Team id",example = "16")
    private Integer teamId;

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "response_id", referencedColumnName = "response_id")
    private final List<ResponseModel> responseModel =new ArrayList<>();
}
