package io.springboot.survey.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static io.springboot.survey.utils.Constants.ModelConstraintMessage.*;

/**
 * This model contains answer of questions of the templates, it includes answer id,answer text,question id
 */
@Entity
@Table(name = "template_answer")
@Getter
@Setter
public class TemplateAnswerModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "answer_id")
    @ApiModelProperty(notes = "Auto generated answer id",example = "1")
    private int ansId;

    @Column(name = "question_id")
    @NotNull(message = QUESTION_ID_NULL)
    @ApiModelProperty(notes = "Question id",example = "14")
    private int quesId;

    @Column(name = "answer_text")
    @NotNull(message = ANSWER_TEXT_NULL)
    @NotEmpty(message = ANSWER_TEXT_EMPTY)
    @ApiModelProperty(notes = "Answer text",example = "Extremely skilled")
    private String ansText;
}