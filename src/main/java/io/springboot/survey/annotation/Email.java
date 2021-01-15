package io.springboot.survey.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.springboot.survey.utils.Constants.ValidationConstant.INVALID_EMAIL;


@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
public @interface Email {
    String message() default INVALID_EMAIL;
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
