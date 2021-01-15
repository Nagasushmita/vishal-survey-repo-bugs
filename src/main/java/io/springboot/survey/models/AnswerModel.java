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
 * This models contains answer id and answer text of different questions in a io.springboot.survey.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "answer")
public class AnswerModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "answer_id")
    @ApiModelProperty(notes = "Auto generated answer id",example = "1")
    private Integer ansId;

    @Column(name = "question_id")
    @NotNull(message = QUESTION_ID_NULL)
    @ApiModelProperty(notes = "question id ",example = "2346")
    private int quesId;

    @Column(name = "answer_text",length = 500)
    @NotNull(message = ANSWER_TEXT_NULL)
    @NotEmpty(message = ANSWER_TEXT_EMPTY)
    @ApiModelProperty(notes = "answer text",example = "Extremely satisfied")
    private String ansText;

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "answer_id", referencedColumnName = "answer_id")
    private final List<ResponseModel> responseModel =new ArrayList<>();
}
