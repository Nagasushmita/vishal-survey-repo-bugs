package io.springboot.survey.impl;

import io.springboot.survey.exception.APIException;
import io.springboot.survey.models.AnswerModel;
import io.springboot.survey.models.QuestionModel;
import io.springboot.survey.models.SurveyModel;
import io.springboot.survey.repository.*;
import io.springboot.survey.request.CreateSurveyRequest;
import io.springboot.survey.request.DeleteArchiveRequest;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.response.SurveyData;
import io.springboot.survey.service.SurveyCrudService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.springboot.survey.utils.Constants.ApiResponseConstant.INTERNAL_SERVER_ERROR;
import static io.springboot.survey.utils.Constants.CommonConstant.*;
import static io.springboot.survey.utils.Constants.ErrorMessageConstant.*;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;
import static java.lang.String.valueOf;

@Component
public class SurveyCrudServiceImpl implements SurveyCrudService {

    final SurveyRepo surveyRepo;
    final UserRepo userRepo;
    final QuestionTypeRepo questionTypeRepo;
    final QuestionRepo questionRepo;
    final SurveyStatusRepo surveyStatusRepo;
    final AnswerRepo answerRepo;

    private static final Logger logger= LoggerFactory.getLogger(SurveyCrudServiceImpl.class.getSimpleName());

    public SurveyCrudServiceImpl(SurveyRepo surveyRepo, UserRepo userRepo, QuestionTypeRepo questionTypeRepo, QuestionRepo questionRepo, SurveyStatusRepo surveyStatusRepo, AnswerRepo answerRepo) {
        this.surveyRepo = surveyRepo;
        this.userRepo = userRepo;
        this.questionTypeRepo = questionTypeRepo;
        this.questionRepo = questionRepo;
        this.surveyStatusRepo = surveyStatusRepo;
        this.answerRepo = answerRepo;
    }


    /**
     * Create new io.springboot.survey
     *
     * @param createSurveyRequest : CreateSurveyRequest
     * @return ResponseEntity<ResponseMessage>
     */
    @Override
    public ResponseEntity<ResponseMessage> createSurvey(CreateSurveyRequest createSurveyRequest) {
        logger.info(STARTING_METHOD_EXECUTION);
        try {
            ResponseMessage responseMessage = new ResponseMessage();
            if (userRepo.getSurveyDataByNameAndId(createSurveyRequest.getSurveyName(),
                    createSurveyRequest.getCreatorEmail(),false) == null) {
                SurveyModel ob = new SurveyModel();
                ob.setSurveyName(createSurveyRequest.getSurveyName());
                ob.setSurveyDesc(createSurveyRequest.getSurveyDesc());
                ob.setCreationDate(System.currentTimeMillis());
                ob.setLink(createSurveyRequest.getLink());
                ob.setExpirationDate(0);
                ob.setArchived(false);
                if (createSurveyRequest.getTemplateName() == null) {
                    ob.setTemplateId(null);
                } else {
                    ob.setTemplateId(userRepo.getTemplateIdByName(createSurveyRequest.getTemplateName()));
                }
                ob.setCreatorUserId(userRepo.getUserIdByUserEmail(createSurveyRequest.getCreatorEmail()));
                int surId =  surveyRepo.save(ob).getSurveyId();
                List<SurveyData> dataList = createSurveyRequest.getSurveyDataList();
                logger.debug("New io.springboot.survey created : {}",ob);
                createSurveyHelperFunction(dataList, surId);
            } else {
                responseMessage.setMessage(SAME_SURVEY);
                logger.debug("Survey of same name {} already exist",createSurveyRequest.getSurveyName());
                responseMessage.setStatusCode(HttpStatus.CONFLICT.value());
                return new ResponseEntity<>(responseMessage, HttpStatus.CONFLICT);
            }
            responseMessage.setMessage(SUCCESS);
            responseMessage.setStatusCode(HttpStatus.CREATED.value());
            logger.info(EXITING_METHOD_EXECUTION);
            return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
        }
        catch (Exception ex)
        {
            logger.error("Exception occurred while creating io.springboot.survey :: ",ex);
            throw new APIException(INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * Create Survey Helper Function
     * Used to store question in the question model based on their question type and
     * their answers in the answer table.
     *
     * @param dataList :  List<SurveyData>
     * @param surId : io.springboot.survey Id
     */
    private void createSurveyHelperFunction(@NotNull List<SurveyData> dataList, int surId)
    {
        logger.info(STARTING_METHOD_EXECUTION);
        for (SurveyData surveyData : dataList) {
            QuestionModel questionModel = new QuestionModel();
            questionModel.setQuesText(surveyData.getQuestion());
            questionModel.setQuesTypeId(questionTypeRepo.getIdByQuestionName(surveyData.getQuesType()));
            questionModel.setSurveyId(surId);
            questionModel.setMandatory(surveyData.getMandatory());
            int quesId = questionRepo.save(questionModel).getQuesId();
            logger.debug("Question added : {}",questionModel);
            switch (surveyData.getQuesType()) {
                case QUES_TYPE_RATING:
                    for (int i = 0; i < surveyData.getNumberOfOptions(); i++) {
                        AnswerModel answerModel = new AnswerModel();
                        answerModel.setQuesId(quesId);
                        answerModel.setAnsText(valueOf(i + 1));
                        answerRepo.save(answerModel);
                        logger.debug("Answer of rating questionType added : {}",answerModel);
                    }
                    break;
                case QUES_TYPE_RADIO:
                case QUES_TYPE_CHECKBOX:
                    List<String> answers = surveyData.getAnswers();
                    for (String ans : answers) {
                        AnswerModel model = new AnswerModel();
                        model.setQuesId(quesId);
                        model.setAnsText(ans);
                        answerRepo.save(model);
                        logger.debug("Answer of radio|checkbox questionType added : {}",model);
                    }
                    break;
                default:
            }
        }
        logger.info(EXITING_METHOD_EXECUTION);
    }

    /**
     * Archive a io.springboot.survey on the basis of surveyName,CreatorUserId and
     * creationDate
     *
     * @param archiveResponse :DeleteArchiveRequest.
     * @return :  ResponseEntity<ResponseMessage>
     */
    @Override
    public ResponseEntity<ResponseMessage> archiveSurvey(DeleteArchiveRequest archiveResponse) {
        logger.info(STARTING_METHOD_EXECUTION);
        SurveyModel surveyModel = surveyRepo.findBySurveyNameAndCreatorUserIdAndCreationDateAndArchivedFalse
                    (archiveResponse.getSurveyName(), userRepo.getUserIdByUserEmail(archiveResponse.getCreatorEmail()),
                            archiveResponse.getCreationDate());
            surveyModel.setArchived(true);
            surveyModel.setUpdatedDate(System.currentTimeMillis());
            surveyModel.setTemplateId(null);
            surveyRepo.save(surveyModel);
            List<Integer> pendingUser = surveyStatusRepo.getParKeyByIdAndTaken(surveyModel.getSurveyId(), false);
            for (Integer user : pendingUser) {
                surveyStatusRepo.delete(surveyStatusRepo.findByParKey(user));
            }
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(ARCHIVED);
            responseMessage.setStatusCode(HttpStatus.OK.value());
            logger.info(EXITING_METHOD_EXECUTION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }


    /**
     *  Unarchive a io.springboot.survey on the basis of surveyName,CreatorUserId and creationDate
     *
     * @param archiveResponse : DeleteArchiveRequest.
     * @return : ResponseEntity<ResponseMessage>
     */
    @Override
    public ResponseEntity<ResponseMessage> unarchiveSurvey(DeleteArchiveRequest archiveResponse) {
        logger.info(STARTING_METHOD_EXECUTION);
        SurveyModel surveyModel = surveyRepo.findBySurveyNameAndCreatorUserIdAndCreationDateAndArchivedTrue
                    (archiveResponse.getSurveyName(), userRepo.getUserIdByUserEmail(archiveResponse.getCreatorEmail())
                            , archiveResponse.getCreationDate());
            surveyModel.setArchived(false);
            if (userRepo.getTemplateIdByName(archiveResponse.getSurveyName())!= null)
                surveyModel.setTemplateId(userRepo.getTemplateIdByName(archiveResponse.getSurveyName()));
            else
                surveyModel.setTemplateId(null);
            surveyModel.setUpdatedDate(System.currentTimeMillis());
            surveyRepo.save(surveyModel);
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(UNARCHIVED);
            responseMessage.setStatusCode(HttpStatus.OK.value());
            logger.info(EXITING_METHOD_EXECUTION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }

    /**
     *Delete a io.springboot.survey on the basis of surveyName,CreatorUserId and creationDate
     *
     * @param deleteResponse : DeleteArchiveRequest.
     * @return : ResponseEntity<Void> -- 204 No Content.
     */
    @Override
    public ResponseEntity<Void> deleteSurvey(DeleteArchiveRequest deleteResponse) {
        logger.info(STARTING_METHOD_EXECUTION);
        try {
            SurveyModel surveyModel = surveyRepo.findBySurveyNameAndCreatorUserIdAndCreationDateAndArchivedTrue(deleteResponse.getSurveyName(),
                    userRepo.getUserIdByUserEmail(deleteResponse.getCreatorEmail()),deleteResponse.getCreationDate());
            if (surveyModel != null) {
                surveyRepo.delete(surveyModel);
            }
            else {
                surveyRepo.deleteSurveyModelBySurveyNameAndCreatorUserIdAndCreationDate(deleteResponse.getSurveyName(),
                        userRepo.getUserIdByUserEmail(deleteResponse.getCreatorEmail()), deleteResponse.getCreationDate());
            }
            logger.info(EXITING_METHOD_EXECUTION);
            return ResponseEntity.noContent().build();
        }
        catch (Exception ex)
        {
            logger.error("Error occurred while deleting io.springboot.survey :: ",ex);
            throw new APIException(INTERNAL_SERVER_ERROR);
        }
    }





}
