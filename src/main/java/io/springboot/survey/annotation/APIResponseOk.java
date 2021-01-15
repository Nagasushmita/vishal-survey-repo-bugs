package io.springboot.survey.annotation;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.springboot.survey.utils.Constants.ApiResponseConstant.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
        @ApiResponse(code = 200, message = OK_MESSAGE),
        @ApiResponse(code = 400, message = BAD_REQUEST),
        @ApiResponse(code = 404, message = RESOURCE_NOT_FOUND),
        @ApiResponse(code = 422, message = UNPROCESSABLE_ENTITY_MESSAGE),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)})
public @interface APIResponseOk {

}
