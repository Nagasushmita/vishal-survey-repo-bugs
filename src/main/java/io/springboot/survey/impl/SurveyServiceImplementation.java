package io.springboot.survey.impl;

import io.springboot.survey.exception.CustomRetryException;
import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.mapper.SurveyStatusDto;
import io.springboot.survey.models.SurveyModel;
import io.springboot.survey.models.UploadFileModel;
import io.springboot.survey.pojo.GetRequestParam;
import io.springboot.survey.pojo.survey.impl.FindByCreatorUserIdParam;
import io.springboot.survey.pojo.survey.impl.StatusResponseParam;
import io.springboot.survey.repository.*;
import io.springboot.survey.response.StatusResponse;
import io.springboot.survey.response.SurveyResponse;
import io.springboot.survey.response.UploadFileResponse;
import io.springboot.survey.service.SurveyService;
import io.springboot.survey.utils.DynamicFiltering;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static io.springboot.survey.utils.Constants.ApiResponseConstant.INTERNAL_SERVER_ERROR;
import static io.springboot.survey.utils.Constants.CommonConstant.*;
import static io.springboot.survey.utils.Constants.ErrorMessageConstant.*;
import static io.springboot.survey.utils.Constants.FilterConstants.STATUS_RESPONSE_FILTER;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.SurveyModuleConstants.*;
import static io.springboot.survey.utils.Constants.TeamConstants.TEAM_NOT_FOUND;
import static io.springboot.survey.utils.Constants.TemplateModuleConstant.BYTE_MINUS_ONE;
import static io.springboot.survey.utils.Constants.ValidationConstant.CREATION_DATE;
import static io.springboot.survey.utils.Constants.ValidationConstant.SURVEY_NAME;


@Component
public class SurveyServiceImplementation  implements SurveyService {

    private final SurveyRepo surveyRepo;
    private final SurveyStatusRepo surveyStatusRepo;
    private final UserRepo userRepo;
    private final UploadFileRepo uploadFileRepo;
    private final TeamRepo teamRepo;

    private static final Logger logger= LoggerFactory.getLogger(SurveyServiceImplementation.class.getSimpleName());

    final Base64.Decoder decoder = Base64.getDecoder();

    public SurveyServiceImplementation(SurveyRepo surveyRepo, SurveyStatusRepo surveyStatusRepo, UserRepo userRepo, UploadFileRepo uploadFileRepo, TeamRepo teamRepo) {
        this.surveyRepo = surveyRepo;
        this.surveyStatusRepo = surveyStatusRepo;
        this.userRepo = userRepo;
        this.uploadFileRepo = uploadFileRepo;
        this.teamRepo = teamRepo;
    }


    /**
     * Return list of archived/unarchived io.springboot.survey(s) created by an user.
     *
     * @param params:FindByCreatorUserIdParam
     * @return List<SurveyModel>
     */
    private  List<SurveyModel> findByCreatorUserIdAndArchived(FindByCreatorUserIdParam params) {

        logger.info(STARTING_METHOD_EXECUTION);
        if (params.getPage() < 0)
            params.setPage(0);
        PageRequest paging = PageRequest.of(params.getPage(), params.getPageSize(), Sort.by(params.getSortBy()).ascending());
        logger.info(EXITING_METHOD_EXECUTION);
        return surveyRepo.findByCreatorUserIdAndArchived(params.getUserId(), paging, params.isArchived()).getContent();

    }

    /**
     * Return information about all the io.springboot.survey(s) created by a particular user
     *
     * @param getRequestParam:GetRequestParam
     * @throws ResourceNotFoundException : if surveyRepo.getCountByCreatedAndArchived() returns 0.
     * @return  List<StatusResponse> responses
     */
    @Override
    public MappingJacksonValue getAllSurvey(GetRequestParam getRequestParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        int userId = userRepo.getUserIdByUserEmail(getRequestParam.getEmail());
        List<StatusResponse> responses = new ArrayList<>();
        Integer modelSize = surveyRepo.getCountByCreatedAndArchived(userRepo.getUserIdByUserEmail(getRequestParam.getEmail()), false);
        if (modelSize.equals(0)) {
            logger.debug("No Survey found for email : {}", getRequestParam.getEmail());
            throw new ResourceNotFoundException(NO_SURVEYS_FOUND);
        }
        FindByCreatorUserIdParam params=new FindByCreatorUserIdParam(userId, getRequestParam.getPage(), getRequestParam.getPageSize(), getRequestParam.getSortBy(), true);
        List<SurveyModel> modelList = findByCreatorUserIdAndArchived(params);
        StatusResponseParam statusResponseParam=new StatusResponseParam(getRequestParam.getPageSize(), responses, modelList, modelSize);
        logger.info(EXITING_METHOD_EXECUTION);
        return getStatusResponse(statusResponseParam);
    }

    /**
     * Return information about all the archived io.springboot.survey(s) created by a particular user
     *
     * @param getRequestParam:Request Params
     * @throws ResourceNotFoundException : if surveyRepo.getCountByCreatedAndArchived() returns 0.
     * @return : List<StatusResponse> responses
     */
    @Override
    public MappingJacksonValue getArchivedSurvey(GetRequestParam getRequestParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        Integer modelSize = surveyRepo.getCountByCreatedAndArchived(userRepo.getUserIdByUserEmail(getRequestParam.getEmail()),true);
        int userId = userRepo.getUserIdByUserEmail(getRequestParam.getEmail());
        if (modelSize.equals(0)) {
            logger.debug("No Survey found for email : {}", getRequestParam.getEmail());
            throw new ResourceNotFoundException(NO_SURVEYS_FOUND);
        }
        FindByCreatorUserIdParam param=new FindByCreatorUserIdParam(userId, getRequestParam.getPage(), getRequestParam.getPageSize(), getRequestParam.getSortBy(), false);
        List<StatusResponse> responses = new ArrayList<>();
        List<SurveyModel> modelList = findByCreatorUserIdAndArchived(param);
        StatusResponseParam statusResponseParam=new StatusResponseParam(getRequestParam.getPageSize(), responses, modelList, modelSize);
        logger.info(EXITING_METHOD_EXECUTION);
        return getStatusResponse(statusResponseParam);
    }

    /**
     * Return  List<StatusResponse> for getArchivedSurvey,getAllSurvey and findByCreatorUserIdAndArchived
     *
     * @param statusResponseParam:StatusResponseParam
     * @return : List<StatusResponse>
     */
    private MappingJacksonValue getStatusResponse(StatusResponseParam statusResponseParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        for (SurveyModel surveyModel : statusResponseParam.getModelList()) {
            int d = ((statusResponseParam.getModelSize()) % statusResponseParam.getPageSize());
            int q = ((statusResponseParam.getModelSize()) / statusResponseParam.getPageSize());
            StatusResponse statusResponse = new StatusResponse();
            statusResponse.setSurveyName(surveyModel.getSurveyName());
            statusResponse.setCreationDate(surveyModel.getCreationDate());
            statusResponse.setExpirationDate(surveyModel.getExpirationDate());
            statusResponse.setSurveyDescription(surveyModel.getSurveyDesc());
            statusResponse.setSurveyPendingCount(
                    surveyStatusRepo.getSizeBySurveyIdAndTaken(surveyModel.getSurveyId(), false));
            statusResponse.setSurveyTakenCount(
                    surveyStatusRepo.getSizeBySurveyIdAndTaken(surveyModel.getSurveyId(), true));
            statusResponse.setPageRequired(((d == 0) ? q : q + 1));
            statusResponseParam.getResponses().add(statusResponse);
        }
        DynamicFiltering dynamicFiltering = new DynamicFiltering();
        Set<String> fields=new HashSet<>(Arrays.asList(SURVEY_NAME,CREATION_DATE,EXPIRATION_DATE,
                SURVEY_DESCRIPTION,SURVEY_PENDING_COUNT,SURVEY_TAKEN_COUNT,PAGE_REQUIRED));
        logger.info(EXITING_METHOD_EXECUTION);
        return  dynamicFiltering.dynamicObjectFiltering(statusResponseParam.getResponses(),fields,STATUS_RESPONSE_FILTER);
    }


    /**
     * Return information by decoding the surveyName and creatorEmail from the link.
     *
     * @param link : link of the io.springboot.survey.
     * @param teamName : name of the team.
     * @return : SurveyResponse
     * @throws ResourceNotFoundException : teamRepo.findByTeamName() returns null.
     */
    @Override
    public SurveyResponse decodeLink(String link, String teamName) {
        logger.info(STARTING_METHOD_EXECUTION);
        String decodedLink = new String(decoder.decode(link));
        String[] dividedLink = decodedLink.split(EMPTY_STRING);
        SurveyResponse surveyResponse = new SurveyResponse();
        surveyResponse.setSurveyName(dividedLink[0]);
        surveyResponse.setCreatorEmail(dividedLink[1]);
        int teamId;
        if(teamName.equals(NULL)) {
            surveyResponse.setTeamName(N_A);
            teamId=BYTE_MINUS_ONE;
        }
        else {
            if(teamRepo.findByTeamName(teamName)==null)
                throw new ResourceNotFoundException(TEAM_NOT_FOUND);
            surveyResponse.setTeamName(teamName);
            teamId= teamRepo.getTeamId(teamName);
        }
        int surveyId = userRepo.getSurveyDataByNameAndId(dividedLink[0],dividedLink[1],false).getSurveyId();
        List<Integer> userId = userRepo.getSurveyById(surveyId).stream()
                .map(SurveyStatusDto::getUserId).collect(Collectors.toList());
        Set<String> mails = new HashSet<>();
        List<Integer> activeUser = new ArrayList<>();
        for (Integer id : userId) {
            if (surveyStatusRepo.findBySurveyIdAndUserIdAndTeamIdAndTaken(surveyId, id, teamId, false) != null)
                activeUser.add(id);
        }
        for (Integer id : activeUser) {
            mails.add((String) userRepo.getUserNameAndUserEmail(id).get(1));
        }
        surveyResponse.setEmails(mails);
        logger.info(EXITING_METHOD_EXECUTION);
        return surveyResponse;
    }


    /**
     * Upload file in database while giving response of io.springboot.survey
     *
     * @param file : MultipartFile
     * @return : UploadFileResponse
     */
    @Override
    public UploadFileResponse uploadFile(MultipartFile file) {
        logger.info(STARTING_METHOD_EXECUTION);
    try {
        UploadFileModel model = new UploadFileModel();
        model.setFileName(file.getOriginalFilename());
        model.setFileData(file.getBytes());
        model.setFileType(file.getContentType());
        uploadFileRepo.save(model);
        logger.debug("File saved : {}",model);
        UploadFileResponse uploadFileResponse = new UploadFileResponse();
        uploadFileResponse.setFileIdentity(model.getFileId());
        uploadFileResponse.setResponse(FILE_UPLOADED);
        uploadFileResponse.setStatus(SUCCESS);
        logger.info(EXITING_METHOD_EXECUTION);
        return uploadFileResponse;
    }
    catch (Exception ex)
        {
            logger.error("Error occurred while uploading file :: ", ex);
            throw new CustomRetryException(INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * if the upload file function does not succeed even after n tries then this method will be called.

     * @param customRetryException : CustomRetryException
     * @return : String
     */
    @Override
    @Recover
    public String retryMechanism(CustomRetryException customRetryException) {
        logger.info("Default Retry service ");
        return "All retries completed, so Fallback method called!!!";

    }

    /**
     * Return information about whether the template has been used to create io.springboot.survey in the past or not
     * if yes then number of times io.springboot.survey assigned to team and to individuals.
     *
     * @param templateName : templateName
     * @return :  Map<String, Integer>
     * @throws ResourceNotFoundException : if surveyRepo.findSurveyModelByTemplateId() returns empty list.
     */
    @Override
    public Map<String, Integer> tooltip(String templateName) {
        logger.info(STARTING_METHOD_EXECUTION);
        int templateId = userRepo.getTemplateIdByName(templateName);
        List<Integer> surveyId = surveyRepo.findSurveyModelByTemplateId(templateId).stream().map
                (SurveyModel::getSurveyId).collect(Collectors.toList());
        if(surveyId.isEmpty()) {
            logger.debug("Template {} not used yet",templateName);
            throw new ResourceNotFoundException(TEMPLATE_NOT_USED);
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return tooltipHelperFunction(surveyId);
    }

    /**
     * Tooltip helper function
     *
     * @param surveyId : suveyId.
     * @return : Map<String, Integer>
     */
    private Map<String, Integer> tooltipHelperFunction(@NotNull List<Integer> surveyId) {
        logger.info(STARTING_METHOD_EXECUTION);
        int teamCount = 0;
        int generalCount = 0;
        Map<String, Integer> map = new HashMap<>();
        for (Integer id : surveyId) {
            Set<Integer> teamId = userRepo.getSurveyById(id).stream().map(SurveyStatusDto::getTeamId).collect(Collectors.toSet());
            for (Integer team : teamId) {
                if (team.equals(-2))
                    generalCount=+2;
                else
                    teamCount++;
            }
        }
        map.put(TEAM_COUNT, teamCount);
        map.put(GENERAL_COUNT, generalCount);
        logger.info(EXITING_METHOD_EXECUTION);
        return map;
    }
}