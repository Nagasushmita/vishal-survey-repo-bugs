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

import static io.springboot.survey.utils.Constants.ModelConstraintMessage.QUESTION_TYPE_NAME_EMPTY;
import static io.springboot.survey.utils.Constants.ModelConstraintMessage.QUESTION_TYPE_NAME_NULL;

/**
 * This model contains data about the type of question which can be used while creating io.springboot.survey,it includes
 * question type id,question type name.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "question_type")
public class QuestionTypeModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "Auto generated id",example = "1")
    @Column(name = "question_type_id")
    private int quesTypeId;

    @Column(name = "question_type_name",length = 20)
    @NotNull(message = QUESTION_TYPE_NAME_NULL)
    @NotEmpty(message = QUESTION_TYPE_NAME_EMPTY)
    @ApiModelProperty(notes = "Type of question",example = "radio|checkbox|file")
    private String quesTypeName;

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "question_type_id", referencedColumnName = "question_type_id")
    private final List<QuestionModel> questionModel=new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_type_id", referencedColumnName = "question_type_id")
    private final List<TemplateQuestionModel> templateQuestionModel=new ArrayList<>();

}
