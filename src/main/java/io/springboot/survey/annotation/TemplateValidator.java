package io.springboot.survey.annotation;

import io.springboot.survey.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TemplateValidator implements ConstraintValidator<Template, String> {

    @Autowired
    UserRepo userRepo;

    @Override
    public void initialize(Template constraintAnnotation) {
        // do nothing
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return  (userRepo.getTemplateIdByName(s) != null);
    }

}
