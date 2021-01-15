package io.springboot.survey.repository;


import io.springboot.survey.mapper.SurveyByNameAndCreatorId;
import io.springboot.survey.mapper.SurveyStatusDto;
import io.springboot.survey.mapper.SurveyTakenDto;
import io.springboot.survey.models.SurveyModel;
import io.springboot.survey.models.SurveyStatusModel;
import io.springboot.survey.models.UserModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface UserRepo extends PagingAndSortingRepository<UserModel, String>, JpaSpecificationExecutor<UserModel> {

    UserModel findByUserId(int userId);
    UserModel findByUserEmail(String email);
    UserModel findByUserName(String name);
    List<UserModel> findByRoleId(int roleId);
    long deleteByUserEmail(String email);
    @NotNull List<UserModel> findAll();
    Page<UserModel> findAllByUserEmailNotContainingAndActive(String userEmail, Pageable pageable, boolean isActive);
    UserModel findByUserEmailAndActive(String userEmail, boolean active);

    List<UserModel> findByUserNameContainingIgnoreCaseAndActiveTrue(String userName);

    List<UserModel> findByUserNameStartingWithIgnoreCaseAndActiveTrue(String userName);

    List<UserModel> findByUserNameContainingIgnoreCaseAndActiveFalse(String userName);

    List<UserModel> findByUserNameStartingWithIgnoreCaseAndActiveFalse(String userName);

    UserModel findByOrgId(String ordId);

    List<UserModel> findAllByActiveTrueAndUserEmailNotContaining(String email);
    List<UserModel> findAllByActiveFalseAndUserEmailNotContaining(String email);

    @Query("select s from UserModel a inner join  a.surveyModel s  where a.userId=?1")
    List<SurveyModel> findSurveyByUser(int userId);

    @Query("select a.userName,a.userEmail,a.orgId from UserModel a where a.userId=?1")
    Tuple getUserNameAndUserEmail(int userId);

    @Query("select a.userId from UserModel a where a.userEmail=?1")
    Integer getUserIdByUserEmail(@Param("email") String email);

    @Query("select a.userName,a.orgId from UserModel a where a.userEmail=?1")
    Tuple getUserByEmail(@Param("email") String email);

    @Query("select s.surveyId from UserModel u inner join u.surveyModel s where u.userEmail=?1 and s.templateId=?2 and s.archived=true")
    Integer getSurveyId(@Param("userEmail") String userEmail, @Param("templateId") int templateId);

    @Query("select s from UserModel u inner join u.surveyStatusModels s where u.userEmail=?1 and s.taken=?2")
    List<SurveyStatusModel> getSurveyStatusModel(@Param("email") String email, @Param("taken") boolean taken);

    @Query("select length(u.surveyStatusModels) from UserModel u inner join u.surveyStatusModels s where u.useremail=?1 and s.Taken=?2")
    Integer getSurveyStatusModelSize(@Param("email") String email, @Param("taken") boolean taken);

    @Query("select size(u.surveyStatusModels) from UserModel u  where u.userEmail=?1")
    Integer getSurveyStatusModelByEmailSize(@Param("email") String email);

    @Query("select u.surveyStatusModels from UserModel u where u.userEmail=?1")
    List<SurveyStatusModel> getSurveyStatusModelByEmail(@Param("email") String email);

    @Query("select u.roleId from UserModel u where u.userId=?1")
    int getRoleIdByUserId(@Param("userId") int userId);

    @Query("select u.roleId from UserModel u where u.userEmail=?1")
    int getRoleIdByUserEmail(@Param("userEmail") String userEmail);

    @Query("select s from UserModel u inner join u.surveyModel s where s.surveyName=?1 and u.userEmail=?2")
    SurveyModel getSurveyBySurveyNameAndCreatorEmail(@Param("surveyName") String surveyName, @Param("userEmail") String userEmail);

    @Query("select u.userName,u.userEmail from UserModel u inner join u.surveyModel s where s.surveyId=?1")
    Object[] getUserNameAndEmailBySurveyId(@Param("surveyId") int surveyId);

    @Query("select u.userName as userName,u.userEmail as userEmail,s.surveyName as surveyName,s.creationDate as creationDate" +
            ",size(s.questionModel) as noOfQuestion,ss.responseDate as responseDate from UserModel u inner join" +
            " u.surveyModel s inner join s.surveyResponseModel sss  where s.surveyId=?1 and u.userId=?2 and ss.userId=?4")
    SurveyTakenDto getSurveyInfo(@Param("surveyId") int surveyId, @Param("assignedBy") int assignedBy, @Param("userId") int userId);


    @Query("select s.surveyId as surveyId,s.surveyDesc as surveyDesc from UserModel u inner join " +
            " u.surveyModel s where s.surveyName=?1 and u.userEmail=?2 and s.archived=?3")
    SurveyByNameAndCreatorId getSurveyDataByNameAndId(@Param("surveyName") String surveyName, @Param("userEmail") String userEmail, @Param("archived") boolean archived);

    @Query("select ss from UserModel u inner join u.surveyStatusModels ss where ss.surveyId=?1 and ss.userId=?2 and ss.teamId=?3")
    SurveyStatusModel findBySurveyIdAndUserIdAndTeamId(@Param("surveyId") int surveyId, @Param("userId") int userId, @Param("teamId") int teamId);

    @Query("select t.teamId from UserModel u inner join u.teamModels t where t.teamName=?1")
    int getTeamId(@Param("teamName") String teamName);

    @Query("select t.teamName from UserModel u inner join u.teamModels t where t.teamId=?1")
    String getTeamNameByTeamId(@Param("teamId") int teamId);

    @Query("select s.surveyId from UserModel u inner join u.surveyModel s inner join u.templateModel t where t.templateName=?1")
    List<Integer> getSurveyIdByName(@Param("templateName") String templateName);

    @Query("select t.templateId from UserModel u inner join u.templateModel t where t.templateName=?1")
    Integer getTemplateIdByName(@Param("templateName") String templateName);

    @Query("select s.creationDate,s.expirationDate,s.surveyName,s.surveyDesc from UserModel u inner join  u.surveyModel s where s.surveyId=?1")
    Object [] getSurveyData(@Param("surveyId") int surveyId);

    @Query("select s.creationDate from UserModel u inner join u.surveyModel s where s.surveyId=?1")
    Long getCreationDateBySurveyId(@Param("surveyId") int surveyId);

    @Query("select t.quesText from UserModel u inner join u.templateModel tt inner join tt.templateQuestionModel t where t.templateId=?1")
    List<String> findAllByTemplateId(int tempId);

    @Query("select s.userId as userId,s.teamId as teamId from UserModel u inner  join u.surveyStatusModels s where s.surveyId=?1")
    List<SurveyStatusDto> getSurveyById(@Param("surveyId") int surveyId);




}
