package io.springboot.survey.repository;

import io.springboot.survey.models.TeamModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface TeamRepo extends PagingAndSortingRepository<TeamModel, Integer>, JpaSpecificationExecutor<TeamModel> {
    TeamModel findByTeamName(String teamName);
    TeamModel findByTeamNameAndUserId(String teamName, int userId);
    long deleteByTeamNameAndUserId(String name, int id);
    List<TeamModel> findByUserId(int userId);
    List<TeamModel> findByManagerId(int managerId);
    TeamModel findByTeamId(int teamId);
    List<TeamModel> findByProjectNameContaining(String projectName);

    @NotNull List<TeamModel> findAll();

    @Query("SELECT t.teamId from TeamModel t where t.teamName=?1 and t.userId=?2")
    Integer getTeamIdByNameAndId(@Param("teamName") String teamName, @Param("userId") int userId);

    @Query("select t.userId from TeamModel t where t.teamName=?1")
    int getCreatorUserId(@Param("teamName") String teamName);

    @Query("select t.teamId from TeamModel t where t.teamName=?1")
    int getTeamId(@Param("teamName") String teamName);

    @Query("select t.managerId from TeamModel t where t.teamName=?1")
    int getManagerUserId(@Param("teamName") String teamName);

    @Query("SELECT t.teamName from TeamModel t where t.teamId=?1")
    String getTeamNameByTeamId(@Param("teamId") int teamId);

    @Query("select tm.userId from TeamModel t inner join t.teamMemberModels tm where t.teamName=?1")
    List<Integer> getTeamMemberByTeamName(@Param("teamName") String teamName);

    @Query("SELECT COUNT(t)from TeamModel t where t.managerId=?1")
    Integer getSizeByManagerId(@Param("managerId") int managerId);

}
