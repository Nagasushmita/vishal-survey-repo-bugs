package io.springboot.survey.utils;

import io.springboot.survey.exception.AuthorizationException;
import io.springboot.survey.models.PrivilegesModel;
import io.springboot.survey.repository.PrivilegesRepo;
import io.springboot.survey.repository.RoleRepo;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.service.JwtUtil;
import io.springboot.survey.service.PrivilegesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Arrays;
import java.util.List;

import static io.springboot.survey.utils.Constants.AuthorizationModuleConstant.*;
import static io.springboot.survey.utils.Constants.CommonConstant.MANAGER;
import static io.springboot.survey.utils.Constants.ErrorMessageConstant.INVALID_JWT_BEARER_FORMAT;

@Component
public class AuthorizationService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PrivilegesRepo privilegesRepo;

    @Autowired
    public RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    PrivilegesService privilegesService;

    public boolean authorizationManager(String jwt, String uriAddress)
    {
        List<String> list;
        String userEmail;
        PrivilegesModel privilegesModel;
        if(!jwt.contains(BEARER))
            throw new AuthorizationException(INVALID_JWT_BEARER_FORMAT);
        userEmail=jwtUtil.extractUserEmail(jwt.split(" ")[1]);
        privilegesModel=privilegesRepo.findByRoleId(userRepo.getRoleIdByUserEmail(userEmail));
        String roleName=roleRepo.getRoleByRoleId(userRepo.getRoleIdByUserEmail(userEmail));
        String message =uriAddress.split("/")[4];
        list=getPrivileges(privilegesModel);
        if(getUriArray().contains(message))
        {
            if(roleName.equals(MANAGER) && getHrUri().contains(message))
                return true;
            else
                return roleName.equals(MANAGER) && getManagerUri().contains(message);
        }
       else
        return list.contains(message);
    }

    private List<String> getPrivileges(PrivilegesModel privilegesModel)
    {
       return privilegesService.getPrivileges(privilegesModel);

    }
    public boolean showEndpointsAction(String uriAddress)
    {
        List<Object> endPoints= Arrays.asList(requestMappingHandlerMapping.getHandlerMethods().keySet().stream().map(t ->
                (t.getMethodsCondition().getMethods().isEmpty()? GET_MAPPING :
                        t.getPatternsCondition().getPatterns().toArray()[3])).toArray());
        return endPoints.contains(uriAddress);

    }
}
