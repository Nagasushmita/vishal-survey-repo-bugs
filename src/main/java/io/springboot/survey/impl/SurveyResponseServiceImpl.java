package io.springboot.survey.impl;

import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.mapper.SurveyByNameAndCreatorId;
import io.springboot.survey.models.*;
import io.springboot.survey.repository.*;
import io.springboot.survey.request.UserRequest;
import io.springboot.survey.request.UserSurveyRequest;
import io.springboot.survey.response.GetSurveyResponse;
import io.springboot.survey.response.QuestionResponse;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.response.SurveyData;
import io.springboot.survey.service.SurveyResponseService;
import io.springboot.survey.service.SurveyUserResponseService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static io.springboot.survey.utils.Constants.CommonConstant.*;
import static io.springboot.survey.utils.Constants.ErrorMessageConstant.*;
import static io.springboot.survey.utils.Constants.LoggerConstants.*;


@Component
public class SurveyResponseServiceImpl implements SurveyResponseService {

    private final UserRepo userRepo;
    private final SurveyResponseRepo surveyResponseRepo;
    private final QuestionRepo questionRepo;
    private final ResponseRepo responseRepo;
    private final QuestionTypeRepo questionTypeRepo;
    private final SurveyRepo surveyRepo;
    private final SurveyUserResponseService surveyUserResponseService;
    private final Base64.Decoder decoder = Base64.getDecoder();

    private static final Logger logger= LoggerFactory.getLogger(SurveyResponseServiceImpl.class.getSimpleName());

    public SurveyResponseServiceImpl(UserRepo userRepo, SurveyResponseRepo surveyResponseRepo, QuestionRepo questionRepo, ResponseRepo responseRepo, QuestionTypeRepo questionTypeRepo, SurveyRepo surveyRepo, SurveyUserResponseService surveyUserResponseService) {
        this.userRepo = userRepo;
        this.surveyResponseRepo = surveyResponseRepo;
        this.questionRepo = questionRepo;
        this.responseRepo = responseRepo;
        this.questionTypeRepo = questionTypeRepo;
        this.surveyRepo = surveyRepo;
        this.surveyUserResponseService = surveyUserResponseService;
    }


    /**
     * Take user response for question of a io.springboot.survey and save it in database.
     *
     * @param userSurveyRequest : UserSurveyRequest
     * @return : ResponseEntity<ResponseMessage>
     */
    @Override
    public ResponseEntity<ResponseMessage> surveyResponse(@NotNull UserSurveyRequest userSurveyRequest) {
        logger.info(STARTING_METHOD_EXECUTION);
        SurveyResponseModel ob = new SurveyResponseModel();
        String teamName = userSurveyRequest.getTeamName();
        ob.setSurveyId(userRepo.getSurveyDataByNameAndId(userSurveyRequest.getSurveyName(),
                userSurveyRequest.getCreatorEmail(),false).getSurveyId());
        ob.setUserId(userRepo.getUserIdByUserEmail(userSurveyRequest.getEmail()));
        ob.setResponseDate(System.currentTimeMillis());
        if (!NO_ACTIVE_SURVEY.equals(teamName)) {
            ob.setTeamId(
                    userRepo.getTeamId(userSurveyRequest.getTeamName()));
        } else {
            ob.setTeamId(-1);
        }
        surveyResponseRepo.save(ob);
        logger.debug("SurveyResponse saved : {}",ob);
        int surId = ob.getSurveyId();
        int resId = ob.getResponseId();
        SurveyStatusModel surveyStatusModel;
        if (!N_A.equals(teamName)) {
            surveyStatusModel = userRepo.findBySurveyIdAndUserIdAndTeamId(surId, userRepo.getUserIdByUserEmail
                    (userSurveyRequest.getEmail()), userRepo.getRoleIdByUserEmail(userSurveyRequest.getTeamName()));
        } else {
            surveyStatusModel = userRepo.findBySurveyIdAndUserIdAndTeamId(surId, userRepo.getUserIdByUserEmail
                    (userSurveyRequest.getEmail()), -1);
        }
        surveyStatusModel.setTaken(true);
        List<SurveyData> list = userSurveyRequest.getSurveyDataList();
        surveyResponseHelperFunction(list,surId,resId);
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage(RESPONSE_STORED);
        responseMessage.setStatusCode(HttpStatus.OK.value());
        logger.info(EXITING_METHOD_EXECUTION);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);

    }

    /**
     * Save the answers in the database according to the question type.
     *
     * @param list : List<SurveyData>
     * @param surId : surveyId
     * @param resId : responseId
     */
    private void surveyResponseHelperFunction(List<SurveyData> list,int surId,int resId)
    {
        logger.info(STARTING_METHOD_EXECUTION);
        for (SurveyData model : list) {
            String quesType = questionTypeRepo.getQuestTypeNameByQuestion(model.getQuestion(), surId);
            switch (quesType) {
                case QUES_TYPE_RADIO:
                case QUES_TYPE_CHECKBOX:
                case QUES_TYPE_RATING:
                    List<String> answers = model.getAnswers();
                    if (answers.isEmpty()) {
                        ResponseModel obj = new ResponseModel();
                        obj.setAnswerId(null);
                        obj.setResponseId(resId);
                        obj.setQuesId(questionRepo.getIdBySurveyIdAndQuesText(surId, model.getQuestion()));
                        obj.setFileId(null);
                        obj.setTextAnswer(null);
                        responseRepo.save(obj);
                        logger.debug(RESPONSE_SAVED,obj);
                    } else {
                        for (String ans : answers) {
                            ResponseModel obj = new ResponseModel();
                            obj.setResponseId(resId);
                            obj.setTextAnswer(null);
                            obj.setQuesId(questionRepo.getIdBySurveyIdAndQuesText(surId, model.getQuestion()));
                            obj.setAnswerId(questionRepo.getIdByQuesIdAndAnsText(obj.getQuesId(), ans));
                            obj.setFileId(null);
                            responseRepo.save(obj);
                            logger.debug(RESPONSE_SAVED,obj);
                        }
                    }
                    break;
                case QUES_TYPE_TEXT:
                    ResponseModel obj = new ResponseModel();
                    obj.setResponseId(resId);
                    obj.setQuesId(questionRepo.getIdBySurveyIdAndQuesText(surId, model.getQuestion()));
                    obj.setAnswerId(null);
                    obj.setTextAnswer(model.getAnswerText());
                    obj.setFileId(null);
                    responseRepo.save(obj);
                    logger.debug(RESPONSE_SAVED,obj);
                    break;
                case QUES_TYPE_FILE:
                    ResponseModel object = new ResponseModel();
                    object.setResponseId(resId);
                    object.setQuesId(questionRepo.getIdBySurveyIdAndQuesText(surId, model.getQuestion()));
                    object.setAnswerId(null);
                    object.setTextAnswer(null);
                    object.setFileId(model.getFile());
                    responseRepo.save(object);
                    logger.debug(RESPONSE_SAVED,object);
                    break;
                default:
            }
        }
        logger.info(EXITING_METHOD_EXECUTION);
    }


    /**
     * Return preview of io.springboot.survey created by a user
     *
     * @param surveyName : SurveyName
     * @param creatorEmail : email of the logged in user.
     * @return :  List<GetSurveyResponse>
     */
    @Override
    public List<GetSurveyResponse> getSurvey(String surveyName, String creatorEmail) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<GetSurveyResponse> response = new ArrayList<>();
        SurveyByNameAndCreatorId surveyData= userRepo.getSurveyDataByNameAndId(surveyName,creatorEmail,false);
        int surveyId=surveyData.getSurveyId();
        List<QuestionModel> list = questionRepo.findAllBySurveyId(surveyId);
        for (QuestionModel questionModel : list) {
            GetSurveyResponse getSurveyResponse = new GetSurveyResponse();
            String quesType = questionTypeRepo.getQuestionNameByTypeId((questionModel.getQuesTypeId()));
            getSurveyResponse.setSurveyName(surveyName);
            getSurveyResponse.setSurveyDescription(surveyData.getSurveyDesc());
            switch (quesType) {
                case QUES_TYPE_RADIO:
                case QUES_TYPE_CHECKBOX:
                    getSurveyResponse.setAnswers(findAllAnswer(questionModel.getQuesId()));
                    break;
                case QUES_TYPE_FILE:
                    List<String> ans = new ArrayList<>();
                    ans.add(GET_TEMPLATE_FILE);
                    getSurveyResponse.setAnswers(ans);
                    break;
                case QUES_TYPE_TEXT:
                    List<String> answer = new ArrayList<>();
                    answer.add(GET_TEMPLATE_TEXT);
                    getSurveyResponse.setAnswers(answer);
                    break;
                case QUES_TYPE_RATING:
                    List<String> answers = new ArrayList<>();
                    answers.add(GET_TEMPLATE_RATING);
                    getSurveyResponse.setAnswers(answers);
                    break;
                default:
                    getSurveyResponse.setAnswers(null);
            }
            getSurveyResponse.setQuestion(questionModel.getQuesText());
            getSurveyResponse.setNumberOfOptions(questionRepo.getAnswerCountById(surveyId,questionModel.getQuesText()));
            getSurveyResponse.setQuesType(quesType);
            getSurveyResponse.setMandatory(questionModel.getMandatory());
            response.add(getSurveyResponse);
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return response;
    }

    /**
     * Extract surveyName and creatorEmail from surveyLink and then return
     * a preview of the io.springboot.survey
     *
     * @param link : link of the io.springboot.survey.
     * @return : List<GetSurveyResponse>
     */
    @Override
    public List<GetSurveyResponse> getSurveyByLink(String link) {
        logger.info(STARTING_METHOD_EXECUTION);
        String decodedLink = new String(decoder.decode(link));
        String[] dividedLink = decodedLink.split(BACK_SLASH);
        logger.info(EXITING_METHOD_EXECUTION);
        return getSurvey(dividedLink[0], dividedLink[1]);
    }

    /**
     * Return list of answer for a question having questionId= id
     *
     * @param id : questionId
     * @return : List<String>
     */
    private List<String> findAllAnswer(int id) {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return  questionRepo.getAnsIdByQuesId(id);

    }

    /**
     * Return response the user gave to a particular io.springboot.survey.
     *
     * @param userRequest : UserRequest
     * @throws ResourceNotFoundException : if surveyRepo.findBySurveyNameAndCreatorUserId() returns null.
     * @return : List<QuestionResponse>
     */
    @Override
    public List<QuestionResponse> getUserResponse(@NotNull UserRequest userRequest) {
        logger.info(STARTING_METHOD_EXECUTION);
        if (surveyRepo.findBySurveyNameAndCreatorUserId(userRequest.getSurveyName(),
                userRepo.getUserIdByUserEmail(userRequest.getCreatorEmail()))!= null) {
            logger.info(EXITING_METHOD_EXECUTION);
            return getUserResponseHelperFunction(userRequest);
        }
        logger.debug("Survey {} does not exist",userRequest.getSurveyName());
        throw new ResourceNotFoundException(SURVEY_NOT_FOUND);
    }

    /**
     * UserResponseHelperFunction
     *
     * @param userRequest  : UserRequest
     * @return : List<QuestionResponse>
     */
    private List<QuestionResponse> getUserResponseHelperFunction(@NotNull UserRequest userRequest)
    {
        logger.info(STARTING_METHOD_EXECUTION);
        int surveyId;
        SurveyModel surveyModel = surveyRepo.findBySurveyNameAndCreatorUserIdAndArchivedFalse(userRequest.getSurveyName(),
                userRepo.getUserIdByUserEmail(userRequest.getCreatorEmail()));

        if (surveyModel != null) {
            surveyId = surveyModel.getSurveyId();
        } else
            surveyId = (int) surveyRepo.getSurveyIdAndLink(userRequest.getSurveyName(),
                    userRepo.getUserIdByUserEmail(userRequest.getCreatorEmail()))[0];
        int userId = userRepo.getUserIdByUserEmail(userRequest.getEmail());
        Integer responseId;
        String teamName = userRequest.getTeamName();
        if (!N_A.equals(teamName)) {
            int teamId = userRepo.getTeamId(teamName);
            responseId = (Integer) surveyResponseRepo.getResponseIdAndTimestamp(userId, surveyId, teamId)[0];
        } else {
            responseId = (Integer) surveyResponseRepo.getResponseIdAndTimestamp(userId, surveyId, -1)[0];
        }
        List<Integer> uniqueQuestion=questionRepo.getQuestIdBySurveyId(surveyId);
        logger.info(EXITING_METHOD_EXECUTION);
        return surveyUserResponseService.getUserResponse(uniqueQuestion,surveyId,responseId);
    }

}
