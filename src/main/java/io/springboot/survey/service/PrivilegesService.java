package io.springboot.survey.service;

import io.springboot.survey.models.PrivilegesModel;
import io.springboot.survey.request.PrivilegesRequest;
import io.springboot.survey.response.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PrivilegesService {
        ResponseEntity<ResponseMessage> mapPrivileges(PrivilegesRequest privilegesRequest);
        List<PrivilegesRequest> showAllPrivileges();
        PrivilegesRequest showPrivileges(String roleName);
         List<String> getPrivileges(PrivilegesModel privilegesModel);
}
