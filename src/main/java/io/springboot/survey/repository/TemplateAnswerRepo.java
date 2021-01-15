package io.springboot.survey.repository;

import io.springboot.survey.models.TemplateAnswerModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface TemplateAnswerRepo extends PagingAndSortingRepository<TemplateAnswerModel, Integer> {

    List<TemplateAnswerModel> findByQuesId(int quesId);

    @Query("select a.ansText from TemplateAnswerModel a where a.quesId=?1")
    List<String> getAnswerByQuesId(@Param("quesId") int quesId);


}
