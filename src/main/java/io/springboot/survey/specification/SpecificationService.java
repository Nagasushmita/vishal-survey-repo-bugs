package io.springboot.survey.specification;

import io.springboot.survey.models.TeamModel;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.response.SurveyResponse;
import io.springboot.survey.response.UserFilter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public interface SpecificationService {
    Specification<UserModel> getUserFilter(String email, UserFilter userFilter);
    Specification<TeamModel> getTeamFilter(String email, UserFilter userFilter);
    Specification<UserModel> getAllByDesignation(String email, SurveyResponse surveyResponse);
}
