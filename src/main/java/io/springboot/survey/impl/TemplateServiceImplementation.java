package io.springboot.survey.impl;

import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.mapper.GetTemplate;
import io.springboot.survey.mapper.SurveyModelDto;
import io.springboot.survey.models.SurveyModel;
import io.springboot.survey.models.TemplateModel;
import io.springboot.survey.pojo.survey.impl.FindByCreatorUserIdParam;
import io.springboot.survey.pojo.template.GetTemplateParam;
import io.springboot.survey.pojo.template.PaginationResponseParam;
import io.springboot.survey.pojo.template.TemplatePaginationParam;
import io.springboot.survey.repository.*;
import io.springboot.survey.response.*;
import io.springboot.survey.service.TemplateService;
import io.springboot.survey.utils.DynamicFiltering;
import io.springboot.survey.utils.Pagination;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import javax.persistence.Tuple;
import java.util.*;

import static io.springboot.survey.utils.Constants.CommonConstant.*;
import static io.springboot.survey.utils.Constants.ErrorMessageConstant.*;
import static io.springboot.survey.utils.Constants.FilterConstants.PAGINATION_FILTER;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.SurveyModuleConstants.UNEXPECTED_VALUE;
import static io.springboot.survey.utils.Constants.TemplateModuleConstant.*;
import static io.springboot.survey.utils.Constants.ValidationConstant.SURVEY_NAME;
import static io.springboot.survey.utils.Constants.ValidationConstant.TEMPLATE_RESPONSE_LIST;


@Component
public class TemplateServiceImplementation implements TemplateService {


    private final TemplateRepo templateRepo;
    private final TemplateQuestionRepo templateQuestionRepo;
    private final TemplateAnswerRepo answerRepo;
    private final UserRepo userRepo;
    private final QuestionTypeRepo questionTypeRepo;
    private final SurveyRepo surveyRepo;

    private static final Logger logger= LoggerFactory.getLogger(TemplateServiceImplementation.class.getSimpleName());

    public TemplateServiceImplementation(TemplateRepo templateRepo, TemplateQuestionRepo templateQuestionRepo, TemplateAnswerRepo answerRepo, UserRepo userRepo, QuestionTypeRepo questionTypeRepo, SurveyRepo surveyRepo) {
        this.templateRepo = templateRepo;
        this.templateQuestionRepo = templateQuestionRepo;
        this.answerRepo = answerRepo;
        this.userRepo = userRepo;
        this.questionTypeRepo = questionTypeRepo;
        this.surveyRepo = surveyRepo;
    }

    /**
     * Returns list of answer for a question
     *
     * @param id : questionId
     * @return : List<String>
     */
    private List<String> findAnswerByQuesId(int id) {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return answerRepo.getAnswerByQuesId(id);
    }

    /**
     * Return the list of all the template
     *
     * @param page : current page number.
     * @param pageSize : number of object per page.
     * @param sortBy : field by which the sorting is to be done.
     * @return : List<TemplateModel>
     */
    private List<TemplateModel> findAllTemplate(int page, Integer pageSize, String sortBy) {
        logger.info(STARTING_METHOD_EXECUTION);
        if (page < 0)
            page = 0;
        PageRequest paging = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());
        logger.info(EXITING_METHOD_EXECUTION);
        return templateRepo.findByIsArchivedFalse(paging).getContent();

    }

    /**
     * Return  all the Unarchived templates created by an user
     *
     * @param email : email of the logged in user.
     * @return : List<TemplateCountResponse>
     * @throws  ResourceNotFoundException : if templateRepo.findByIsArchivedFalse() returns empty list.
     */
    @Override
    public List<TemplateCountResponse> getUnarchivedTemplate(String email) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<TemplateCountResponse> templateCountResponses = new ArrayList<>();
        List<TemplateModel> list = templateRepo.findByIsArchivedFalse();
        if (!list.isEmpty()) {
            for (TemplateModel templateModel : list) {
                TemplateCountResponse templateCountResponse = new TemplateCountResponse();
                templateCountResponse.setTemplateModel(templateModel);
                templateCountResponse.setUseCount(surveyRepo.getCountByTemplateId(templateModel.getTemplateId()));
                int userId = userRepo.getUserIdByUserEmail(email);
                SurveyModel surveyModel = surveyRepo.findByCreatorUserIdAndAndTemplateIdAndArchivedFalse(userId, templateModel.getTemplateId());
                templateCountResponse.setUsed(surveyModelNullCheck(surveyModel));
                templateCountResponses.add(templateCountResponse);
            }
            templateCountResponses.sort(Comparator.comparing(TemplateCountResponse::getUseCount).reversed());
            logger.info(EXITING_METHOD_EXECUTION);
            return templateCountResponses;
        }
        logger.debug("No template created by user with email {} found",email);
        logger.info(EXITING_METHOD_EXECUTION);
        throw new ResourceNotFoundException(NO_TEMPLATE_FOUND);
    }

    /**
     * Check if the template is used to create io.springboot.survey or not
     *
     * @param surveyModel :SurveyModel
     * @return : boolean
     */
    private boolean surveyModelNullCheck(SurveyModel surveyModel) {
        logger.info(STARTING_METHOD_EXECUTION);
        boolean isSurveyNull;
        isSurveyNull= surveyModel != null;
        logger.info(EXITING_METHOD_EXECUTION);
        return isSurveyNull;
    }
    /**
     * Return the list of all the template(s) created by a particular user
     * @param param :FindByCreatorUserIdParam
     * @return : List<TemplateModel>
     */

    private List<TemplateModel> findAllByCreatorId(FindByCreatorUserIdParam param) {
        logger.info(STARTING_METHOD_EXECUTION);
        if (param.getPage() < BYTE_ZERO)
            param.setPage(0);
        PageRequest paging = PageRequest.of(param.getPage(), param.getPageSize(), Sort.by(param.getSortBy()).descending());
        logger.info(EXITING_METHOD_EXECUTION);
        return templateRepo.findByCreatorUserIdAndIsArchivedFalse(param.getUserId(), paging).getContent();
    }

    /**
     * Return the list of all the archived template(s) created by a particular user
     * @param param :FindByCreatorUserIdParam
     * @return : List<TemplateModel>
     */
    private List<TemplateModel> findArchivedTemplates(FindByCreatorUserIdParam param) {
        logger.info(STARTING_METHOD_EXECUTION);
        if (param.getPage() < BYTE_ZERO)
            param.setPage(0);
        PageRequest paging = PageRequest.of(param.getPage(), param.getPageSize(), Sort.by(param.getSortBy()).descending());
        logger.info(EXITING_METHOD_EXECUTION);
        return templateRepo.findByCreatorUserIdAndIsArchivedTrue(param.getUserId(), paging).getContent();
    }

    /**
     * Return a preview of template
     * @param templateName : templateName
     * @return  List<GetSurveyResponse>
     */
    @Override
    public List<GetSurveyResponse> getTemplate(String templateName) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<GetSurveyResponse> response = new ArrayList<>();
        List<GetTemplate> list = templateRepo.getQuestionModelByName(templateName);
        for (GetTemplate templateQuestionModel : list) {
            GetSurveyResponse questionResponse = new GetSurveyResponse();
            questionResponse.setQuestion(templateQuestionModel.getQuesText());
            String quesType = questionTypeRepo.getQuestionNameByTypeId(templateQuestionModel.getTypeId());
            questionResponse.setQuesType(quesType);
            questionResponse.setMandatory(templateQuestionModel.getMandatory());
            switch (quesType) {
                case QUES_TYPE_RADIO:
                case QUES_TYPE_CHECKBOX:
                    questionResponse.setAnswers(findAnswerByQuesId(templateQuestionModel.getQuesId()));
                    break;
                case QUES_TYPE_FILE:
                    List<String> ans = new ArrayList<>();
                    ans.add(GET_TEMPLATE_FILE);
                    questionResponse.setAnswers(ans);
                    break;
                case QUES_TYPE_TEXT:
                    List<String> answer = new ArrayList<>();
                    answer.add(GET_TEMPLATE_TEXT);
                    questionResponse.setAnswers(answer);
                    break;
                case QUES_TYPE_RATING:
                    List<String> answers = new ArrayList<>();
                    answers.add(GET_TEMPLATE_RATING);
                    questionResponse.setAnswers(answers);
                    break;
                default:
                    logger.error("Unexpected filter value  : {}",quesType);
                    throw new IllegalArgumentException(UNEXPECTED_VALUE + quesType);
            }
            questionResponse.setNumberOfOptions(findAnswerByQuesId(templateQuestionModel.getQuesId()).size());
            response.add(questionResponse);
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return response;
    }

    /**
     * For getting all the templates created by an user
     *
     * @param getTemplateParam :GetTemplateParam
     * @return : List<TemplateResponse>
     */
    @Override
    public MappingJacksonValue getAllTemplateByUserId(GetTemplateParam getTemplateParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<Integer> templateId = surveyRepo.getSurveyIdByCreatorId(userRepo.getUserIdByUserEmail(getTemplateParam.getEmail()),false);
        templateId.remove(null);
        PaginationResponse paginationResponse = templatePagination(new TemplatePaginationParam(templateId, getTemplateParam.getPage(), getTemplateParam.getPageSize(), getTemplateParam.getSortBy()));
        logger.info(EXITING_METHOD_EXECUTION);
        return getDynamicFiltering(paginationResponse);
    }

    /**
     * @param paginationResponse : PaginationResponse
     * @return MappingJacksonValue
     */
    private MappingJacksonValue getDynamicFiltering(PaginationResponse paginationResponse) {
        logger.info(STARTING_METHOD_EXECUTION);
        Set<String> filter = new HashSet<>(Arrays.asList(TEMPLATE_RESPONSE_LIST, PAGE_REQUIRED));
        DynamicFiltering dynamicFiltering = new DynamicFiltering();
        logger.info(EXITING_METHOD_EXECUTION);
        return dynamicFiltering.dynamicObjectFiltering(paginationResponse, filter, PAGINATION_FILTER);
    }


    /**
     * Template Pagination
     * @param param :TemplatePaginationParam
     * @return PaginationResponse
     */
    private PaginationResponse templatePagination(TemplatePaginationParam param) {
        logger.info(STARTING_METHOD_EXECUTION);
        PaginationResponse paginationResponse = new PaginationResponse();
        List<TemplateModel> templateModels;
        List<TemplateModel> templateModelList;
        List<TemplateResponse> templateResponses = new ArrayList<>();
        if (param.getTemplateId().isEmpty()) {
            logger.debug("List of Template Ids empty");
            templateModels = templateRepo.findByIsArchivedFalse();
            int d = ((templateModels.size()) % param.getPageSize());
            int q = ((templateModels.size()) / param.getPageSize());
            templateModelList = findAllTemplate(param.getPage(), param.getPageSize(), param.getSortBy());
            for (TemplateModel templateModel : templateModelList) {
                TemplateResponse templateResponse = new TemplateResponse();
                templateResponse.setTemplate(templateModel);
                templateResponse.setNoOfQuestions(templateQuestionRepo.findByTemplateIdSize(templateModel.getTemplateId()));
                templateResponses.add(templateResponse);
            }
            paginationResponse.setTemplateResponsesList(templateResponses);
            paginationResponse.setPageRequired(((d == BYTE_ZERO) ? q : q + BYTE_ONE));
        } else {
            logger.debug("List of Template Ids not empty");
            templateModels = templateRepo.findByIsArchivedFalse();
            Pagination pagination = new Pagination();
            templateModels.removeIf(templateModel -> param.getTemplateId().contains(templateModel.getTemplateId()));
            for (TemplateModel templateModel : templateModels) {
                TemplateResponse templateResponse = new TemplateResponse();
                templateResponse.setNoOfQuestions(templateQuestionRepo.findByTemplateIdSize(templateModel.getTemplateId()));
                templateResponse.setTemplate(templateModel);
                templateResponses.add(templateResponse);
            }
            int d = ((templateModels.size()) % param.getPageSize());
            int q = ((templateModels.size()) / param.getPageSize());
            paginationResponse.setTemplateResponsesList(pagination.surveyPagination(templateResponses, param.getPage(), param.getPageSize()));
            paginationResponse.setPageRequired(((d == BYTE_ZERO) ? q : q + BYTE_ONE));
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return paginationResponse;

    }

    /**
     *Pagination Response for getMyTemplate
     * @param param :PaginationResponseParam
     * @return PaginationResponse
     */
    private PaginationResponse paginationResponse(PaginationResponseParam param) {
        logger.info(STARTING_METHOD_EXECUTION);
        PaginationResponse paginationResponse = new PaginationResponse();
        List<TemplateResponse> templateResponses = new ArrayList<>();
        List<TemplateModel> templateModels = findAllByCreatorId(new FindByCreatorUserIdParam(
                userRepo.getUserIdByUserEmail(param.getEmail()), param.getPage(), param.getPageSize(), param.getSortBy(),false));
        Integer templateModelListSize =templateRepo.findByCreatorUserIdSize(
                userRepo.getUserIdByUserEmail(param.getEmail()),false);
        for (TemplateModel templateModel : templateModels) {
            TemplateResponse templateResponse = new TemplateResponse();
            templateResponse.setTemplate(templateModel);
            templateResponse.setNoOfQuestions(templateQuestionRepo.findByTemplateIdSize(templateModel.getTemplateId()));
            templateResponses.add(templateResponse);
        }
        int d = ((templateModelListSize) % param.getPageSize());
        int q = ((templateModelListSize) / param.getPageSize());
        paginationResponse.setTemplateResponsesList(templateResponses);
        paginationResponse.setPageRequired(((d == BYTE_ZERO) ? q : q + BYTE_ONE));
        logger.info(EXITING_METHOD_EXECUTION);
        return paginationResponse;
    }

    /**
     *Return all the template created by an user
     * @param getTemplateParam :GetTemplateParam
     * @return PaginationResponse
     * @throws ResourceNotFoundException: if  templateRepo.findByCreatorUserIdAndIsArchivedFalse() returns
     * empty list.
     */
    @Override
    public MappingJacksonValue getMyTemplate(GetTemplateParam getTemplateParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<Integer> templateId = surveyRepo.getTemplateIdByCreatorAndArchived(userRepo.getUserIdByUserEmail(getTemplateParam.getEmail()), false);
        templateId.remove(null);
        PaginationResponse paginationResponse = new PaginationResponse();
        if (templateId.isEmpty()) {
            logger.debug("List of Template Ids empty");
            paginationResponse = paginationResponse(new PaginationResponseParam(getTemplateParam.getEmail(), getTemplateParam.getPage(), getTemplateParam.getPageSize(), getTemplateParam.getSortBy()));
        } else {
            logger.debug("List of Template Ids not empty");
            List<TemplateModel> templateModelList = templateRepo.findByCreatorUserIdAndIsArchivedFalse(
                    userRepo.getUserIdByUserEmail(getTemplateParam.getEmail()));
            if (templateModelList.isEmpty())
                throw new ResourceNotFoundException(NO_TEMPLATE_FOUND);
            Pagination pagination = new Pagination();
            List<TemplateResponse> templateResponses = getMyTemplateHelperFunction(templateModelList, templateId);
            int d = ((templateModelList.size()) % getTemplateParam.getPageSize());
            int q = ((templateModelList.size()) / getTemplateParam.getPageSize());
            paginationResponse.setPageRequired(((d == BYTE_ZERO) ? q : q + BYTE_ONE));
            paginationResponse.setTemplateResponsesList(pagination.surveyPagination(templateResponses, getTemplateParam.getPage(), getTemplateParam.getPageSize()));
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return getDynamicFiltering(paginationResponse);

    }

    /**
     * GetMyTemplateHelperFunction
     *
     * @param templateModelList : List<TemplateModel>
     * @param templateId :List of templateId
     * @return :  List<TemplateResponse>
     */
    private List<TemplateResponse> getMyTemplateHelperFunction(@NotNull List<TemplateModel> templateModelList, List<Integer> templateId) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<TemplateResponse> templateResponses = new ArrayList<>();
        templateModelList.removeIf(templateModel -> templateId.contains(templateModel.getTemplateId()));
        for (TemplateModel templateModel : templateModelList) {
            TemplateResponse templateResponse = new TemplateResponse();
            templateResponse.setTemplate(templateModel);
            templateResponse.setNoOfQuestions(templateQuestionRepo.findByTemplateIdSize(templateModel.getTemplateId()));
            templateResponses.add(templateResponse);
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return templateResponses;
    }

    /**
     * Return list of all the template that has been used to create a io.springboot.survey more than once
     *
     * @param page : current page number.
     * @param pageSize : number of object per page.
     * @param sortBy :  field by which sorting is to be done.
     * @return : List<TemplateResponse>
     * @throws ResourceNotFoundException: if surveyRepo.getTemplateIdByArchived() returns empty list.
     *
     */
    @Override
    public MappingJacksonValue getUsedTemplates(Integer page, Integer pageSize, String sortBy) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<TemplateResponse> templateResponses = new ArrayList<>();
        List<Integer> uniqueTemplate = surveyRepo.getTemplateIdByArchived(false);
        uniqueTemplate.remove(null);
        if (uniqueTemplate.isEmpty()) {
            logger.debug("No template used");
            logger.info(EXITING_METHOD_EXECUTION);
            throw new ResourceNotFoundException(NO_TEMPLATE_USED);
        }
        for (Integer tempId : uniqueTemplate) {
            if (surveyRepo.getCountByTemplateId(tempId) > BYTE_ONE) {
                TemplateResponse templateResponse = new TemplateResponse();
                templateResponse.setNoOfSurveys(surveyRepo.getSurveyCount(tempId));
                templateResponse.setTotalResponses(surveyRepo.getResponseCountByTemplateId(tempId));
                templateResponse.setTemplate(templateRepo.findByTemplateId(tempId));
                templateResponses.add(templateResponse);
            }
        }
        PaginationResponse paginationResponse = new PaginationResponse();
        Pagination pagination = new Pagination();
        int d = ((templateResponses.size()) % pageSize);
        int q = ((templateResponses.size()) / pageSize);
        paginationResponse.setTemplateResponsesList(pagination.surveyPagination(templateResponses, page, pageSize));
        paginationResponse.setPageRequired(((d == BYTE_ZERO) ? q : q + BYTE_ONE));
        logger.info(EXITING_METHOD_EXECUTION);
        return getDynamicFiltering(paginationResponse);
    }


    /**
     * Return list of all the archived template created by an user
     * @param getTemplateParam :GetTemplateParam
     * @return : List<TemplateResponse>
     * @throws ResourceNotFoundException: if templateRepo.findByCreatorUserIdSize() returns 0.
     *
     */
    @Override
    public MappingJacksonValue getArchivedTemplate(GetTemplateParam getTemplateParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        Integer templateModelSize = templateRepo.findByCreatorUserIdSize(userRepo.getUserIdByUserEmail(getTemplateParam.getEmail()),true);
        if (templateModelSize.equals(0)) {
            logger.debug("No template created by user with email {} found",getTemplateParam.getEmail());
            logger.info(EXITING_METHOD_EXECUTION);
            throw new ResourceNotFoundException(TEMPLATE_NOT_FOUND);
        }
        List<TemplateResponse> templateResponses = new ArrayList<>();
        PaginationResponse paginationResponse = new PaginationResponse();
        for (TemplateModel templateModel : findArchivedTemplates(new FindByCreatorUserIdParam(
                userRepo.getUserIdByUserEmail(getTemplateParam.getEmail()), getTemplateParam.getPage(), getTemplateParam.getPageSize(), getTemplateParam.getSortBy(),false))) {
            TemplateResponse templateResponse = new TemplateResponse();
            templateResponse.setTemplate(templateModel);
            templateResponse.setNoOfQuestions(templateQuestionRepo.findByTemplateIdSize(templateModel.getTemplateId()));
            templateResponses.add(templateResponse);
        }
        int d = ((templateModelSize) % getTemplateParam.getPageSize());
        int q = ((templateModelSize) / getTemplateParam.getPageSize());
        paginationResponse.setTemplateResponsesList(templateResponses);
        paginationResponse.setPageRequired(((d == BYTE_ZERO) ? q : q + BYTE_ONE));
        logger.info(EXITING_METHOD_EXECUTION);
        return getDynamicFiltering(paginationResponse);
    }

    /**
     * For showing the information about a template
     *
     * @param templateName : templateName
     * @return : TemplateInformation
     * @throws ResourceNotFoundException : if  templateRepo.getSurveyModelByName() returns empty list.
     */
    @Override
    public TemplateInformation templateInformation(String templateName) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<SurveyModelDto> surveys = templateRepo.getSurveyModelByName(templateName);
        if (surveys.isEmpty()){
            logger.debug("No io.springboot.survey created using template : {}",templateName);
            logger.info(EXITING_METHOD_EXECUTION);
            throw new ResourceNotFoundException(NO_SURVEY_CREATED);
        }
        TemplateInformation templateInformation = new TemplateInformation();
        List<HashMap<String, String>> surveyDetails = new ArrayList<>();
        templateInformation.setTemplateName(templateName);
        Object[] obj = templateRepo.getDescAndCreationDate(templateName);
        templateInformation.setTemplateDesc((String) obj[0]);
        templateInformation.setCreatedOn((Long) obj[1]);
        templateInformation.setQuestionCount((Integer) obj[2]);
        templateInformation.setSurveyCount(surveys.size());
        for (SurveyModelDto survey : surveys) {
            HashMap<String, String> map = new HashMap<>();
            map.put(SURVEY_NAME, survey.getSurveyName());
            map.put(SURVEY_DESC, survey.getSurveyDesc());
            Tuple tuple=userRepo.getUserNameAndUserEmail(survey.getCreatorUserId());
            map.put(ASSIGNED_BY, (String) tuple.get(0));
            map.put(ASSIGNED_BY_EMAIL, (String) tuple.get(1));
            map.put(ASSIGNED_TO, String.valueOf(survey.getSize()));
            surveyDetails.add(map);
        }
        templateInformation.setSurveyDetails(surveyDetails);
        logger.info(EXITING_METHOD_EXECUTION);
        return templateInformation;
    }

}