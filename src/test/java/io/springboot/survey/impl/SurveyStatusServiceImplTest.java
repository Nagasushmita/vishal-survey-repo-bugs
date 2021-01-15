package io.springboot.survey.impl;

import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.mapper.SurveyByNameAndCreatorId;
import io.springboot.survey.mapper.SurveyStatusDto;
import io.springboot.survey.mapper.SurveyTakenDto;
import io.springboot.survey.models.SurveyModel;
import io.springboot.survey.models.SurveyStatusModel;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.pojo.survey.controller.GetSurveyInfoParam;
import io.springboot.survey.pojo.survey.controller.SurveyInfoParam;
import io.springboot.survey.repository.*;
import io.springboot.survey.response.AssigneeInformationResponse;
import io.springboot.survey.response.StatusFilteredResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.converter.json.MappingJacksonValue;

import javax.persistence.Tuple;
import java.util.*;

import static io.springboot.survey.utils.Constants.ErrorMessageConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
class SurveyStatusServiceImplTest {

    @InjectMocks
    SurveyStatusServiceImpl surveyStatusService;

    @Mock
    UserRepo userRepo;

    @Mock
    SurveyRepo surveyRepo;

    @Mock
    QuestionRepo questionRepo;
    @Mock
    SurveyStatusRepo surveyStatusRepo;
    @Mock
    SurveyResponseRepo surveyResponseRepo;
    @Mock
    TemplateRepo templateRepo;
    @Mock
    RoleRepo roleRepo;


    @Test
    @DisplayName("Survey Taken Info Test")
    void surveyTakenInfoTest() {
        SurveyStatusModel surveyStatusModel= Mockito.mock(SurveyStatusModel.class);
        List<SurveyStatusModel> modelList=new ArrayList<>();
        modelList.add(surveyStatusModel);
        SurveyTakenDto surveyTakenDto=Mockito.mock(SurveyTakenDto.class);
        when(userRepo.getSurveyStatusModel("dummy@nineleaps.com",true)).thenReturn(modelList);
        when(userRepo.getSurveyInfo(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt())).thenReturn(surveyTakenDto);
        MappingJacksonValue response1=surveyStatusService.getSurveyTakenInfo("dummy@nineleaps.com",1,2);
        SurveyStatusModel surveyStatusModel1=new SurveyStatusModel();
        surveyStatusModel1.setTeamId(-1);
        when(userRepo.getSurveyStatusModel("dummy@nineleaps.com",true)).thenReturn(new ArrayList<>(Collections.singletonList(surveyStatusModel1)));
        MappingJacksonValue response2=surveyStatusService.getSurveyTakenInfo("dummy@nineleaps.com",1,1);
    when(userRepo.getSurveyStatusModel("dummy@nineleaps.com",true)).thenReturn(new ArrayList<>());
        Exception exception= assertThrows(ResourceNotFoundException.class,
                ()->surveyStatusService.getSurveyTakenInfo("dummy@nineleaps.com",1,2));
        assertAll(() -> assertNotNull(response1),
                ()->assertNotNull(response2),
                ()-> assertEquals(NO_TAKEN_SURVEY,exception.getMessage()));
    }

    @Test
    @DisplayName("Survey Pending Info Test")
    void getSurveyPendingInfo() {
        SurveyStatusModel surveyStatusModel= Mockito.mock(SurveyStatusModel.class);
        List<SurveyStatusModel> modelList=new ArrayList<>();
        modelList.add(surveyStatusModel);
        SurveyModel surveyModel=Mockito.mock(SurveyModel.class);
        Tuple tuple=Mockito.mock(Tuple.class);
        when(userRepo.getSurveyStatusModel("dummy@nineleaps.com",false)).thenReturn(modelList);
        when(surveyRepo.findBySurveyId(Mockito.anyInt())).thenReturn(surveyModel);
        when(userRepo.getUserNameAndUserEmail(Mockito.anyInt())).thenReturn(tuple);
        when(questionRepo.getSizeBySurveyId(Mockito.anyInt())).thenReturn(1);
        MappingJacksonValue response1=surveyStatusService.getSurveyPendingInfo("dummy@nineleaps.com",1,2);
        SurveyStatusModel surveyStatusModel1=new SurveyStatusModel();
        surveyStatusModel1.setTeamId(-1);
        when(userRepo.getSurveyStatusModel("dummy@nineleaps.com",false)).thenReturn(new ArrayList<>(Collections.singleton(surveyStatusModel1)));
        MappingJacksonValue response2=surveyStatusService.getSurveyPendingInfo("dummy@nineleaps.com",1,1);
        when(userRepo.getSurveyStatusModel("dummy@nineleaps.com",false)).thenReturn(new ArrayList<>());
        Exception exception= assertThrows(ResourceNotFoundException.class,
                ()->surveyStatusService.getSurveyPendingInfo("dummy@nineleaps.com",1,2));
        assertAll(() -> assertNotNull(response1),
                ()->assertNotNull(response2),
                () -> assertNotNull(exception),
                ()-> assertEquals(NO_PENDING_SURVEY,exception.getMessage()));
    }

    @Test
    @DisplayName("Survey Info Test")
    void getSurveyInfo() {
        SurveyModel surveyModel=Mockito.mock(SurveyModel.class);
        List<SurveyModel> modelList=new ArrayList<>();
        modelList.add(surveyModel);
        Page<SurveyModel> modelPage=new PageImpl<>(modelList);
        when( userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(1);
        when(surveyRepo.findByCreatorUserIdAndArchived(Mockito.anyInt(),Mockito.any(),Mockito.anyBoolean())).thenReturn(modelPage);
        when(surveyRepo.getCountByCreatedAndArchived(1,false)).thenReturn(1);
        when(surveyStatusRepo.getStatusModelSizeById(Mockito.anyInt())).thenReturn(1);
        when( surveyStatusRepo.getSizeBySurveyIdAndTaken(Mockito.anyInt(),Mockito.anyBoolean())).thenReturn(1);
        MappingJacksonValue response1=surveyStatusService.getSurveyInfo(new GetSurveyInfoParam("dummy@nineleaps.com",1,1,"creationDate"));
        MappingJacksonValue response2=surveyStatusService.getSurveyInfo(new GetSurveyInfoParam("dummy@nineleaps.com",-1,2,"creationDate"));
        when(surveyRepo.getCountByCreatedAndArchived(1,false)).thenReturn(0);
       GetSurveyInfoParam surveyInfoParam=new GetSurveyInfoParam("dummy@nineleaps.com",1,1,"creationDate");
        Exception exception= assertThrows(ResourceNotFoundException.class,
                ()->surveyStatusService.getSurveyInfo(surveyInfoParam));
        assertAll(() -> assertNotNull(response1),
                ()->assertNotNull(response2),
                () -> assertNotNull(exception),
                ()-> assertEquals(NO_SURVEYS_FOUND,exception.getMessage()));
    }

    @Test
    @DisplayName("Survey Info Pending Test")
    void getSurveyInfoPending() {
        SurveyByNameAndCreatorId survey=Mockito.mock(SurveyByNameAndCreatorId.class);
        SurveyStatusDto surveyStatusDto=Mockito.mock(SurveyStatusDto.class);
        Tuple tuple=Mockito.mock(Tuple.class);
        Object [] object=new Object[4];
        object[1]=2L;
        when(userRepo.getSurveyDataByNameAndId("test","dummy@nineleaps.com",false))
                .thenReturn(survey);
        when(surveyStatusRepo.getSurveyByIdAndTaken(Mockito.anyInt(),Mockito.anyBoolean()))
                .thenReturn(new ArrayList<>(Collections.singleton(surveyStatusDto)));
        when(userRepo.getUserNameAndUserEmail(Mockito.anyInt())).thenReturn(tuple);
        when( userRepo.getSurveyData(Mockito.anyInt())).thenReturn(object);
        SurveyInfoParam surveyInfoParam=new SurveyInfoParam("test","dummy@nineleaps.com",1,10);
        MappingJacksonValue response1=surveyStatusService.getSurveyInfoPending(surveyInfoParam);
        SurveyStatusDto surveyStatusDto1=new SurveyStatusDto() {
            @Override
            public int getUserId() { return 0; }
            @Override
            public Integer getTeamId() { return -1; }
        };
        when(surveyStatusRepo.getSurveyByIdAndTaken(0,false))
                .thenReturn(new ArrayList<>(Collections.singleton(surveyStatusDto1)));
        MappingJacksonValue response2=surveyStatusService.getSurveyInfoPending(surveyInfoParam);
        when(surveyStatusRepo.getSurveyByIdAndTaken(0,false))
                .thenReturn(new ArrayList<>());
        Exception exception1= assertThrows(ResourceNotFoundException.class,
                ()->surveyStatusService.getSurveyInfoPending(surveyInfoParam));
        when(userRepo.getSurveyDataByNameAndId("test","dummy@nineleaps.com",false))
                .thenReturn(null);
        Exception exception2= assertThrows(ResourceNotFoundException.class,
                ()->surveyStatusService.getSurveyInfoPending(surveyInfoParam));
        assertAll(()-> assertNotNull(response1),
                ()->assertNotNull(response2),
                ()-> assertEquals(NO_PENDING_USER_SURVEY, exception1.getMessage()),
                ()-> assertEquals(SURVEY_NOT_FOUND,exception2.getMessage()));
    }


    @Test
    @DisplayName("Survey Info Taken Test")
    void getSurveyInfoTaken() {
        SurveyByNameAndCreatorId survey = Mockito.mock(SurveyByNameAndCreatorId.class);
        SurveyStatusDto surveyStatusDto = Mockito.mock(SurveyStatusDto.class);
        Tuple tuple = Mockito.mock(Tuple.class);
        Object [] objects=new Object[2];
        objects[0]=1L;
        objects[1]=1L;
        when(userRepo.getSurveyDataByNameAndId("test", "dummy@nineleaps.com", false))
                .thenReturn(survey);
        when(surveyStatusRepo.getSurveyByIdAndTaken(0, true))
                .thenReturn(new ArrayList<>(Collections.singleton(surveyStatusDto)));
        when(userRepo.getUserNameAndUserEmail(0)).thenReturn(tuple);
        when(surveyResponseRepo.getResponseIdAndTimestamp(0, 0, 0)).thenReturn(objects);
        when(userRepo.getSurveyData(0)).thenReturn(objects);
        MappingJacksonValue response1 = surveyStatusService.getSurveyInfoTaken(new SurveyInfoParam("test", "dummy@nineleaps.com", 1, 2));
        SurveyStatusDto surveyStatusDto1 = new SurveyStatusDto() {
            @Override
            public int getUserId()
            { return 0; }
            @Override
            public Integer getTeamId()
            { return -1; }
        };
        when(surveyStatusRepo.getSurveyByIdAndTaken(0, true))
                .thenReturn(new ArrayList<>(Collections.singleton(surveyStatusDto1)));
        when(surveyResponseRepo.getResponseIdAndTimestamp(0, 0, -1)).thenReturn(objects);
        SurveyInfoParam surveyInfoParam=new SurveyInfoParam("test", "dummy@nineleaps.com", 1, 2);
        MappingJacksonValue response2 = surveyStatusService.getSurveyInfoTaken(surveyInfoParam);
        when(surveyStatusRepo.getSurveyByIdAndTaken(0, true))
                .thenReturn(new ArrayList<>());
        Exception exception1 = assertThrows(ResourceNotFoundException.class,
                () -> surveyStatusService.getSurveyInfoTaken(surveyInfoParam));
        when(userRepo.getSurveyDataByNameAndId("test", "dummy@nineleaps.com", false))
                .thenReturn(null);
        Exception exception2 = assertThrows(ResourceNotFoundException.class,
                () -> surveyStatusService.getSurveyInfoTaken(surveyInfoParam));
        assertAll(() -> assertNotNull(response1),
                () -> assertNotNull(response2),
                () -> assertEquals(NO_TAKEN_USER_SURVEY, exception1.getMessage()),
                () -> assertEquals(SURVEY_NOT_FOUND, exception2.getMessage()));
    }


    @Test
    @DisplayName("Survey Info Assigned Test")
    void getSurveyInfoAssigned() {
        SurveyByNameAndCreatorId survey = Mockito.mock(SurveyByNameAndCreatorId.class);
        SurveyStatusDto surveyStatusDto = Mockito.mock(SurveyStatusDto.class);
        Tuple tuple=Mockito.mock(Tuple.class);
        Object [] object=new Object[4];
        object[1]=2L;
        object[2]=2L;

        when(userRepo.getSurveyDataByNameAndId("test","dummy",false)).thenReturn(survey);
        when(userRepo.getSurveyById(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(surveyStatusDto)));
        when(userRepo.getUserNameAndUserEmail(0)).thenReturn(tuple);
        when( userRepo.getSurveyData(0)).thenReturn(object);
        MappingJacksonValue response  =surveyStatusService.getSurveyInfoAssigned(new SurveyInfoParam("test","dummy",1,1));
        when(userRepo.getSurveyById(Mockito.anyInt())).thenReturn(new ArrayList<>());
        SurveyInfoParam surveyInfoParam=new SurveyInfoParam("test","dummy",1,1);
        Exception exception1=assertThrows(ResourceNotFoundException.class,()->surveyStatusService.getSurveyInfoAssigned(surveyInfoParam));
        when(userRepo.getSurveyDataByNameAndId("test","dummy",false)).thenReturn(null);
        Exception exception2=assertThrows(ResourceNotFoundException.class,()->surveyStatusService.getSurveyInfoAssigned(surveyInfoParam));
        assertAll(() -> assertNotNull(response),
                () -> assertEquals(NO_ASSIGNED_USER_SURVEY, exception1.getMessage()),
                () -> assertEquals(SURVEY_NOT_FOUND, exception2.getMessage()));
    }

    @Test
    @DisplayName("Survey Information Test")
    void surveyInformation() {
        SurveyByNameAndCreatorId survey = Mockito.mock(SurveyByNameAndCreatorId.class);
        SurveyModel surveyModel=Mockito.mock(SurveyModel.class);
        SurveyStatusDto surveyStatusDto=Mockito.mock(SurveyStatusDto.class);
        UserModel userModel=Mockito.mock(UserModel.class);
        when(userRepo.getUserIdByUserEmail("dummy")).thenReturn(1);
        when(userRepo.getSurveyDataByNameAndId("test","dummy",false)).thenReturn(survey);
        when(surveyRepo.findBySurveyNameAndCreatorUserIdAndArchivedFalse(Mockito.anyString(),Mockito.anyInt())).thenReturn(surveyModel);
        when(userRepo.getSurveyById(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(surveyStatusDto)));
        when(userRepo.findByUserId(Mockito.anyInt())).thenReturn(userModel);
        when(surveyStatusRepo.getSizeBySurveyIdAndTaken(Mockito.anyInt(),Mockito.anyBoolean())).thenReturn(1);
        when(questionRepo.getSizeBySurveyId(Mockito.anyInt())).thenReturn(1);
        when(templateRepo.getTemplateNameById(Mockito.anyInt())).thenReturn("dummy");
        MappingJacksonValue response1=surveyStatusService.surveyInformation("test","dummy");
        SurveyModel surveyModel1=new SurveyModel();
        surveyModel1.setTemplateId(1);
        when(surveyRepo.findBySurveyNameAndCreatorUserIdAndArchivedFalse(Mockito.anyString(),Mockito.anyInt())).thenReturn(surveyModel1);
        MappingJacksonValue response2=surveyStatusService.surveyInformation("test","dummy");
        when(userRepo.getSurveyDataByNameAndId("test","dummy",false)).thenReturn(null);
        Exception exception=assertThrows(ResourceNotFoundException.class,()->surveyStatusService.surveyInformation("test","dummy"));
        assertAll(()->assertNotNull(response1),
                ()->assertNotNull(response2),
                ()->assertEquals(SURVEY_NOT_FOUND,exception.getMessage()));
    }

    @Test
    @DisplayName("Active Survey Test")
    void getActiveSurvey() {
        SurveyStatusModel surveyStatusModel=Mockito.mock(SurveyStatusModel.class);
        SurveyModel surveyModel=new SurveyModel();
        surveyModel.setExpirationDate(System.currentTimeMillis()+(10*840000));
        Tuple tuple=Mockito.mock(Tuple.class);
        when( userRepo.getSurveyStatusModel("dummy",false))
                .thenReturn(new ArrayList<>(Collections.singleton(surveyStatusModel)));
        when(surveyRepo.findBySurveyId(Mockito.anyInt())).thenReturn(surveyModel);
        when(userRepo.getUserNameAndUserEmail(Mockito.anyInt())).thenReturn(tuple);
        when(questionRepo.getSizeBySurveyId(Mockito.anyInt())).thenReturn(1);
        List<StatusFilteredResponse> response=surveyStatusService.getActiveSurvey("dummy");
        surveyModel.setExpirationDate(System.currentTimeMillis()-(50*840000));
        Exception exception=assertThrows(ResourceNotFoundException.class,()->surveyStatusService.getActiveSurvey("dummy"));
        assertAll(()->assertNotNull(response),
                ()->assertEquals(NO_ACTIVE_SURVEY,exception.getMessage()));
    }

    @Test
    @DisplayName("Total Assigned Survey Test")
    void totalAssignedSurveys() {
        SurveyStatusModel surveyStatusModel=Mockito.mock(SurveyStatusModel.class);
        Object[] objects=new Object[2];
        Object [] object=new Object[4];
        object[0]=2L;
        object[1]=2L;
        object[2]="dummy";
        object[3]="dummy";
        when(userRepo.getSurveyStatusModelByEmail(Mockito.anyString())).thenReturn(new ArrayList<>(Collections.singleton(surveyStatusModel)));
        when(userRepo.getSurveyData(Mockito.anyInt())).thenReturn(object);
        when(userRepo.getUserNameAndEmailBySurveyId(Mockito.anyInt())).thenReturn(objects);
        when(surveyStatusRepo.getStatusModelSizeById(Mockito.anyInt())).thenReturn(1);
        MappingJacksonValue response1=surveyStatusService.totalAssignedSurveys("dummy",1,1);
        SurveyStatusModel surveyStatusModel1=new SurveyStatusModel();
        surveyStatusModel1.setTeamId(-1);
        surveyStatusModel1.setTaken(true);
        when(userRepo.getSurveyStatusModelByEmail(Mockito.anyString())).thenReturn(new ArrayList<>(Collections.singleton(surveyStatusModel1)));
        MappingJacksonValue response2=surveyStatusService.totalAssignedSurveys("dummy",1,2);
        when(userRepo.getSurveyStatusModelByEmail(Mockito.anyString())).thenReturn(new ArrayList<>());
        Exception exception=assertThrows(ResourceNotFoundException.class,()->surveyStatusService.totalAssignedSurveys("dummy",1,2));
        assertAll(()->assertNotNull(response1),
                ()->assertNotNull(response2),
                ()->assertEquals(NO_SURVEYS_FOUND,exception.getMessage()));
    }

    @Test
    @DisplayName("Survey Assignee Info Test")
    void surveyAssigneeInfo() {
        when(userRepo.getUserIdByUserEmail(Mockito.anyString())).thenReturn(1);
        when(surveyStatusRepo.getAssignedByUserId(Mockito.anyInt())).thenReturn(new HashSet<>(Arrays.asList(2,4)));
        when(userRepo.getRoleIdByUserId(Mockito.anyInt())).thenReturn(12);
        when(roleRepo.getRoleByRoleId(Mockito.anyInt())).thenReturn("dummy");
        when(surveyStatusRepo.getSizeByUserIdAndAssignedBy(Mockito.anyInt(),Mockito.anyInt())).thenReturn(12);
        when(surveyStatusRepo.getSizeByUserIdAndAssignedByAndTaken(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyBoolean())).thenReturn(1);
        List<AssigneeInformationResponse> response=surveyStatusService.surveyAssigneeInfo("dummy@nineleps.com");
        assertNotNull(response);
    }
}