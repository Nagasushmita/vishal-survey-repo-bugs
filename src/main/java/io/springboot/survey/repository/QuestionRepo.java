package io.springboot.survey.repository;
import io.springboot.survey.models.QuestionModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface QuestionRepo extends PagingAndSortingRepository<QuestionModel, Integer> {

    @Query("select q.quesTypeId,q.quesText from QuestionModel q  where q.quesId=?1")
    Tuple findByQuesId(@Param("quesId") int quesId);

    @Query("select q.quesId, q.quesTypeId from QuestionModel q  where q.quesText=?1 and q.surveyId=?2")
    int findByQuesTextAndSurveyId(@Param("quesText") String quesText, @Param("surveyId") int surveyId);

    List<QuestionModel> findAllBySurveyId(int surveyId);

    @Query("select q.quesId from QuestionModel q  where q.surveyId=?1")
    List<Integer> getQuestIdBySurveyId(@Param("surveyId") int surveyId);

    @Query("select COUNT(q.quesId) from QuestionModel q  where q.surveyId=?1")
    Integer getSizeBySurveyId(@Param("surveyId") int surveyId);

    @Query("select a.ansId from QuestionModel q inner join q.answerModel a where q.quesText=?1 and q.surveyId=?2")
    List<Integer> getAnsByQuesTextAndSurveyId(@Param("quesText") String quesText, @Param("surveyId") int surveyId);

    @Query("select q.mandatory from QuestionModel q where q.quesText=?1 and q.surveyId=?2")
    Boolean getMandatoryByQuesTextAndSurveyId(@Param("quesText") String quesText, @Param("surveyId") int surveyId);

    @Query("select q.quesId from QuestionModel q where q.surveyId=?1 and q.quesText=?2")
    int getIdBySurveyIdAndQuesText(@Param("surveyId") int surveyId, @Param("quesText") String quesText);

    @Query("select size(q.answerModel) from QuestionModel q where q.surveyId=?1 and q.quesText=?2")
    Integer getAnswerCountById(@Param("surveyId") int surveyId, @Param("quesText") String quesText);

    @Query("select a.ansId from QuestionModel q inner join q.answerModel a where a.quesId=?1 and a.ansText=?2")
    int getIdByQuesIdAndAnsText(@Param("quesId") int quesId, @Param("ansText") String ansText);

    @Query("select a.ansId from QuestionModel q inner join q.answerModel a where a.quesId=?1")
    List<String> getAnsIdByQuesId(@Param("quesId") int quesId);

    @Query("select a.ansText from QuestionModel q inner join q.answerModel a where a.ansId=?1")
    String findByAnsId(@Param("ansId") int ansId);

}
