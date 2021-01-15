package io.springboot.survey.repository;

import io.springboot.survey.models.RoleModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface RoleRepo extends PagingAndSortingRepository<RoleModel,Integer> {

    long deleteByRole(String role);

    RoleModel findByRole(String role);

    List<RoleModel>  findAll();

    @Query("select r.roleId from RoleModel r where r.role=?1")
    Integer getRoleIdByRole(@Param("role") String role);

    @Query("select r.role from RoleModel r where r.roleId=?1")
    String getRoleByRoleId(@Param("roleId") int roleId);
}
