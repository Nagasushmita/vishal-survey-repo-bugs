package io.springboot.survey.specification;

import io.springboot.survey.models.TeamMemberModel;
import io.springboot.survey.models.TeamModel;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.RoleRepo;
import io.springboot.survey.repository.TeamMemberRepo;
import io.springboot.survey.repository.TeamRepo;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.response.SurveyResponse;
import io.springboot.survey.response.UserFilter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static io.springboot.survey.utils.Constants.CommonConstant.*;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.TeamConstants.*;
import static io.springboot.survey.utils.Constants.TemplateModuleConstant.PERCENTAGE;

@Component
public class SpecificationServiceImpl implements SpecificationService {

    final UserRepo userRepo;
    final TeamRepo teamRepo;
    final TeamMemberRepo teamMemberRepo;
    final RoleRepo roleRepo;

    private static final Logger logger= LoggerFactory.getLogger(SpecificationServiceImpl.class.getSimpleName());

    public SpecificationServiceImpl(UserRepo userRepo, TeamRepo teamRepo, TeamMemberRepo teamMemberRepo, RoleRepo roleRepo) {
        this.userRepo = userRepo;
        this.teamRepo = teamRepo;
        this.teamMemberRepo = teamMemberRepo;
        this.roleRepo = roleRepo;
    }


    @Override
    public Specification<UserModel> getUserFilter(String email, UserFilter userFilter) {
        return  (root, cq, cb) -> {
            Predicate p = cb.conjunction();
            if (!userFilter.getGender().isEmpty()) {
                Predicate pred = genderFilter(userFilter, root, cb);
                p = cb.and(p, pred);
            }
            if (!userFilter.getDesignation().isEmpty()) {
                Predicate pred = designationFilter(userFilter, root, cb);
                p = cb.and(p, pred);
            }
            if (!userFilter.getRole().isEmpty()) {
                Predicate predicate = roleFilter(userFilter, root, cb);
                p = cb.and(p, predicate);
            }
            if (!userFilter.getProject().isEmpty()) {
                Predicate predicate = projectFilter(userFilter, root, cb);
                p = cb.and(p, predicate);
            }
            return p;
        };
    }

    private Predicate genderFilter(@NotNull UserFilter userFilter, Root<UserModel> root, @NotNull CriteriaBuilder cb ){
        logger.info(STARTING_METHOD_EXECUTION);
        Predicate predicate=cb.disjunction();
        for(String gender:userFilter.getGender()){
            predicate=cb.or(predicate,cb.equal(root.get(GENDER),gender));
        }
        return predicate;
    }

    private Predicate designationFilter(@NotNull UserFilter userFilter, Root<UserModel> root, @NotNull CriteriaBuilder cb ){
        logger.info(STARTING_METHOD_EXECUTION);
        Predicate predicate=cb.disjunction();
        for(String designation:userFilter.getDesignation()){
            predicate=cb.or(predicate,cb.equal(root.get(DESIGNATION),designation));
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return predicate;
    }
    private Predicate roleFilter(@NotNull UserFilter userFilter, Root<UserModel> root, @NotNull CriteriaBuilder cb ){
        logger.info(STARTING_METHOD_EXECUTION);
        Predicate predicate=cb.disjunction();
        for(String role:userFilter.getRole()){
            int roleId=roleRepo.getRoleIdByRole(role);
            predicate=cb.or(predicate,cb.equal(root.get(ROLE_ID),roleId));
        }
        return predicate;
    }

    private Predicate projectFilter(@NotNull UserFilter userFilter, Root<UserModel> root, @NotNull CriteriaBuilder cb ){
        logger.info(STARTING_METHOD_EXECUTION);
        Predicate predicate=cb.disjunction();
        List<TeamMemberModel> all=new ArrayList<>();
        for(String project:userFilter.getProject()){
            List<TeamModel> teams=teamRepo.findByProjectNameContaining(project);
            for(TeamModel team:teams){
                all.addAll(teamMemberRepo.findByTeamId(team.getTeamId()));
            }
        }
        for(TeamMemberModel model:all){
            predicate=cb.or(predicate,cb.equal(root.get(USER_ID),model.getUserId()));
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return predicate;
    }


    private Predicate projectFilterTeam(@NotNull UserFilter userFilter, Root<TeamModel> root, @NotNull CriteriaBuilder cb){
        logger.info(STARTING_METHOD_EXECUTION);
        Predicate predicate = cb.disjunction();
        for (String project : userFilter.getProject()) {
            predicate = cb.or(predicate, cb.like(root.get(PROJECT),PERCENTAGE+project+PERCENTAGE));
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return predicate;
    }
    private Predicate projectStatusFilter(@NotNull UserFilter userFilter, Root<TeamModel> root, @NotNull CriteriaBuilder cb){
        logger.info(STARTING_METHOD_EXECUTION);
        Predicate predicate = cb.disjunction();
        for (String status : userFilter.getStatus()) {
            predicate = cb.or(predicate, cb.equal(root.get(STATUS), status));
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return predicate;
    }


    @Override
    public Specification<TeamModel> getTeamFilter(String email,UserFilter userFilter) {
        String roleName=roleRepo.getRoleByRoleId(userRepo.getRoleIdByUserEmail(email));
        return  (root, cq, cb) -> {
            Predicate p = cb.conjunction();
            if(roleName.equals(MANAGER)){
                int userId=userRepo.getUserIdByUserEmail(email);
                p=cb.and(p,cb.equal(root.get(MANAGER_ID),userId));
            }
            if (!userFilter.getProject().isEmpty()) {
                Predicate predicate=projectFilterTeam(userFilter,root,cb);
                p = cb.and(p, predicate);
            }
            if (!userFilter.getStatus().isEmpty()) {
                Predicate predicate=projectStatusFilter(userFilter,root,cb);
                p = cb.and(p, predicate);
            }
            logger.info(EXITING_METHOD_EXECUTION);
            return p;
        };
    }

    @Override
    public Specification<UserModel> getAllByDesignation(String email, SurveyResponse surveyResponse) {
        return (root, cq, cb) -> {
            Predicate p = cb.conjunction();
            if (!StringUtils.isEmpty(email)) {
                p = cb.and(p, cb.notEqual(root.get(USER_EMAIL), email));
            }
            if (!surveyResponse.getDesignation().isEmpty()) {
                Predicate predicate = cb.disjunction();
                for (String designation : surveyResponse.getDesignation()) {
                    predicate = cb.or(predicate, cb.equal(root.get(DESIGNATION), designation));
                }
                p = cb.and(p, predicate);
            }return p;
        };
    }

}
