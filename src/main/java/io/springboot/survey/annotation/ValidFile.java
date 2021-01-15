package io.springboot.survey.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.springboot.survey.utils.Constants.ValidationConstant.INVALID_FILE_TYPE;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidFileValidator.class)
public @interface ValidFile {

    String message() default INVALID_FILE_TYPE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
