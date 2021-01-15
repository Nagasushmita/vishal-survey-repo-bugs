package io.springboot.survey.annotation;



import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmptyNotNullValidator implements ConstraintValidator<EmptyNotNull, String> {

    @Override
    public void initialize(EmptyNotNull constraintAnnotation) {
    //empty function
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !(s == null || "".equals(s));
    }
}


