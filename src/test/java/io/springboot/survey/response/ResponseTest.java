package io.springboot.survey.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meanbean.test.BeanTester;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Tag("Response")
class ResponseTest {
    private static final BeanTester beanTester=new BeanTester();

    @Test
    @DisplayName("All Response Test")
    void AllResponseTest() {
        beanTester.testBean(AllResponse.class);
    }
    @Test
    @DisplayName("Answer Response Test")
    void answerResponse() {
        beanTester.testBean(AnswerResponse.class);
    }
    @Test
    @DisplayName("Authentication Response Test")
    void authenticationResponse() {
        beanTester.testBean(AuthenticationResponse.class);
    }
    @Test
    @DisplayName("DateFilter Response Test")
    void dateFilterResponse() {
        beanTester.testBean(DateFilterResponse.class);
    }
    @Test
    @DisplayName("Employee Details Test")
    void employeeDetails() {
        beanTester.testBean(EmployeeDetails.class);
    }
    @Test
    @DisplayName("Get Survey Response Test")
    void getSurveyResponse() {
        beanTester.testBean(GetSurveyResponse.class);
    }
    @Test
    @DisplayName("GraphList Response Test")
    void graphListResponse() {
        beanTester.testBean(GraphListResponse.class);
    }
    @Test
    @DisplayName("ListOfMails Test")
    void listOfMails() {
        beanTester.testBean(ListOfMails.class);
    }
    @Test
    @DisplayName("Member Response Test")
    void memberTest() {
        beanTester.testBean(Member.class);
    }
    @Test
    @DisplayName("Pagination Response Test")
    void paginationResponse() {
        beanTester.testBean(PaginationResponse.class);
    }
    @Test
    @DisplayName("PendingTaken Response Test")
    void pendingTakenResponse() {
        beanTester.testBean(PendingTakenResponse.class);
    }
    @Test
    @DisplayName("Privilege Set Test")
    void privilegeSet() {
        beanTester.testBean(PrivilegeSet.class);
    }

    @Test
    @DisplayName("Schedule Email Response Test")
    void scheduleEmailResponse() {
        beanTester.testBean(ScheduleEmailResponse.class);
    }
    @Test
    @DisplayName("Status Response Test")
    void statusResponse() {
        beanTester.testBean(StatusResponse.class);
    }
    @Test
    @DisplayName("Status Filtered Response Test")
    void statusFilteredResponse() {
        beanTester.testBean(StatusFilteredResponse.class);
    }
    @Test
    @DisplayName("Survey Data Test")
    void surveyData() {
        beanTester.testBean(SurveyData.class);
    }
    @Test
    @DisplayName("Survey Data Response Test")
    void surveyDataResponse() {
        beanTester.testBean(SurveyDataResponse.class);
    }
    @Test
    @DisplayName("Status Pagination Test")
    void surveyPagination() {
        beanTester.testBean(SurveyPagination.class);
    }
    @Test
    @DisplayName("Survey Response Test")
    void surveyResponse() {
        beanTester.testBean(SurveyResponse.class);
    }
    @Test
    @DisplayName("Template Count Response Test")
    void templateCountResponse() {
        beanTester.testBean(TemplateCountResponse.class);
    }
    @Test
    @DisplayName("Template Information Test")
    void templateInformation() {
        beanTester.testBean(TemplateInformation.class);
    }
    @Test
    @DisplayName("Template Response Test")
    void templateResponse() {
        beanTester.testBean(TemplateResponse.class);
    }
    @Test
    @DisplayName("Upload File Response Test")
    void uploadFile() {
        beanTester.testBean(UploadFileResponse.class);
    }
    @Test
    @DisplayName("User FilterTest")
    void userFilter() {
        beanTester.testBean(UserFilter.class);
    }
    @Test
    @DisplayName("Response Message Test")
    void responseMessage() {
        beanTester.testBean(ResponseMessage.class);
    }
    @Test
    @DisplayName("Assignee Information Test")
    void assigneeInformation() {
        beanTester.testBean(AssigneeInformationResponse.class);
    }


}