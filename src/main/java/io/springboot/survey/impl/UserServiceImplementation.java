package io.springboot.survey.impl;

import io.springboot.survey.exception.APIException;
import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.pojo.GetRequestParam;
import io.springboot.survey.pojo.user.DynamicSearchParam;
import io.springboot.survey.pojo.user.GetAllParam;
import io.springboot.survey.repository.RoleRepo;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.request.AddUserRequest;
import io.springboot.survey.request.ModifyUserRequest;
import io.springboot.survey.request.OtpRequest;
import io.springboot.survey.request.UpdateUsersRequest;
import io.springboot.survey.response.AuthenticationResponse;
import io.springboot.survey.response.EmployeeDetails;
import io.springboot.survey.response.PaginationResponse;
import io.springboot.survey.response.ResponseMessage;
import io.springboot.survey.service.JwtUtil;
import io.springboot.survey.service.NotificationService;
import io.springboot.survey.service.UserService;
import io.springboot.survey.specification.SpecificationService;
import io.springboot.survey.utils.DynamicFiltering;
import io.springboot.survey.utils.Pagination;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static io.springboot.survey.utils.Constants.CommonConstant.*;
import static io.springboot.survey.utils.Constants.ErrorMessageConstant.*;
import static io.springboot.survey.utils.Constants.FilterConstants.PAGINATION_FILTER;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.ValidationConstant.*;

@Component
public class UserServiceImplementation implements UserService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final JwtUtil jwtUtil;
    private final NotificationService notificationService;
    private UserModel userModel;
    private final SpecificationService specificationService;

    private static final Logger logger= LoggerFactory.getLogger(UserServiceImplementation.class.getSimpleName());

    public UserServiceImplementation(UserRepo userRepo, RoleRepo roleRepo, JwtUtil jwtUtil, NotificationService notificationService, SpecificationService specificationService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.jwtUtil = jwtUtil;
        this.notificationService = notificationService;
        this.specificationService = specificationService;
    }


    /**
     * Return list of active/inactive users
     * @param param:GetRequestParam
     * @param isActive : Boolean field which indicates if the user is active or not.
     * @return List<UserModel>
     */
    private List<UserModel> findAllUser(GetRequestParam param,boolean isActive) {
        logger.info(STARTING_METHOD_EXECUTION);
        if (param.getPage() < 0)
            param.setPage(0);
        PageRequest paging = PageRequest.of(param.getPage(), param.getPageSize(), Sort.by(param.getSortBy()));
        logger.info(EXITING_METHOD_EXECUTION);
        return userRepo.findAllByUserEmailNotContainingAndActive(param.getEmail(), paging, isActive).getContent();
    }

    /**
     * Pagination for find employees
     *
     * @param list :  List<UserModel>
     * @param totalEmp :  List<UserModel>
     * @param pageSize : number of object per page.
     * @return : List<HashMap<String, String>>
     */
    private List<HashMap<String, String>> findEmployees(@NotNull List<UserModel> list, List<UserModel> totalEmp, int pageSize) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<HashMap<String, String>> allEmp = new ArrayList<>();
        for (UserModel model : list) {
            HashMap<String, String> empList = new HashMap<>();
            int d = ((totalEmp.size() - 1) % pageSize);
            int q = ((totalEmp.size() - 1) / pageSize);
            getDetails(model, empList);
            empList.put(PAGE_REQUIRED, String.valueOf(((d == 0) ? q : q + 1)));
            allEmp.add(empList);
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return allEmp;
    }

    /**
     * Details of the users
     *
     * @param model :UserModel
     * @param empList : HashMap<String, String>
     */
    private void getDetails(@NotNull UserModel model, @NotNull HashMap<String, String> empList) {
        logger.info(STARTING_METHOD_EXECUTION);
        empList.put(USER_NAME, model.getUserName());
        empList.put(USER_EMAIL, model.getUserEmail());
        empList.put(ORG_ID, model.getOrgId());
        empList.put(ROLE_NAME, roleRepo.getRoleByRoleId(model.getRoleId()));
        empList.put(GENDER, model.getGender());
        empList.put(DESIGNATION, model.getDesignation());
        logger.info(EXITING_METHOD_EXECUTION);
    }


    /**
     * Checking if the user is present in the database and sending otp if present.

     * @param email : email to which the otp is to be sent.
     * @return ResponseEntity<ResponseMessage>
     */
    @Override
    public ResponseEntity<ResponseMessage> sendEmail(String email) {
        logger.info("Starting method execution");
        ResponseMessage responseMessage = new ResponseMessage();
        if ((userRepo.findByUserEmailAndActive(email, true) != null)) {
            userModel = userRepo.findByUserEmail(email);
            try {
                notificationService.sendNotification(email);
            } catch (Exception e) {
                logger.error(ERROR_SENDING_EMAIL,e);
                throw new APIException(ERROR_SENDING_EMAIL);
            }
            responseMessage.setMessage(USER_PRESENT);
            responseMessage.setStatusCode(HttpStatus.OK.value());
            logger.info(EXITING_METHOD_EXECUTION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);

        } else {
            responseMessage.setMessage(USER_NOT_PRESENT);
            responseMessage.setStatusCode(HttpStatus.NOT_FOUND.value());
            logger.debug("user with email {} not found",email);
            logger.info(EXITING_METHOD_EXECUTION);
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }
    /**
     * Checking the otp if matched then generate jwt token for the user
     *
     * @param otp : otp sent to the email of user.
     * @return ResponseEntity<AuthenticationResponse>
     */
    @Override
    public ResponseEntity<AuthenticationResponse> userAuthentication(@NotNull OtpRequest otp) {
        logger.info(STARTING_METHOD_EXECUTION);
        String message = otp.getOtpValue();
        String s = notificationService.getOtp();
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        if (s.equals(message)) {
            authenticationResponse.setResponse(OTP_VALID);
            authenticationResponse.setStatus(SUCCESS);
            authenticationResponse.setName(userModel.getUserName());
            authenticationResponse.setRole(roleRepo.getRoleByRoleId(userModel.getRoleId()));
            authenticationResponse.setJwt(jwtGeneration(userModel.getUserName()));
            logger.info(EXITING_METHOD_EXECUTION);
            return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);

        } else {
            authenticationResponse.setStatus(HttpStatus.BAD_REQUEST.toString());
            authenticationResponse.setResponse(OTP_INVALID);
            logger.debug("Invalid otp");
            logger.info(EXITING_METHOD_EXECUTION);
            return new ResponseEntity<>(authenticationResponse, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Generate jwt token for user
     *
     * @param userName : userName
     * @return : String
     */
    private String jwtGeneration(String userName) {
        logger.info(STARTING_METHOD_EXECUTION);
        UserModel userDetails = userRepo.findByUserName(userName);
        logger.info(EXITING_METHOD_EXECUTION);
        return jwtUtil.generateToken(userDetails);
    }

    /**
     * Generate the jwt if a user is authenticated via google sign in
     *
     * @param email : email for which jwt token is to be generated.
     * @return ResponseEntity<AuthenticationResponse> containing jwt token and other basic
     * information about the user
     */

    @Override
    public ResponseEntity<AuthenticationResponse> googleLogin(String email) {
        logger.info(STARTING_METHOD_EXECUTION);
        AuthenticationResponse authenticationResponse;
        try {
            UserModel ob = userRepo.findByUserEmail(email);
            authenticationResponse = new AuthenticationResponse();
            String userName = ob.getUserName();
            authenticationResponse.setName(userName);
            authenticationResponse.setRole((roleRepo.getRoleByRoleId(ob.getRoleId())));
            String jwt = jwtGeneration(userName);
            authenticationResponse.setJwt(jwt);
            authenticationResponse.setStatus(String.valueOf(HttpStatus.OK));
            authenticationResponse.setResponse(LOGIN_SUCCESS);
        } catch (Exception e) {
            logger.error(ERROR_GOOGLE_LOGIN,e);
            throw new APIException(ERROR_GOOGLE_LOGIN);
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
    }

    /**
     * For Adding a new user in the central database
     *
     * @param addUserRequest : AddUserRequest.
     * @return : ResponseEntity<ResponseMessage>
     */
    @Override
    public ResponseEntity<ResponseMessage> addUser(@NotNull AddUserRequest addUserRequest) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<EmployeeDetails> list = addUserRequest.getEmployeeDetailsList();
        List<String> notAdded = new ArrayList<>();
        List<String> notAddedId = new ArrayList<>();
        try {
            for (EmployeeDetails model : list) {
                if (userRepo.findByUserEmail(model.getEmail()) == null) {
                    if (userRepo.findByOrgId(model.getEmpId()) == null) {
                        addNewUserHelperFunction(model, addUserRequest);
                    } else
                        notAddedId.add(model.getEmpId());
                } else
                    notAdded.add(model.getEmail());
            }
            logger.info(EXITING_METHOD_EXECUTION);
            return addUserCondition(notAdded, notAddedId);
        }
        catch (Exception ex)
        {
            logger.error(ERROR_ADDING_USER,ex);
            throw  new APIException(ERROR_ADDING_USER);
        }
    }

    /**
     * AddUserHelperFunction
     *
     * @param model :EmployeeDetails
     * @param addUserRequest: AddUserRequest
     */
    private void addNewUserHelperFunction(@NotNull EmployeeDetails model, @NotNull AddUserRequest addUserRequest) {
        logger.info(STARTING_METHOD_EXECUTION);
        UserModel ob = new UserModel();
        ob.setUserName(model.getName());
        ob.setRoleId(roleRepo.getRoleIdByRole(addUserRequest.getRoleName()));
        ob.setOrgId(model.getEmpId());
        ob.setUserEmail(model.getEmail());
        ob.setGender(model.getGender());
        ob.setDesignation(model.getDesignation());
        ob.setActive(true);
        ob.setCreatedOn(System.currentTimeMillis());
        userRepo.save(ob);
        logger.debug("User saved : {}",ob);
        logger.info(EXITING_METHOD_EXECUTION);
    }

    /**
     * AddUserCondition
     *
     * @param notAdded : email of not added user.
     * @param notAddedId: id of not added user.
     * @return : ResponseEntity<ResponseMessage>
     */
  private  ResponseEntity<ResponseMessage> addUserCondition(@NotNull List<String> notAdded, List<String> notAddedId) {
      logger.info(STARTING_METHOD_EXECUTION);
        ResponseMessage responseMessage = new ResponseMessage();
        if (notAdded.isEmpty() && notAddedId.isEmpty()) {
            responseMessage.setMessage(SUCCESS);
            responseMessage.setStatusCode(HttpStatus.CREATED.value());
            logger.info(EXITING_METHOD_EXECUTION);
            return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
        } else if (!notAddedId.isEmpty()) {
            String listString = String.join(", ", notAddedId);
            responseMessage.setMessage(ID_RESPONSE + listString + ALREADY_EXIST);
            responseMessage.setStatusCode(HttpStatus.CONFLICT.value());
            logger.debug("User with Ids {} already exits",listString);
            logger.info(EXITING_METHOD_EXECUTION);
            return new ResponseEntity<>(responseMessage, HttpStatus.CONFLICT);
        } else {
            String listString = String.join(", ", notAdded);
            responseMessage.setMessage(EMAIL_RESPONSE + listString + ALREADY_EXIST);
            responseMessage.setStatusCode(HttpStatus.CONFLICT.value());
            logger.debug("User with emails {} already exits",listString);
            logger.info(EXITING_METHOD_EXECUTION);
            return new ResponseEntity<>(responseMessage, HttpStatus.CONFLICT);
        }

    }

    /**
     * For deleting an user from the central database
     *
     * @param modifyUserRequest :ModifyUserRequest.
     * @param email : email of the logged in user.
     * @return : ResponseEntity<Void> --> 204 No Content.
     */
    @Override
    public ResponseEntity<Void> deleteUsers(@NotNull ModifyUserRequest modifyUserRequest, String email) {
        logger.info(STARTING_METHOD_EXECUTION);
        try {
           List<String> list = new ArrayList<>();
           List<String> emails = modifyUserRequest.getListOfMails();
           for (String emailId : emails) {
               if (emailId.equals(email)) {
                   list.add(emailId);
                   continue;
               }
               long d = userRepo.deleteByUserEmail(emailId);
               if (d != 1)
                   list.add(emailId);
           }
           if (!list.isEmpty()) {
               logger.debug("User with email(s) {} not deleted",list);
               logger.info(EXITING_METHOD_EXECUTION);
               throw new APIException(USER_WITH_EMAILS + list.toString() + NOT_DELETED);
           }
            logger.info(EXITING_METHOD_EXECUTION);
           return ResponseEntity.noContent().build();
       }
       catch (Exception ex)
       {
           logger.error(ERROR_DELETE_USER,ex);
           throw new APIException(ERROR_DELETE_USER);
       }
    }

    /**
     * For soft deleting an user from the central database
     *
     * @param modifyUserRequest :ModifyUserRequest.
     * @return : ResponseEntity<ResponseMessage>
     */
    @Override
    public ResponseEntity<ResponseMessage> softDeleteUser(ModifyUserRequest modifyUserRequest) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<String> invalidEmails = new ArrayList<>();
        ResponseMessage responseMessage = new ResponseMessage();
        for (String email : modifyUserRequest.getListOfMails()) {
            UserModel obj = userRepo.findByUserEmail(email);
            if (obj == null)
                invalidEmails.add(email);
            else {

                obj.setActive(false);
                obj.setUpdatedOn(System.currentTimeMillis());
                userRepo.save(obj);
            }
        }
        if (!invalidEmails.isEmpty()) {
            logger.debug("User with email(s) {} not found",invalidEmails);
            logger.info(EXITING_METHOD_EXECUTION);
            throw new ResourceNotFoundException(USER_WITH_EMAILS + invalidEmails.toString() + NOT_FOUND);
        }
        else {
            responseMessage.setMessage(USER_ARCHIVED);
            responseMessage.setStatusCode(HttpStatus.OK.value());
            logger.info(EXITING_METHOD_EXECUTION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
    }

    /**
     * For updating the role of a particular user
     *
     * @param updateUsersRequest : UpdateUsersRequest.
     * @return :  ResponseEntity<ResponseMessage
     */
    @Override
    public ResponseEntity<ResponseMessage> updateRole(@NotNull UpdateUsersRequest updateUsersRequest) {
        logger.info(STARTING_METHOD_EXECUTION);
        ResponseMessage responseMessage = new ResponseMessage();
        UserModel obj = userRepo.findByUserEmail(updateUsersRequest.getEmail());
        if (!StringUtils.isEmpty(updateUsersRequest.getDesignation()) && !StringUtils.isEmpty(updateUsersRequest.getRoleName())) {
            obj.setRoleId(roleRepo.getRoleIdByRole(updateUsersRequest.getRoleName()));
            obj.setDesignation(updateUsersRequest.getDesignation());
        }
        if (updateUsersRequest.getDesignation()!=null && updateUsersRequest.getRoleName()==null) {
            obj.setDesignation(updateUsersRequest.getDesignation());
            obj.setRoleId(obj.getRoleId());
        }
        if (updateUsersRequest.getDesignation()==null && updateUsersRequest.getRoleName()!=null) {
            obj.setRoleId(roleRepo.getRoleIdByRole(updateUsersRequest.getRoleName()));
        }
        if (updateUsersRequest.getDesignation()==null && updateUsersRequest.getRoleName()==null) {
            throw new APIException(WRONG_INPUT);
        }
        obj.setUpdatedOn(System.currentTimeMillis());
        userRepo.save(obj);
        logger.debug("User updated : {}",obj);
        responseMessage.setMessage(ROLE_UPDATED);
        responseMessage.setStatusCode(HttpStatus.OK.value());
        logger.info(EXITING_METHOD_EXECUTION);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * @param param :GetRequestParam
     * @return :  List<HashMap<String, String>>
     */
    @Override
    public List<HashMap<String, String>> viewUser(GetRequestParam param) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<UserModel> list=findAllUser(new GetRequestParam(param.getEmail(),param.getPage(),param.getPageSize(),param.getSortBy()),true);
        List<UserModel> totalEmp=userRepo.findAllByActiveTrueAndUserEmailNotContaining(param.getEmail());
        logger.info(EXITING_METHOD_EXECUTION);
        return findEmployees(list,totalEmp,param.getPageSize());
    }

    /**
     * For importing users to database in bulk
     *
     * @param addUserRequest : AddUserRequest.
     * @return : ResponseEntity<ResponseMessage>
     */
    @Override
    public ResponseEntity<ResponseMessage> importUsers(@NotNull AddUserRequest addUserRequest) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<EmployeeDetails> list= addUserRequest.getEmployeeDetailsList();
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            if (!list.isEmpty()) {
                List<String> notAdded = new ArrayList<>();
                for (EmployeeDetails model : list) {
                    if (userRepo.findByUserEmail(model.getEmail()) == null) {
                        UserModel ob = new UserModel();
                        ob.setUserName(model.getName());
                        ob.setRoleId(roleRepo.getRoleIdByRole(model.getRole()));
                        ob.setOrgId(model.getEmpId());
                        ob.setUserEmail(model.getEmail());
                        ob.setDesignation(model.getDesignation());
                        ob.setGender(model.getGender());
                        ob.setActive(true);
                        ob.setCreatedOn(System.currentTimeMillis());
                        userRepo.save(ob);
                        logger.debug("User saved : {}",ob);
                    } else {
                        notAdded.add(model.getEmail());
                    }
                }
                logger.info(EXITING_METHOD_EXECUTION);
                return addUserCondition(notAdded, notAdded);
            }
            logger.info(EXITING_METHOD_EXECUTION);
            return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        }
        catch (Exception ex)
        {
            logger.error(ERROR_IMPORTING_USER,ex);
            throw new APIException(ERROR_IMPORTING_USER);
        }
    }
    /**
     * For Searching users in the database by their name
     * @param dynamicSearchParam : DynamicSearchParam
     * @return : List<HashMap<String, String>>
     */
    @Override
    public List<HashMap<String, String>> dynamicSearch(DynamicSearchParam dynamicSearchParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<UserModel> list1;
        List<UserModel>list2;
       if(dynamicSearchParam.isActive()){
           list1 = userRepo.findByUserNameStartingWithIgnoreCaseAndActiveTrue(dynamicSearchParam.getName());
           list2 = userRepo.findByUserNameContainingIgnoreCaseAndActiveTrue(dynamicSearchParam.getName());
       }
        else {
           list1 = userRepo.findByUserNameStartingWithIgnoreCaseAndActiveFalse(dynamicSearchParam.getName());
           list2 = userRepo.findByUserNameContainingIgnoreCaseAndActiveFalse(dynamicSearchParam.getName());
        }

        ArrayList<UserModel> duplicates = new ArrayList<>(list2);

        //Remove all the entries that does not exits in list1
        duplicates.retainAll(list1);

        ArrayList<UserModel> uniques = new ArrayList<>(list2);
        //Remove all the entries that does exits in list1
        uniques.removeAll(list1);
        duplicates.addAll(uniques);
        duplicates.remove(userRepo.findByUserEmail(dynamicSearchParam.getEmail()));
        logger.info(EXITING_METHOD_EXECUTION);
        return findEmployees(duplicates,duplicates,dynamicSearchParam.getPageSize());

    }
    /**
     * Get all the employee by their role
     *
     * @param role : name of the role.
     * @param email : email of the logged in user.
     * @return : List<UserModel>
     */

    @Override
    public List<UserModel> getAllUserByRole(String role, String email) {
        logger.info(STARTING_METHOD_EXECUTION);
        if(!StringUtils.isEmpty(role)) {
            logger.info(EXITING_METHOD_EXECUTION);
            return userRepo.findByRoleId(roleRepo.getRoleIdByRole(role));
        }
        else {
            logger.info(EXITING_METHOD_EXECUTION);
            return userRepo.findAllByActiveTrueAndUserEmailNotContaining(email);
        }
    }

    /**
     * To enable the disabled user again
     *
     * @param modifyUserRequest : ModifyUserRequest.
     * @return : ResponseEntity<ResponseMessage>
     */

    @Override
    public ResponseEntity<ResponseMessage> enableUser(ModifyUserRequest modifyUserRequest) {
        logger.info(STARTING_METHOD_EXECUTION);
        ResponseMessage responseMessage = new ResponseMessage();
        List<String> invalidEmails = new ArrayList<>();
            for (String email : modifyUserRequest.getListOfMails()) {
                UserModel obj = userRepo.findByUserEmail(email);
                if (obj == null)
                    invalidEmails.add(email);
                else {
                    obj.setActive(true);
                    obj.setUpdatedOn(System.currentTimeMillis());
                    userRepo.save(obj);
                }
            }
            if (!invalidEmails.isEmpty()) {
                logger.debug("User(s) with email(s) {} not found",invalidEmails);
                logger.info(EXITING_METHOD_EXECUTION);
                throw new ResourceNotFoundException(USER_WITH_EMAILS + invalidEmails.toString() + NOT_FOUND);
            }
            else {
                responseMessage.setMessage(USER_ACTIVATED);
                responseMessage.setStatusCode(HttpStatus.OK.value());
                logger.info(EXITING_METHOD_EXECUTION);
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            }
    }
    /**
     * Return all the disabled user in the org
     * @param param:GetRequestParam
     * @return : List<HashMap<String, String>>
     */

    @Override
    public List<HashMap<String, String>> viewDisabledUser(GetRequestParam param) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<UserModel> list=findAllUser(new GetRequestParam(param.getEmail(),param.getPage(),param.getPageSize(),param.getSortBy()),false);
        List<UserModel> totalEmp=userRepo.findAllByActiveFalseAndUserEmailNotContaining(param.getEmail());
        logger.info(EXITING_METHOD_EXECUTION);
        return findEmployees(list,totalEmp,param.getPageSize());
    }
    /**
     * Return all the users filtered by gender,role,designation,project and status
     *
     * @param getAllParam:GetAllParam
     * @return : List<HashMap<String, String>>
     */

    @Override
    public MappingJacksonValue getAll(GetAllParam getAllParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<UserModel> list = userRepo.findAll((specificationService.getUserFilter(getAllParam.getEmail(), getAllParam.getUserFilter())));
        logger.info(EXITING_METHOD_EXECUTION);
        return getALlHelperFunction(list,getAllParam.getPage(),getAllParam.getPageSize());
    }

    /**
     *Get all user helper function
     *
     * @param list :  List<UserModel>
     * @param page : current page number
     * @param pageSize : number of object per page
     * @return : List<HashMap<String, String>>
     */
    private MappingJacksonValue getALlHelperFunction(@NotNull List<UserModel> list, int page, int pageSize)
    {
        logger.info(STARTING_METHOD_EXECUTION);
        List<HashMap<String, String>> allEmp = new ArrayList<>();
        for (UserModel model : list) {
            HashMap<String, String> empList = new HashMap<>();
            getDetails(model, empList);
            allEmp.add(empList);
        }
        Pagination pagination = new Pagination();
        PaginationResponse  paginationResponse=new PaginationResponse();
        int d= ((allEmp.size())%pageSize);
        int q=((allEmp.size())/pageSize);
        paginationResponse.setHashMapList(pagination.surveyPagination(allEmp,page,pageSize));
        paginationResponse.setPageRequired(((d == 0) ? q : q+1));
        logger.info(EXITING_METHOD_EXECUTION);
      return getDynamicFilteredObject(paginationResponse);

    }

    /**
     * @param paginationResponse : PaginationResponse
     * @return : MappingJacksonValue
     */
    private MappingJacksonValue getDynamicFilteredObject(PaginationResponse paginationResponse)
    {
        logger.info(STARTING_METHOD_EXECUTION);
        Set<String> filter = new HashSet<>(Arrays.asList(HASH_MAP_LIST,PAGE_REQUIRED));
        DynamicFiltering dynamicFiltering = new DynamicFiltering();
        logger.info(EXITING_METHOD_EXECUTION);
        return dynamicFiltering.dynamicObjectFiltering(paginationResponse,filter,PAGINATION_FILTER);

    }

    /**
     * Return  information about the filter that are used while assigning the io.springboot.survey to the users
     *
     * @return : Object
     */
    @Override
    public Object getFilterInfo(){
        logger.info(STARTING_METHOD_EXECUTION);
        List<HashMap<String, Set<String>>> info=new ArrayList<>();
        List<UserModel> userModels=userRepo.findAll();
        HashMap<String ,Set<String>> gender=new HashMap<>();
        HashMap<String ,Set<String>> designation=new HashMap<>();
        HashMap<String ,Set<String>> roles=new HashMap<>();
        Set<String> genderSet=userModels.stream().map(UserModel::getGender).collect(Collectors.toSet());
        Set<String> desSet=userModels.stream().map(UserModel::getDesignation).collect(Collectors.toSet());
        Set<Integer> roleIds=userModels.stream().map(UserModel::getRoleId).collect(Collectors.toSet());
        Set<String> roleNames=new HashSet<>();
        for(int roleId:roleIds){
            roleNames.add(roleRepo.getRoleByRoleId(roleId));
        }
        gender.put(GENDER,genderSet);
        info.add(gender);
        designation.put(DESIGNATION,desSet);
        info.add(designation);
        roles.put(ROLE,roleNames);
        info.add(roles);
        logger.info(EXITING_METHOD_EXECUTION);
        return info;
    }
}