package io.springboot.survey.models;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static io.springboot.survey.utils.Constants.ModelConstraintMessage.*;

/**
 * This model contains the response given by an user for a io.springboot.survey it includes response id, question id,
 * answer id, text answer, file id(if any file is uploaded)
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "response")
public class ResponseModel implements Serializable {

    @Id
    @Column(name ="partial_key")
    @GeneratedValue(strategy = GenerationType.TABLE)
    @ApiModelProperty(notes = "Auto generated id",example = "1")
    private  int parKey;

    @Column(name = "response_id")
    @NotNull(message = RESPONSE_ID_CANNOT_BE_NULL)
    @ApiModelProperty(notes = "response id ",example = "123")
    private Integer responseId;

    @Column(name = "question_id")
    @NotNull(message = QUESTION_ID_NULL)
    @ApiModelProperty(notes = "question id",example = "81")
    private int quesId;

    @Column(name = "answer_id")
    @NotNull(message = ANSWER_ID_NULL)
    @ApiModelProperty(notes = "answer id",example = "1256")
    private Integer answerId;

    @Column(name = "text_answer")
    @NotNull(message = ANSWER_TEXT_NULL)
    @NotEmpty(message = ANSWER_TEXT_EMPTY)
    @ApiModelProperty(notes = "answer text",example = "Good")
    private String textAnswer;

    @Column(name="file_id")
    @ApiModelProperty(notes = "File id",example = "998")
    private String fileId;

}