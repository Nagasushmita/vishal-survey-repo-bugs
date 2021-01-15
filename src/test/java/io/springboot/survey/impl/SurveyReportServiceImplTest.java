package io.springboot.survey.impl;

import io.springboot.survey.models.TemplateModel;
import io.springboot.survey.repository.SurveyRepo;
import io.springboot.survey.repository.SurveyStatusRepo;
import io.springboot.survey.repository.TemplateRepo;
import io.springboot.survey.response.SurveyResponse;
import io.springboot.survey.service.ConsolidatedReportService;
import io.springboot.survey.service.SurveyTeamReportService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
class SurveyReportServiceImplTest {

    @InjectMocks
    SurveyReportServiceImpl surveyReportService;

    @Mock
    ConsolidatedReportService consolidatedReportService;

    @Mock
    SurveyTeamReportService surveyTeamReportService;
    @Mock
    SurveyRepo surveyRepo;
    @Mock
    SurveyStatusRepo surveyStatusRepo;
    @Mock
    TemplateRepo templateRepo;


    @Nested
    @DisplayName("Template Report Switch")
    class TemplateReport {
        @BeforeEach
        public void init() {
            TemplateModel templateModel=Mockito.mock(TemplateModel.class);
            when(templateRepo.findByTemplateName(null)).thenReturn(templateModel);
            when(surveyRepo.findSurveysByTemplateId(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singletonList(1)));
        }

        @Test
        @DisplayName("Consolidated Report Test")
        void consolidatedReport() throws ParseException {
            SurveyResponse surveyResponse=Mockito.mock(SurveyResponse.class);
            Object object=Mockito.mock(Object.class);
            when(surveyStatusRepo.findBySurveyId(Mockito.anyInt())).thenReturn(new HashSet<>(Collections.singleton(-1)));
            when(consolidatedReportService.consolidatedReports(Mockito.any())).thenReturn(object);
            Object response=surveyReportService.reportSwitch(surveyResponse);
            assertNotNull(response);
        }

        @Test
        @DisplayName("Team Report Test")
        void teamReport() throws ParseException {
            SurveyResponse surveyResponse=Mockito.mock(SurveyResponse.class);
            Object object=Mockito.mock(Object.class);
            when(surveyStatusRepo.findBySurveyId(Mockito.anyInt())).thenReturn(new HashSet<>(Collections.singleton(1)));
            when(surveyTeamReportService.teamReport(Mockito.any())).thenReturn(object);
            Object response=surveyReportService.reportSwitch(surveyResponse);
            assertNotNull(response);
        }


    }
}