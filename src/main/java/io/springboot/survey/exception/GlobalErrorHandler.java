package io.springboot.survey.exception;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import io.springboot.survey.response.ResponseMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.springboot.survey.utils.Constants.CommonConstant.*;
import static java.util.Arrays.asList;

@ControllerAdvice
@RestController
public class GlobalErrorHandler extends ResponseEntityExceptionHandler {

    private final Logger
            mLogger = Logger.getLogger(GlobalErrorHandler.class.getSimpleName());
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseMessage handleConstraintViolationException(ConstraintViolationException e)
    {
        String message=EMPTY_STRING;
       Set<ConstraintViolation<?>> exception= e.getConstraintViolations();
       for (ConstraintViolation<?> cs :exception)
       {
           message=message.concat(cs.getMessage()+STRING_COMMA);
       }
        return new ResponseMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(),message);
    }

    @Override
    protected @NotNull ResponseEntity<Object> handleMethodArgumentNotValid(@NotNull MethodArgumentNotValidException ex, @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest request) {
        String message=EMPTY_STRING;
        BindingResult errorResult =ex.getBindingResult();
        for (FieldError error : errorResult.getFieldErrors() ) {
            message=message.concat(error.getDefaultMessage()+STRING_COMMA);
        }
        ResponseMessage responseMessage =new ResponseMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(),message);
        return new ResponseEntity<>(responseMessage, HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseMessage resourceNotFoundExceptionHandler(ResourceNotFoundException ex)
    {
        return new ResponseMessage(HttpStatus.NOT_FOUND.value(),ex.getMessage());
    }

    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseMessage authorizationExceptionHandler(AuthorizationException ex)
    {
        return new ResponseMessage(HttpStatus.UNAUTHORIZED.value(),ex.getMessage());
    }

    @Override
    protected @NotNull ResponseEntity<Object> handleTypeMismatch(@NotNull TypeMismatchException ex, @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest request) {
        ResponseMessage responseMessage =new ResponseMessage(HttpStatus.BAD_REQUEST.value(),ex.getMessage());
        return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity <ResponseMessage> nullPointerExceptionHandler(NullPointerException ex){
        mLogger.log(Level.SEVERE,NULL_POINTER_EXCEPTION,ex);
        ResponseMessage responseMessage = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Joiner.on(" \n ").join(Iterables.limit(asList(ex.getStackTrace()), 5)));
        return new ResponseEntity<>(responseMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(APIException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseMessage apiExceptionHandler(@NotNull APIException ex)
    {
        return new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseMessage badRequestExceptionHandler(@NotNull BadRequestException ex)
    {
        return new ResponseMessage(HttpStatus.BAD_REQUEST.value(),ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseMessage forbiddenExceptionHandler(@NotNull ForbiddenException ex)
    {
        return new ResponseMessage(HttpStatus.FORBIDDEN.value(),ex.getMessage());
    }

    @Override
    protected @NotNull ResponseEntity<Object> handleMissingServletRequestParameter(@NotNull MissingServletRequestParameterException ex, @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest request) {
        String requestURI= ((ServletWebRequest) request).getRequest().getRequestURI();
        String message= "Required "+ex.getParameterType()+" parameter "+ex.getParameterName()+" is not present ";
        logger.error(message+"for " + requestURI);
        ResponseMessage responseMessage =new ResponseMessage(HttpStatus.NOT_FOUND.value(),message);
        return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
    }


}
