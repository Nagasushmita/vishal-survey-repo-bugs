package io.springboot.survey.annotation;


import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.springboot.survey.utils.Constants.AuthorizationModuleConstant.JWT;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ApiImplicitParams({@ApiImplicitParam(name = JWT)})
public @interface IgnoreHeader {
}
