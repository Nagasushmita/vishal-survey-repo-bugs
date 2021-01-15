package io.springboot.survey.repository;

import io.springboot.survey.models.TeamMemberModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface TeamMemberRepo extends PagingAndSortingRepository<TeamMemberModel, Integer> {

    List<TeamMemberModel> findByTeamId(int teamId);

    long deleteByUserIdAndTeamId(int userId, int teamId);

    @Query("select t.teamId from TeamMemberModel t where t.userId=?1")
    List<Integer> findByUserId(@Param("userId") int userId);

    TeamMemberModel findByTeamIdAndAndUserId(int teamId, int userId);

    @Query("select COUNT(t) from TeamMemberModel t where t.teamId=?1")
    Integer getCountByTeamId(@Param("teamId") int teamId);












}
