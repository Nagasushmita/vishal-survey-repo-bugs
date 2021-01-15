package io.springboot.survey.service;


import io.springboot.survey.models.RoleModel;
import io.springboot.survey.response.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleService {

     List<RoleModel> getAllRoles();
     ResponseEntity<ResponseMessage> addRole(String roleName, String email);
    ResponseEntity<Void> deleteRole(String roleName);

}
