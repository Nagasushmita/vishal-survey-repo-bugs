package io.springboot.survey.repository;

import io.springboot.survey.models.PrivilegesModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PrivilegesRepo extends PagingAndSortingRepository<PrivilegesModel,Integer> {

    @NotNull List<PrivilegesModel>  findAll();
    PrivilegesModel findByRoleId(int roleId);
}
