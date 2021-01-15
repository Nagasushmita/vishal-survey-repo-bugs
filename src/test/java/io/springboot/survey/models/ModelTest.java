package io.springboot.survey.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meanbean.test.BeanTester;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Tag("Model")
class ModelTest {

    private static final BeanTester beanTester=new BeanTester();

    @Test
    @DisplayName("User Model Test")
    void userModelTest() {
        beanTester.testBean(UserModel.class);
    }
    @Test
    @DisplayName("Survey Status Model Test")
    void surveyStatusModelTest() {
        beanTester.testBean(SurveyStatusModel.class);
    }
    @DisplayName("Team Model Test")
    @Test
    void teamModel() {
        beanTester.testBean(TeamModel.class);
    }

    @Test
    @DisplayName("Survey Model Test")
    void surveyModeTest() {
        beanTester.testBean(SurveyModel.class);
    }
    @Test
    @DisplayName("Team Member Model Test")
    void teamMemberModel() {
        beanTester.testBean(TeamMemberModel.class);
    }
    @Test
    @DisplayName("Question Model Test")
    void questionModel() {
        beanTester.testBean(QuestionModel.class);
    }

    @DisplayName("Template Model Test")
    @Test
    void templateModel() {
        beanTester.testBean(TemplateModel.class);
    }
    @Test
    @DisplayName("Question Type Model")
    void questionTypeModel() {
        beanTester.testBean(QuestionTypeModel.class);
    }
    @Test
    @DisplayName("Template Answer Model Test")
    void templateAnswerModel() {
        beanTester.testBean(TemplateAnswerModel.class);
    }
    @Test
    @DisplayName("Role Model Test")
    void roleModel() {
        beanTester.testBean(RoleModel.class);
    }
    @Test
    @DisplayName("Answer Model Test")
    void answerModel() {
        beanTester.testBean(AnswerModel.class);
    }
    @Test
    @DisplayName("Survey Response Model Test")
    void surveyResponse() {
        beanTester.testBean(SurveyResponseModel.class);
    }
    @Test
    @DisplayName("Template Question Model")
    void templateQuestion() {
        beanTester.testBean(TemplateQuestionModel.class);
    }
    @Test
    @DisplayName("Response Model Test")
    void responseModel() {
        beanTester.testBean(ResponseModel.class);
    }
    @Test
    @DisplayName("Privileges Model Test")
    void privilegesModelTest() {
        beanTester.testBean(PrivilegesModel.class);
    }
}
