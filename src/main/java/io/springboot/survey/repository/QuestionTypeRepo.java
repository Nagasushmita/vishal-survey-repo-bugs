package io.springboot.survey.repository;

import io.springboot.survey.models.QuestionTypeModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface QuestionTypeRepo extends PagingAndSortingRepository<QuestionTypeModel, Integer> {

    @Query("SELECT q.quesTypeName from QuestionTypeModel q where q.quesTypeId=?1")
    String getQuestionNameByTypeId(@Param("typeId") Integer typeId);

    @Query("SELECT q.quesTypeId from QuestionTypeModel q where q.quesTypeName=?1")
    int getIdByQuestionName(@Param("quesType") String quesType);

    @Query("SELECT qt.quesTypeName from QuestionTypeModel qt inner join qt.questionModel q  where q.quesText=?1 and q.surveyId=?2")
    String getQuestTypeNameByQuestion(@Param("quesText") String quesText, @Param("surveyId") int surveyId);

    @Query("SELECT qt.quesTypeName from QuestionTypeModel qt inner join qt.templateQuestionModel q  where q.quesText=?1 and q.templateId=?2")
    String getQuestTypeNameByTemplateQuestion(@Param("quesText") String quesText, @Param("templateId") int templateId);

}
