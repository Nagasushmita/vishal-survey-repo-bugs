package io.springboot.survey.impl;

import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.models.SurveyModel;
import io.springboot.survey.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJacksonValue;

import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.springboot.survey.utils.Constants.ErrorMessageConstant.NO_SURVEYS_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@Tag("Service")
class SurveyDashboardServiceImplTest {

    @InjectMocks
    SurveyDashboardServiceImpl surveyDashboardService;

    @Mock
    SurveyRepo surveyRepo;
    @Mock
    UserRepo userRepo;
    @Mock
    SurveyStatusRepo surveyStatusRepo;
    @Mock
    TeamRepo teamRepo;
    @Mock
    SurveyResponseRepo surveyResponseRepo;

    @Test
    @DisplayName("Hr Dashboard Graph Test")
    void hrDashboardGraph() {
        SurveyModel surveyModel=Mockito.mock(SurveyModel.class);
        List<SurveyModel> surveyModels=new ArrayList<>();
        surveyModels.add(surveyModel);
        when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(123);
        when(surveyRepo.findAllByCreatorUserIdAndAndExpirationDateBetweenAndArchivedFalse(Mockito.anyInt(),Mockito.anyLong(),Mockito.anyLong()))
                .thenReturn(surveyModels);
        when(surveyStatusRepo.getSizeBySurveyIdAndTaken(Mockito.anyInt(),Mockito.anyBoolean())).thenReturn(1);
        when(surveyStatusRepo.getStatusModelSizeById(0)).thenReturn(1);
        MappingJacksonValue response= surveyDashboardService.hrDashboardGraph("dummy@nineleaps.com");
        when(surveyRepo.findAllByCreatorUserIdAndAndExpirationDateBetweenAndArchivedFalse(Mockito.anyInt(),Mockito.anyLong(),Mockito.anyLong()))
                .thenReturn(new ArrayList<>());
        Exception exception=assertThrows(ResourceNotFoundException.class,()->surveyDashboardService.hrDashboardGraph("dummy@nineleaps.com"));
        assertAll(()->assertNotNull(response),
                ()->assertNotNull(exception),
                ()->assertEquals(NO_SURVEYS_FOUND,exception.getMessage()));
    }

    @Test
    @DisplayName("Manager Dashboard Test")
    void managerDashboard() {
        when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(0);
        when(surveyRepo.getCountByCreatedAndArchived(0,false)).thenReturn(1);
        when(surveyRepo.getCountByCreatorUserIdAndCreationDate(Mockito.anyInt(),Mockito.any(),Mockito.any())).thenReturn(1);
        when(surveyStatusRepo.getSizeByAssignedBy(0)).thenReturn(1);
        when(teamRepo.getSizeByManagerId(Mockito.anyInt())).thenReturn(1);
        Map<String, Integer> response1=surveyDashboardService.managerDashboard("dummy@nineleaps.com");
        when(surveyRepo.getCountByCreatedAndArchived(0,false)).thenReturn(0);
        Map<String, Integer> response2=surveyDashboardService.managerDashboard("dummy@nineleaps.com");
        assertAll(()->assertNotNull(response1),
                ()->assertNotNull(response2));
    }

    @Test
    @DisplayName("Total Survey In Week Test")
    void surveysInWeek() {
        SurveyModel surveyModel=Mockito.mock(SurveyModel.class);
        List<SurveyModel> surveyModels=new ArrayList<>();
        surveyModels.add(surveyModel);
        Tuple tuple =Mockito.mock(Tuple.class);
        when(surveyRepo.findAllByCreationDateBetween(Mockito.anyLong(),Mockito.anyLong()))
                .thenReturn(surveyModels);
        when(userRepo.getUserNameAndUserEmail(Mockito.anyInt())).thenReturn(tuple);
        when(surveyStatusRepo.getStatusModelSizeById(Mockito.anyInt())).thenReturn(1);
        MappingJacksonValue response1=surveyDashboardService.surveysInWeek(1,2);
        MappingJacksonValue response2=surveyDashboardService.surveysInWeek(1,1);
        when(surveyRepo.findAllByCreationDateBetween(Mockito.anyLong(),Mockito.anyLong()))
                .thenReturn(new ArrayList<>());
        Exception exception=assertThrows(ResourceNotFoundException.class,()->surveyDashboardService.surveysInWeek(1,3));
        assertAll(()->assertNotNull(response1),
                ()->assertNotNull(response2),
                ()->assertNotNull(exception),
                ()->assertEquals(NO_SURVEYS_FOUND,exception.getMessage()));
    }

    @Test
    @DisplayName("My Survey In Week Test")
    void mySurveysInWeek() {
        SurveyModel surveyModel=Mockito.mock(SurveyModel.class);
        List<SurveyModel> surveyModels=new ArrayList<>();
        surveyModels.add(surveyModel);
        Tuple tuple =Mockito.mock(Tuple.class);
        when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(123);
        when(surveyRepo.findAllByCreatorUserIdAndAndCreationDateBetween(Mockito.anyInt(),Mockito.anyLong(),Mockito.anyLong()))
                .thenReturn(surveyModels);
        when(userRepo.getUserNameAndUserEmail(Mockito.anyInt())).thenReturn(tuple);
        when(surveyStatusRepo.getStatusModelSizeById(Mockito.anyInt())).thenReturn(1);
        MappingJacksonValue response1=surveyDashboardService.mySurveysInWeek("dummy@nineleaps.com",1,2);
        MappingJacksonValue response2=surveyDashboardService.mySurveysInWeek("dummy@nineleaps.com",1,1);
        when(surveyRepo.findAllByCreatorUserIdAndAndCreationDateBetween(Mockito.anyInt(),Mockito.anyLong(),Mockito.anyLong()))
                .thenReturn(new ArrayList<>());
        Exception exception=assertThrows(ResourceNotFoundException.class,()->surveyDashboardService.mySurveysInWeek("dummy@nineleaps.com",1,3));
        assertAll(()->assertNotNull(response1),
                ()->assertNotNull(response2),
                ()->assertNotNull(exception),
                ()->assertEquals(NO_SURVEYS_FOUND,exception.getMessage()));
    }

    @Test
    @DisplayName("Hr Dashboard Info Test")
    void hrDashboardInfo() {
        when(surveyRepo.getSize()).thenReturn(1);
        when(surveyRepo.getSizeByCreationDateBetween(Mockito.anyLong(),Mockito.anyLong())).thenReturn(1);
        when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(123);
        when(surveyRepo.getCountByCreatedAndArchived(123,false)).thenReturn(1);
        when(surveyResponseRepo.getSize()).thenReturn(1);
        Map<String, Integer> response1=surveyDashboardService.hrDashboardInfo("dummy@nineleaps.com");
        when(surveyRepo.getSize()).thenReturn(0);
        Map<String, Integer> response2=surveyDashboardService.hrDashboardInfo("dummy@nineleaps.com");
        assertAll(()->assertNotNull(response1),
                ()->assertNotNull(response2));
    }

    @Test
    @DisplayName("Total Survey Test")
    void totalSurveys() {
        SurveyModel surveyModel=Mockito.mock(SurveyModel.class);
        List<SurveyModel> surveyModels=new ArrayList<>();
        surveyModels.add(surveyModel);
        Tuple tuple= Mockito.mock(Tuple.class);
        when(surveyRepo.findAll()).thenReturn(surveyModels);
        when(userRepo.getUserNameAndUserEmail(Mockito.anyInt())).thenReturn(tuple);
        when(surveyStatusRepo.getStatusModelSizeById(Mockito.anyInt())).thenReturn(1);
        MappingJacksonValue response1=surveyDashboardService.totalSurveys(1,2);
        MappingJacksonValue response2=surveyDashboardService.totalSurveys(1,1);
        when(surveyRepo.findAll()).thenReturn(new ArrayList<>());
        Exception exception=assertThrows(ResourceNotFoundException.class,()->surveyDashboardService.totalSurveys(1,3));
        assertAll(()->assertNotNull(response1),
                ()->assertNotNull(response2),
                ()->assertNotNull(exception),
                ()->assertEquals(NO_SURVEYS_FOUND,exception.getMessage()));
    }
}