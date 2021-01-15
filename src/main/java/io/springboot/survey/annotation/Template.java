package io.springboot.survey.annotation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.springboot.survey.utils.Constants.ErrorMessageConstant.NO_TEMPLATE_FOUND;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TemplateValidator.class)
public @interface Template {


    String message() default NO_TEMPLATE_FOUND;
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
