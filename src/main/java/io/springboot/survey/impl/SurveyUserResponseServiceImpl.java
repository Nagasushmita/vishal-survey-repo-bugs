package io.springboot.survey.impl;

import io.springboot.survey.models.UploadFileModel;
import io.springboot.survey.repository.*;
import io.springboot.survey.response.QuestionResponse;
import io.springboot.survey.service.SurveyUserResponseService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.List;

import static io.springboot.survey.utils.Constants.CommonConstant.*;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;

@Component
public class SurveyUserResponseServiceImpl implements SurveyUserResponseService {

    private final QuestionRepo questionRepo;
    private final AnswerRepo answerRepo;
    private final QuestionTypeRepo quesTypeRepo;
    private final ResponseRepo responseRepo;
    private final UploadFileRepo uploadFileRepo;
    private final UserRepo userRepo;
    private static final Logger logger= LoggerFactory.getLogger(SurveyUserResponseServiceImpl.class.getSimpleName());

    public SurveyUserResponseServiceImpl(UserRepo userRepo, QuestionRepo questionRepo, AnswerRepo answerRepo, QuestionTypeRepo quesTypeRepo, ResponseRepo responseRepo, UploadFileRepo uploadFileRepo) {
        this.userRepo =userRepo;
        this.questionRepo = questionRepo;
        this.answerRepo = answerRepo;
        this.quesTypeRepo = quesTypeRepo;
        this.responseRepo = responseRepo;
        this.uploadFileRepo = uploadFileRepo;
    }


    /**
     * Return user response
     *
     * @param uniqueQuestion : list of question List<Integer>.
     * @param surveyId : surveyId.
     * @param responseId : responseId.
     * @return List<QuestionResponse>
     */
    @Override
    public List<QuestionResponse> getUserResponse(@NotNull List<Integer> uniqueQuestion, int surveyId,
                                                  int responseId) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<QuestionResponse> response = new ArrayList<>();
        for (Integer question : uniqueQuestion) {
            Tuple object  = questionRepo.findByQuesId(question);
            String quesType = quesTypeRepo.getQuestionNameByTypeId((Integer) object.get(0));
            QuestionResponse questionResponse = new QuestionResponse();
            Object[] survey= userRepo.getSurveyData(surveyId);
            questionResponse.setSurveyName((String) survey[2]);
            questionResponse.setSurveyDescription((String) survey[3]);
            questionResponse.setQuestion((String) object.get(1));
            questionResponse.setQuesType(quesType);
            switch (quesType) {
                case QUES_TYPE_RADIO:
                case QUES_TYPE_CHECKBOX:
                case QUES_TYPE_RATING:
                    List<String> answers = answerRepo.getAnswersByResponseModel(question,responseId);
                    if (answers.isEmpty()) {
                        answers.add(null);
                    }
                    questionResponse.setAnswers(answers);
                    logger.debug("Question Type Radio|Checkbox|Rating :: {}",quesType);
                    break;
                case QUES_TYPE_TEXT:
                    logger.debug("Question Type text :: {}",quesType);
                    questionResponse.setAnswerText( responseRepo.getTextAnswerByResponseIdAndQuesId(responseId, question));
                    break;
                case QUES_TYPE_FILE:
                    logger.debug("Question Type file :: {}",quesType);
                    String fileId= responseRepo.getFileByResponseIdAndQuesId(responseId, question);
                    if (!StringUtils.isEmpty(fileId)) {
                        UploadFileModel model = uploadFileRepo.findByFileId(fileId);
                        questionResponse.setFileData(model.getFileData());
                        questionResponse.setFileType(model.getFileType());
                        questionResponse.setFileName(model.getFileName());
                    }
                    break;
                default:
            }
            response.add(questionResponse);
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return response;
    }


}
