package io.springboot.survey.repository;
import io.springboot.survey.models.SurveyModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

@Repository
@Transactional
public interface SurveyRepo extends PagingAndSortingRepository<SurveyModel, Integer> {

    void deleteSurveyModelBySurveyNameAndCreatorUserIdAndCreationDate(String surveyName, int creatorUserId, long creationDate);

    SurveyModel findBySurveyNameAndCreatorUserId(String name, int id);

    SurveyModel findBySurveyId(int surveyId);

    List<SurveyModel>findAll();

    Page<SurveyModel> findByCreatorUserIdAndArchived(Integer userId, Pageable pageable, boolean archived);

    List<SurveyModel>findAllByCreatorUserIdAndAndCreationDateBetween(int userId, long startDate, long endDate);

    List<SurveyModel>findAllByCreatorUserIdAndAndExpirationDateBetweenAndArchivedFalse(int userId, long startDate,
                                                                                       long endDate);

    SurveyModel findBySurveyNameAndCreatorUserIdAndArchivedFalse(String name, int id);

    SurveyModel findBySurveyNameAndCreatorUserIdAndCreationDateAndArchivedFalse(String name, int id,
                                                                                long creationDate);

    SurveyModel findBySurveyNameAndCreatorUserIdAndCreationDateAndArchivedTrue(String name, int id,
                                                                               long creationDate);
    SurveyModel findByCreatorUserIdAndAndTemplateIdAndArchivedFalse(int userId, int templateId);

    @Query("select COUNT (s) from SurveyModel s")
    Integer getSize();

    @Query("select p.surveyId from SurveyModel p where p.templateId= :tempId")
    List<Integer> findSurveysByTemplateId(@Param("tempId") int tempId);

    @Query("select p from SurveyModel p where p.templateId=?1")
    List<SurveyModel> findSurveyModelByTemplateId(@Param("tempId") int tempId);

    @Query("select p.surveyId from SurveyModel p where p.creatorUserId= ?1 and p.archived=?2")
    List<Integer> getSurveyIdByCreatorId(@Param("creatoruserId") int userId, @Param("archived") boolean archived);

    @Query("select COUNT(p) from SurveyModel p where p.templateId= :tempId")
    Integer getSurveyCount(@Param("tempId") int tempId);

    @Query("select u from SurveyModel u where u.creationDate between ?1 and ?2 ")
    List<SurveyModel>findAllByCreationDateBetween(@Param("startDate") long startDate, @Param("endDate") long endDate);

    @Query("select COUNT (u) from SurveyModel u where u.creationDate between ?1 and ?2 ")
    Integer getSizeByCreationDateBetween(@Param("startDate") long startDate, @Param("endDate") long endDate);

    @Query("select s.creatorUserId from SurveyModel s where s.surveyId=?1")
    int getCreatorById(@Param("surveyId") int surveyId);

    @Query("select COUNT(s) from SurveyModel s where s.creatorUserId=?1 and s.archived=?2")
    Integer getCountByCreatedAndArchived(@Param("creatorUserId") int userId, @Param("archived") boolean archived);

    @Query("select s.surveyId,s.link from SurveyModel s where s.surveyName=?1 and s.creatorUserId=?2")
    Object[] getSurveyIdAndLink(@Param("surveyName") String surveyName, @Param("creatorUserId") int creatorUserId);

    @Query("select COUNT(u) from SurveyModel u where u.creatorUserId=?1 and u.creationDate between ?2 and ?3 ")
    Integer getCountByCreatorUserIdAndCreationDate(@Param("creatorUserId") int creatorUserId, @Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);

    @Query("select u.templateId from SurveyModel u where u.creatorUserId=?1 and u.archived=?2 order  by u.creationDate")
    List<Integer> getTemplateIdByCreatorAndArchived(@Param("creatorUserId") int creatorUserId, @Param("archived") boolean archived);

    @Query("select DISTINCT(s.templateId) from SurveyModel s where s.archived=?1")
    List<Integer> getTemplateIdByArchived(@Param("archived") boolean archived);

    @Query("select count(p) from SurveyModel p where p.templateId= ?1")
    Integer getCountByTemplateId(@Param("templateId") int templateId);

    @Query("select count(s.responseId) from SurveyModel p self join p.surveyResponseModel s where p.templateId=?1")
    Integer getResponseCountByTemplateId(@Param("TemplateId") int templateId);
}
