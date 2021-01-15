package io.springboot.survey.repository;

import io.springboot.survey.models.TemplateQuestionModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface TemplateQuestionRepo extends PagingAndSortingRepository<TemplateQuestionModel,Integer> {

    @Query(value = "select COUNT(t.quesId) from TemplateQuestionModel t where t.templateId=:tempId")
    Integer findByTemplateIdSize(@Param(value = "tempId") int tempId);

    TemplateQuestionModel findByTemplateIdAndQuesText(int id, String ques);

    @Query("select t.quesText from TemplateQuestionModel  t where t.templateId=?1")
    List<String> findAllByTemplateId(int tempId);

    @Query("select t from TemplateQuestionModel t inner join t.answerModel a where t.templateId=?1")
    List<Object[]> getQuestionModelByNameTest(@Param("templateId") int templateId);



}
