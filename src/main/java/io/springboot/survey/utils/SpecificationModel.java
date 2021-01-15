package io.springboot.survey.utils;

import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.response.SurveyResponse;
import io.springboot.survey.specification.SpecificationService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpecificationModel {

   private final UserRepo userRepo;
    private final SpecificationService specificationService;

    public SpecificationModel(UserRepo userRepo, SpecificationService specificationService) {
        this.userRepo = userRepo;

        this.specificationService = specificationService;
    }

    public List<UserModel> getAllByDesignation(String email, SurveyResponse surveyResponse) {
        return userRepo.findAll(specificationService.getAllByDesignation(email, surveyResponse));
    }
}
