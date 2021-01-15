package io.springboot.survey.repository;

import io.springboot.survey.models.AnswerModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface AnswerRepo extends PagingAndSortingRepository<AnswerModel, Integer> {

    List<AnswerModel> findByQuesId(int quesId);

    @Query("select a.ansId from AnswerModel a where a.ansText=?1")
    List<Integer> findByAnsText(@Param("ansText") String ansText);

    @Query("select a.ansText from AnswerModel a left outer join join a.responseModel r where r.quesId=?1 and r.responseId=?2")
    List<String> getAnswersByResponseModel(@Param("quesId") int quesId, @Param("responseId") int responseId);



}
