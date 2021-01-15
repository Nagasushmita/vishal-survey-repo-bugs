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
 * This model contains data about the template, it includes template id,template name,template description,
 * creator user id, is archived flag
 */
@Entity
@Table(name = "template")
@Setter
@Getter
@NoArgsConstructor
public class TemplateModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "template_id")
    @ApiModelProperty(notes = "Auto generated template id",example = "76")
    private Integer templateId;

    @Column(name = "template_name",length = 50)
    @NotNull(message = TEMPLATE_NAME_NULL)
    @NotEmpty(message = TEMPLATE_NAME_EMPTY)
    @ApiModelProperty(notes = "Template name",example = "Food Feedback Survey")
    private String templateName;

    @Column(name = "template_description",length = 200)
    @NotNull(message = TEMPLATE_DESC_NULL)
    @NotEmpty(message = TEMPLATE_DESC_EMPTY)
    @ApiModelProperty(notes = "Template description",example = "Food Feedback Survey description")
    private String templateDesc;

    @Column(name="created_by")
    @NotNull(message = USER_ID_NULL)
    @ApiModelProperty(notes = "Id of user who created the template",example = "235")
    private int creatorUserId;

    @Column(name="creation_date")
    @ApiModelProperty(notes = "Template creation date",example = "1603865351")
    private long creationDate;

    @Column(name = "is_archived")
    @ApiModelProperty(notes = "Template archived or not flag",example = "true|false")
    private boolean isArchived;

    @Column(name="updated_on")
    @ApiModelProperty(notes = "Template updation date",example = "1603865351")
    private long updatedOn;

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", referencedColumnName = "template_id")
    private final List<TemplateQuestionModel> templateQuestionModel=new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", referencedColumnName = "template_id")
     private final List<SurveyModel> surveyModels=new ArrayList<>();
}