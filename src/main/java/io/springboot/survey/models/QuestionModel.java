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
 * This model contains question that is used in io.springboot.survey, it includes question id,io.springboot.survey id,question type id,
 * question text and mandatory flag.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "question")
public class QuestionModel implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "question_id")
    @ApiModelProperty(notes = "Auto generated id",example = "1")
    private int quesId;

    @Column(name = "survey_id")
    @NotNull(message = SURVEY_ID_NULL)
    @ApiModelProperty(notes = "Id of the io.springboot.survey",example = "890")
    private int surveyId;

    @Column(name = "question_type_id")
    @NotNull(message = QUESTION_TYPE_ID_NULL)
    @ApiModelProperty(notes = "Id of question type",example = "1|2|3")
    private int quesTypeId;

    @Column(name = "question_text")
    @NotNull(message = QUESTION_TEXT_NULL)
    @NotEmpty(message = QUESTION_TEXT_EMPTY)
    @ApiModelProperty(notes = "question text",example = "Rate the overall event?")
    private String quesText;

    @Column(name = "mandatory")
    @ApiModelProperty(notes = "mandatory flag",example = "false|true")
    private boolean  mandatory;


    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id", referencedColumnName = "question_id")
    private final List<AnswerModel> answerModel=new ArrayList<>();





}