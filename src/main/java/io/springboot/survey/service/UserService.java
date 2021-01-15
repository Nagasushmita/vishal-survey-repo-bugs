package io.springboot.survey.service;

import io.springboot.survey.models.UserModel;
import io.springboot.survey.pojo.GetRequestParam;
import io.springboot.survey.pojo.user.DynamicSearchParam;
import io.springboot.survey.pojo.user.GetAllParam;
import io.springboot.survey.request.AddUserRequest;
import io.springboot.survey.request.ModifyUserRequest;
import io.springboot.survey.request.OtpRequest;
import io.springboot.survey.request.UpdateUsersRequest;
import io.springboot.survey.response.AuthenticationResponse;
import io.springboot.survey.response.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;


@Service
public interface UserService {

     ResponseEntity<ResponseMessage> sendEmail(String email);
     ResponseEntity<AuthenticationResponse> userAuthentication(OtpRequest otp);
     ResponseEntity<AuthenticationResponse> googleLogin(String email);
     ResponseEntity<ResponseMessage> addUser(AddUserRequest addUserRequest);
     ResponseEntity<Void> deleteUsers(ModifyUserRequest modifyUserRequest, String email);
     ResponseEntity<ResponseMessage> softDeleteUser(ModifyUserRequest modifyUserRequest);
     ResponseEntity<ResponseMessage> updateRole(UpdateUsersRequest updateUsersRequest);
     List<HashMap<String, String>> viewUser(GetRequestParam getRequestParam);
     ResponseEntity<ResponseMessage> importUsers(AddUserRequest addUserRequest);
     List<HashMap<String, String>> dynamicSearch(DynamicSearchParam dynamicSearchParam);
     List<UserModel> getAllUserByRole(String role, String email);
     ResponseEntity<ResponseMessage> enableUser(ModifyUserRequest modifyUserRequest);
     List<HashMap<String, String>> viewDisabledUser(GetRequestParam getRequestParam);
     MappingJacksonValue getAll(GetAllParam getAllParam);
     Object getFilterInfo();
}
