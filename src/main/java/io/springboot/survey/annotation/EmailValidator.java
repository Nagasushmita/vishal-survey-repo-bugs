package io.springboot.survey.annotation;

import io.springboot.survey.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<Email, String> {

    @Autowired
    UserRepo userRepo;

    @Override
    public void initialize(Email constraintAnnotation) {
        //do nothing
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
         return (userRepo.findByUserEmail(s)!=null);
    }
}
