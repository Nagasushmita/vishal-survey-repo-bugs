package io.springboot.survey.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static io.springboot.survey.utils.Constants.ModelConstraintMessage.*;

/**
 * This model contains question of the template, it includes question id,question type id,template id,
 * question text,mandatory flag
 */
@Entity
@Table(name = "template_question")
@Getter
@Setter
public class TemplateQuestionModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "question_id")
    @ApiModelProperty(notes = "Auto generated question id",example = "76")
    private int quesId;

    @Column(name = "template_id")
    @NotNull(message = TEMPLATE_ID_NULL)
    @ApiModelProperty(notes = "Template id",example = "76")
    private int templateId;

    @Column(name = "question_type_id")
    @ApiModelProperty(notes = "Question type id",example = "7623")
    private int quesTypeId;

    @Column(name = "question_text")
    @NotEmpty(message = QUESTION_TEXT_EMPTY)
    @NotNull(message = QUESTION_TEXT_NULL)
    @ApiModelProperty(notes = "Question text",example = "how was the overall event ?")
    private String quesText;

    @Column(name = "mandatory")
    @ApiModelProperty(notes = "mandatory flag",example = "true|false")
    private Boolean mandatory;

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id", referencedColumnName = "question_id")
    private List<TemplateAnswerModel> answerModel=new ArrayList<>();

}
