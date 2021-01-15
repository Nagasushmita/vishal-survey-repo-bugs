package io.springboot.survey.pojo;

import io.springboot.survey.pojo.mail.MailParam;
import io.springboot.survey.pojo.report.*;
import io.springboot.survey.pojo.survey.controller.GetSurveyInfoParam;
import io.springboot.survey.pojo.survey.controller.SurveyInfoParam;
import io.springboot.survey.pojo.survey.impl.*;
import io.springboot.survey.pojo.template.GetTemplateParam;
import io.springboot.survey.pojo.template.PaginationResponseParam;
import io.springboot.survey.pojo.template.TemplatePaginationParam;
import io.springboot.survey.pojo.user.DynamicSearchParam;
import io.springboot.survey.pojo.user.GetAllParam;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meanbean.test.BeanTester;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Tag("Model")
class PojoTest {

    private static final BeanTester beanTester=new BeanTester();


    @Test
    @DisplayName("MailParam Pojo Test")
    void mailParamPojo() {
        beanTester.testBean(MailParam.class);
    }


    @Test
    @DisplayName("GetSurveyInfoParam Pojo Test")
    void getSurveyInfoParam() {
        beanTester.testBean(GetSurveyInfoParam.class);
    }

    @Test
    @DisplayName("SurveyInfoParam  Pojo Test")
    void surveyInfoParam () {
        beanTester.testBean(SurveyInfoParam.class);
    }

    @Test
    @DisplayName("DashboardPaginationParam Pojo Test")
    void dashboardPaginationParam() {
        beanTester.testBean(DashboardPaginationParam.class);
    }

    @Test
    @DisplayName("FindByCreatorUserIdParam Pojo Test")
    void findByCreatorUserIdParam() {
        beanTester.testBean(FindByCreatorUserIdParam.class);
    }

    @Test
    @DisplayName("PaginationParam Pojo Test")
    void paginationParam() {
        beanTester.testBean(PaginationParam.class);
    }

    @Test
    @DisplayName("PendingTakenParam Pojo Test")
    void pendingTakenParam() {
        beanTester.testBean(PendingTakenParam.class);
    }


    @Test
    @DisplayName("StatusResponseParam Pojo Test")
    void statusResponseParam() {
        beanTester.testBean(StatusResponseParam.class);
    }

    @Test
    @DisplayName("SurveyPaginationParam Pojo Test")
    void surveyPaginationParam() {
        beanTester.testBean(SurveyPaginationParam.class);
    }

    @Test
    @DisplayName("GetTemplateParam Pojo Test")
    void getTemplateParam() {
        beanTester.testBean(GetTemplateParam.class);
    }

    @Test
    @DisplayName("PaginationResponseParam Pojo Test")
    void paginationResponseParam() {
        beanTester.testBean(PaginationResponseParam.class);
    }

    @Test
    @DisplayName("TemplatePaginationParam Pojo Test")
    void templatePaginationParam() {
        beanTester.testBean(TemplatePaginationParam.class);
    }

    @Test
    @DisplayName("DynamicSearchParam Pojo Test")
    void dynamicSearchParam() {
        beanTester.testBean(DynamicSearchParam.class);
    }

    @Test
    @DisplayName("GetAllParam Pojo Test")
    void getAllParam() {
        beanTester.testBean(GetAllParam.class);
    }

    @Test
    @DisplayName("GetRequestParam Pojo Test")
    void getRequestParam() {
        beanTester.testBean(GetRequestParam.class);
    }

    @Test
    @DisplayName("ConsolidatedReportParam Pojo Test")
    void consolidatedReportParam() {
        beanTester.testBean(ConsolidatedReportParam.class);
    }


    @Test
    @DisplayName("DateFilterResponseParam Pojo Test")
    void dateFilterResponseParam() {
        beanTester.testBean(DateFilterResponseParam.class);
    }

    @Test
    @DisplayName("GetAnswerResponseParam Pojo Test")
    void getAnswerResponseParam() {
        beanTester.testBean(GetAnswerResponseParam.class);
    }
    @Test
    @DisplayName("TeamDateFilterParam Pojo Test")
    void teamDateFilterParam() {
        beanTester.testBean(TeamDateFilterParam.class);
    }
    @Test
    @DisplayName("TeamFilterSwitchParam Pojo Test")
    void teamFilterSwitchParam() {
        beanTester.testBean(TeamFilterSwitchParam.class);
    }
    @Test
    @DisplayName("TeamReportAnswerParam Pojo Test")
    void teamReportAnswerParam() {
        beanTester.testBean(TeamReportAnswerParam.class);
    }
    @Test
    @DisplayName("TeamReportParam Pojo Test")
    void teamReportParam() {
        beanTester.testBean(TeamReportParam.class);
    }
}

