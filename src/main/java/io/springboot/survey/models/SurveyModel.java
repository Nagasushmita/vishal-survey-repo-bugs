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
 * This model contains data about the io.springboot.survey, it includes io.springboot.survey id, io.springboot.survey name,io.springboot.survey descripton,creation date
 * expiration date,created by ,template id,archived flag,link
 */
@Entity
@Table(name = "survey")
@Getter
@Setter
@NoArgsConstructor
public class SurveyModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "survey_id")
    @ApiModelProperty(notes = "Auto generated io.springboot.survey id",example = "1")
    private int surveyId;

    @Column(name = "survey_name",length = 50)
    @NotNull(message = SURVEY_NAME_NULL)
    @NotEmpty(message = SURVEY_NAME_EMPTY)
    @ApiModelProperty(notes = "Survey name",example = "Employee satisfaction io.springboot.survey")
    private String surveyName;

    @Column(name = "survey_description",length = 200)
    @NotNull(message = SURVEY_DESC_NULL)
    @NotEmpty(message = SURVEY_DESC_EMPTY)
    @ApiModelProperty(notes = "Survey description",example = "This io.springboot.survey provides management with a direction and know-how of how satisfied are its employees.")
    private String surveyDesc;

    @Column(name = "creation_date")
    @ApiModelProperty(notes = "io.springboot.survey creation date",example = "1603865351")
    private long creationDate;

    @Column(name = "expiration_date")
    @ApiModelProperty(notes = "io.springboot.survey expiration date",example = "1603865351")
    private long expirationDate;

    @Column(name = "created_by")
    @NotNull(message = USER_ID_NULL)
    @ApiModelProperty(notes = "Id of user who have created the io.springboot.survey",example = "134")
    private int creatorUserId;

    @Column(name = "template_id")
    @NotNull(message = TEMPLATE_ID_NULL)
    @ApiModelProperty(notes = "Id of template which is used to create io.springboot.survey",example = "767")
    private Integer templateId;

    @Column(name = "archived")
    @ApiModelProperty(notes = "Survey archived flag",example = "true|false")
    private boolean archived;

    @Column(name = "link")
    @NotNull(message = LINK_NULL)
    @NotEmpty(message = LINK_EMPTY)
    @ApiModelProperty(notes = "Survey link",example = "http://localhost:3000/take/SW50ZXJuIEVuZ2FnZW1lbnQgU3VydmV5L3NhbXRhLmJhbnNhbEBuaW5lbGVhcHMuY29t/Alpha")
    private String link;

    @Column(name = "updated_date")
    @ApiModelProperty(notes = "io.springboot.survey updated date",example = "1603865351")
    private long updatedDate;

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "survey_id", referencedColumnName = "survey_id")
    private List<QuestionModel> questionModel=new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "survey_id", referencedColumnName = "survey_id")
    private List<SurveyResponseModel> surveyResponseModel=new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "survey_id", referencedColumnName = "survey_id")
    private final List<SurveyStatusModel> surveyStatusModel=new ArrayList<>();

}