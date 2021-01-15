package io.springboot.survey.repository;


import io.springboot.survey.mapper.SurveyResponseDto;
import io.springboot.survey.models.SurveyResponseModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.util.List;

import static io.springboot.survey.utils.Constants.SurveyModuleConstants.SURVEY_ID;

@Repository
@Transactional
public interface SurveyResponseRepo extends PagingAndSortingRepository<SurveyResponseModel,Integer>
{

    @Query("select p.responseId from SurveyResponseModel p where p.surveyId=?1")
    List<Integer> findResponseIdBySurveyId(@RequestParam(value = SURVEY_ID) int surveyId);

    List<SurveyResponseModel>findBySurveyId(int surveyId);
    List<SurveyResponseModel> findByUserIdAndSurveyId(int userId, int surveyId);
    List<SurveyResponseModel>findBySurveyIdAndResponseDateBetween(int surveyId, long startDate, long endDate);
    List<SurveyResponseModel>findBySurveyIdAndTeamIdAndResponseDateBetween(int surveyId, int teamId, long startDate, long endDate);
    List<SurveyResponseModel> findByTeamId(int teamId);
    List<SurveyResponseModel> findByTeamIdAndSurveyId(int teamId, int surveyId);

    @Query("select COUNT (s) from SurveyResponseModel s")
    Integer getSize();

    @Query("select s.userId,s.teamId,s.surveyId from SurveyResponseModel s where s.responseId=?1")
    Object[] getDataByResponseId(@Param("responseId") int responseId);

    @Query("select s.responseId,s.responseDate from SurveyResponseModel s where s.userId=?1 and s.surveyId=?2 and s.teamId=?3")
    Object[] getResponseIdAndTimestamp(@Param("userId") int userId, @Param("surveyId") int surveyId, @Param("teamId") Integer teamId);

    @Query("select s.responseId as responeId,s.teamId as teamId from SurveyResponseModel s where s.userId=?1 and s.surveyId=?2")
    List<SurveyResponseDto> getResponseIdAndTeamId(@Param("userId") int userId, @Param("surveyId") int surveyId);

    @Query("select DISTINCT (s.surveyId) from SurveyResponseModel s where s.teamId=?1")
    List<Integer> getSurveyIdByTeamId(@Param("teamId") int teamId);
}
