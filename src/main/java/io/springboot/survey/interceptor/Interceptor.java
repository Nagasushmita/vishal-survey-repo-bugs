package io.springboot.survey.interceptor;

import io.springboot.survey.exception.AuthorizationException;
import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.utils.AuthorizationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static io.springboot.survey.utils.Constants.AuthorizationModuleConstant.*;
import static io.springboot.survey.utils.Constants.ErrorMessageConstant.USER_UNAUTHORIZED;
import static io.springboot.survey.utils.Constants.NullEmptyConstant.JWT_NULL;

@Component
public class Interceptor implements HandlerInterceptor {

    @Autowired
    AuthorizationService authorizationService;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        boolean result;
        String uriAddress = request.getRequestURI();
        String jwtToken=request.getHeader(JWT);
       if(!authorizationService.showEndpointsAction(uriAddress))
       {
           response.getWriter().write(INVALID_URI);
           response.setStatus(HttpStatus.BAD_REQUEST.value());
           return true;
       }
        if( !StringUtils.isEmpty(jwtToken)) {
            result = authorizationService.authorizationManager(jwtToken, uriAddress);
            if (result) {
                return false;
            } else {
                throw new AuthorizationException(USER_UNAUTHORIZED);
            }
        }
        else {
            throw new ResourceNotFoundException(JWT_NULL);
        }
    }

}
