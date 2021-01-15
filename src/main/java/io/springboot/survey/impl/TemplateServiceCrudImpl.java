package io.springboot.survey.impl;

import io.springboot.survey.exception.APIException;
import io.springboot.survey.exception.ForbiddenException;
import io.springboot.survey.models.TemplateAnswerModel;
import io.springboot.survey.models.TemplateModel;
import io.springboot.survey.models.TemplateQuestionModel;
import io.springboot.survey.repository.*;
import io.springboot.survey.request.CreateTemplateRequest;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.response.SurveyData;
import io.springboot.survey.service.TemplateServiceCrud;
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
import static io.springboot.survey.utils.Constants.TemplateModuleConstant.BYTE_ONE;
import static io.springboot.survey.utils.Constants.TemplateModuleConstant.BYTE_ZERO;


@Component
public class TemplateServiceCrudImpl implements TemplateServiceCrud {

    private final TemplateRepo templateRepo;
    private final UserRepo userRepo;
    private final QuestionTypeRepo questionTypeRepo;
    private final TemplateQuestionRepo templateQuestionRepo;
    private final TemplateAnswerRepo answerRepo;

    private static final Logger logger= LoggerFactory.getLogger(TemplateServiceCrudImpl.class.getSimpleName());

    public TemplateServiceCrudImpl(TemplateRepo templateRepo, UserRepo userRepo, QuestionTypeRepo questionTypeRepo, TemplateQuestionRepo templateQuestionRepo, TemplateAnswerRepo answerRepo) {
        this.templateRepo = templateRepo;
        this.userRepo = userRepo;
        this.questionTypeRepo = questionTypeRepo;
        this.templateQuestionRepo = templateQuestionRepo;
        this.answerRepo = answerRepo;
    }


    /**
     * Create Template
     *
     * @param createTemplateRequest : CreateTemplateRequest
     * @return : ResponseEntity<ResponseMessage>
     */
    @Override
    public ResponseEntity<ResponseMessage> createTemplate(CreateTemplateRequest createTemplateRequest) {
        logger.info(STARTING_METHOD_EXECUTION);
        try {
            if (userRepo.getTemplateIdByName(createTemplateRequest.getTemplateName()) == null) {

                TemplateModel ob = new TemplateModel();
                ob.setTemplateName(createTemplateRequest.getTemplateName());
                ob.setTemplateDesc(createTemplateRequest.getTemplateDesc());
                ob.setCreationDate(System.currentTimeMillis());
                ob.setArchived(false);
                ob.setCreatorUserId(userRepo.getUserIdByUserEmail(createTemplateRequest.getEmail()));
                int tempId = templateRepo.save(ob).getTemplateId();
                logger.debug("Template created : {}",ob);
                List<SurveyData> dataList = createTemplateRequest.getSurveyDataList();
                createTemplateHelperFunction(dataList, tempId);
            } else {
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage(TEMPLATE_ALREADY_EXIST);
                responseMessage.setStatusCode(HttpStatus.CONFLICT.value());
                logger.debug("Template {} already exist",createTemplateRequest.getTemplateName());
                logger.info(EXITING_METHOD_EXECUTION);
                return new ResponseEntity<>(responseMessage, HttpStatus.CONFLICT);
            }
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(SUCCESS);
            responseMessage.setStatusCode(HttpStatus.CREATED.value());
            logger.info(EXITING_METHOD_EXECUTION);
            return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
        } catch (Exception ex) {
            logger.error("Error occurred while creating template :: ",ex);
            throw new APIException(INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Create template helper function
     *
     * @param dataList : List<SurveyData>
     * @param tempId : templateId
     */
    private void createTemplateHelperFunction(@NotNull List<SurveyData> dataList, int tempId) {
        logger.info(STARTING_METHOD_EXECUTION);
        for (SurveyData surveyData : dataList) {
            TemplateQuestionModel templateQuestionModel = new TemplateQuestionModel();
            templateQuestionModel.setQuesText(surveyData.getQuestion());
            templateQuestionModel.setQuesTypeId(questionTypeRepo.getIdByQuestionName(surveyData.getQuesType()));
            templateQuestionModel.setTemplateId(tempId);
            templateQuestionModel.setMandatory(surveyData.getMandatory());
            int quesId = templateQuestionRepo.save(templateQuestionModel).getQuesId();
            logger.debug("Question added : {}",templateQuestionModel);
            switch (surveyData.getQuesType()) {
                case QUES_TYPE_RATING:
                    for (int i = BYTE_ZERO; i < surveyData.getNumberOfOptions(); i++) {
                        TemplateAnswerModel answerModel = new TemplateAnswerModel();
                        answerModel.setQuesId(quesId);
                        answerModel.setAnsText(valueOf(i + BYTE_ZERO));
                        answerRepo.save(answerModel);
                        logger.debug("Answer of rating questionType added : {}",answerModel);
                    }
                    break;
                case QUES_TYPE_CHECKBOX:
                case QUES_TYPE_RADIO:
                    for (String ans : surveyData.getAnswers()) {
                        TemplateAnswerModel model = new TemplateAnswerModel();
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
     * Archive template
     *
     * @param templateName : templateName
     * @return : ResponseEntity<ResponseMessage>
     */
    @Override
    public ResponseEntity<ResponseMessage> archiveTemplate(String templateName) {
        logger.info(STARTING_METHOD_EXECUTION);
        try {
            ResponseMessage responseMessage = new ResponseMessage();
            TemplateModel templateModel = templateRepo.findByTemplateName(templateName);
            templateModel.setArchived(true);
            templateModel.setUpdatedOn(System.nanoTime());
            templateRepo.save(templateModel);
            responseMessage.setMessage(ARCHIVED);
            responseMessage.setStatusCode(HttpStatus.OK.value());
            logger.info(EXITING_METHOD_EXECUTION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Error occurred while archiving template :: ",ex);
            throw new APIException(INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Unarchive template
     *
     * @param templateName : TemplateName.
     * @return : ResponseMessage.
     */
    public ResponseMessage unarchiveTemplate(String templateName) {
        logger.info(STARTING_METHOD_EXECUTION);
        ResponseMessage responseMessage = new ResponseMessage();
        TemplateModel templateModel = templateRepo.findByTemplateName(templateName);
        templateModel.setArchived(false);
        templateModel.setUpdatedOn(System.currentTimeMillis());
        templateRepo.save(templateModel);
        responseMessage.setMessage(UNARCHIVED);
        responseMessage.setStatusCode(HttpStatus.OK.value());
        logger.info(EXITING_METHOD_EXECUTION);
        return responseMessage;
    }

    /**
     * Delete Template
     *
     * @param templateName : templateName
     * @return ResponseEntity<Void> --> No Content 204.
     * @throws ForbiddenException userRepo.getSurveyIdByName(templateName) is not empty.
     */
    @Override
    public ResponseEntity<Void> deleteTemplates(String templateName) {
        logger.info(STARTING_METHOD_EXECUTION);
            if (!(userRepo.getSurveyIdByName(templateName).isEmpty())) {
                throw new ForbiddenException(TEMPLATE_NOT_DELETED);
            }
        try
        {
            templateRepo.deleteTemplateModelByTemplateName(templateName);
            logger.info(EXITING_METHOD_EXECUTION);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            logger.error("Error occurred while deleting template :: ",ex);
            throw new APIException(INTERNAL_SERVER_ERROR);
        }


    }

}