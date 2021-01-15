package io.springboot.survey.repository;

import io.springboot.survey.models.ResponseModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ResponseRepo extends PagingAndSortingRepository<ResponseModel,Integer> {

    @Query("select AVG(r) from ResponseModel r where r.answerId=?1")
    Integer findByAnswerId(@Param("answerId") int answerId);

    @Query("select COUNT(r) from ResponseModel r where r.answerId=?1 and r.responseId IN (?2)")
    Integer findByAnswerIdAndResponseIdIsIn(@Param("answerId") int answerId, @Param("responseId") List<Integer> responseId);

    @Query("select COUNT(r) from ResponseModel r where  r.answerId IN (?1)")
    Integer findByAnswerIdIsIn(@Param("answerId") List<Integer> answerId);

    @Query("select COUNT(r) from ResponseModel r where r.answerId IN (?1) and r.responseId IN (?2)")
    Integer findByAnswerIdIsInAndResponseIdIsIn(@Param("answerId") List<Integer> answerId, @Param("responseId") List<Integer> responseId);

    @Query("select r.textAnswer from ResponseModel r where r.responseId=?2 and r.quesId=?1")
    String getTextAnswerByResponseIdAndQuesId(@Param("responseId") int responseId, @Param("quesId") int quesId);

    @Query("select r.fileId from ResponseModel r where r.responseId=?1 and r.quesId=?2")
    String getFileByResponseIdAndQuesId(@Param("responseId") int responseId, @Param("quesId") int quesId);


}

