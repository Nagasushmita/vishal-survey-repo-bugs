package io.springboot.survey.repository;

import io.springboot.survey.mapper.SurveyStatusDto;
import io.springboot.survey.models.SurveyStatusModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface SurveyStatusRepo extends PagingAndSortingRepository<SurveyStatusModel,Integer> {


    List<SurveyStatusModel> findByUserId(int userId);

    SurveyStatusModel findByParKey(int key);

    @Query("select distinct (s.teamId) from SurveyStatusModel s where s.surveyId=?1")
    Set<Integer> findBySurveyId(@Param("surveyId") int surveyId);

    SurveyStatusModel findBySurveyIdAndUserIdAndTeamIdAndTaken(int surveyId, int userId, int teamId, boolean taken);

    @Query("select COUNT (s) from SurveyStatusModel s where s.surveyId=?1")
    Integer getStatusModelSizeById(@Param("surveyId") int surveyId);

    @Query("select COUNT (s) from SurveyStatusModel s where s.surveyId=?1 and s.taken=?2")
    Integer getSizeBySurveyIdAndTaken(@Param("surveyId") int surveyId, @Param("taken") boolean taken);

    @Query("select s.userId as userId,s.teamId as teamId from SurveyStatusModel s where s.surveyId=?1 and s.taken=?2")
    List<SurveyStatusDto> getSurveyByIdAndTaken(@Param("surveyId") int surveyId, @Param("taken") boolean taken);

    @Query("select s.userId as userId,s.teamId as teamId from SurveyStatusModel s where s.surveyId=?1")
    List<SurveyStatusDto> getSurveyById(@Param("surveyId") int surveyId);

    @Query("select distinct(parkey) from SurveyStatusModel s where s.surveyId=?1 and s.taken=?2")
    List<Integer> getParKeyByIdAndTaken(@Param("surveyId") int surveyId, @Param("taken") boolean taken);

    @Query("select distinct(s.userId)  from SurveyStatusModel s where s.surveyId=?1 and s.taken=?2")
    Set<Integer> getUserIdByIdAndTaken(@Param("surveyId") int surveyId, @Param("taken") boolean taken);

    @Query("select distinct(s.assignedBy)  from SurveyStatusModel s where s.userId=?1")
    Set<Integer> getAssignedByUserId(@Param("userId") int userId);

    @Query("select COUNT (s) from SurveyStatusModel s where s.userId=?1 and s.assignedBy=?2")
    Integer getSizeByUserIdAndAssignedBy(@Param("userId") int userId, @Param("assignedBy") int assignedBy);

    @Query("select COUNT (S) from SurveyStatusModel s where s.userId=?1 and s.assignedBy=?2 and s.taken=?3")
    Integer getSizeByUserIdAndAssignedByAndTaken(@Param("userId") int userId, @Param("assignedBy") int assignedBy, @Param("taken") boolean taken);

    @Query("select COUNT (s) from SurveyStatusModel s where s.assignedBy=?1")
    Integer getSizeByAssignedBy(@Param("assignedBy") int assignedBy);


}
