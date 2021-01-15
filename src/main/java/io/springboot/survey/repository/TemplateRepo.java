package io.springboot.survey.repository;
import io.springboot.survey.mapper.GetTemplate;
import io.springboot.survey.mapper.SurveyModelDto;
import io.springboot.survey.models.TemplateModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface TemplateRepo extends PagingAndSortingRepository<TemplateModel, Integer> {

    long deleteTemplateModelByTemplateName(String name);
    TemplateModel findByTemplateName(String name);
    TemplateModel findByTemplateId(int tempId);
    Page<TemplateModel> findByIsArchivedFalse(Pageable pageable);
    List<TemplateModel> findByIsArchivedFalse();
    Page<TemplateModel> findByCreatorUserIdAndIsArchivedFalse(int creatorId, Pageable pageable);
    List<TemplateModel> findByCreatorUserIdAndIsArchivedFalse(int creatorId);
    Page<TemplateModel> findByCreatorUserIdAndIsArchivedTrue(int creatorId, Pageable pageable);

    @Query("select count(t) from TemplateModel t where t.creatorUserId=?1 and t.isArchived=?2")
    Integer findByCreatorUserIdSize(@Param("creatorUserId") int creatorUserId, @Param("isArchived") boolean isArchived);

    @Query("select q.quesId as quesId,q.quesText as quesText,q.mandatory as mandatory,q.quesTypeId as typeId from TemplateModel t inner join t.templateQuestionModel q  where t.templateName=?1")
    List<GetTemplate> getQuestionModelByName(@Param("templateName") String templateName);

    @Query("select p.surveyName as surveyName,p.creatorUserId as creatorUserId ,length(p.surveyStatusModel) as size" +
            " from TemplateModel t inner join t.surveyModels p where t.templateName=?1")
    List<SurveyModelDto> getSurveyModelByName(@Param("templateName") String templateName);

    @Query("select t.templateDesc,t.creationDate,size(t.templateQuestionModel)  from TemplateModel t  where t.templateName=?1")
    Object [] getDescAndCreationDate(@Param("templateName") String templateName);

    @Query("select t.templateName from TemplateModel t  where t.templateId=?1")
    String getTemplateNameById(@Param("templateId") int templateId);


}