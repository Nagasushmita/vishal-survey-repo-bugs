package io.springboot.survey.impl;

import io.springboot.survey.exception.CustomRetryException;
import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.mapper.SurveyByNameAndCreatorId;
import io.springboot.survey.mapper.SurveyStatusDto;
import io.springboot.survey.models.SurveyModel;
import io.springboot.survey.models.SurveyStatusModel;
import io.springboot.survey.models.TeamModel;
import io.springboot.survey.models.UploadFileModel;
import io.springboot.survey.pojo.GetRequestParam;
import io.springboot.survey.repository.*;
import io.springboot.survey.response.SurveyResponse;
import io.springboot.survey.response.UploadFileResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static io.springboot.survey.utils.Constants.CommonConstant.TEMPLATE_NOT_USED;
import static io.springboot.survey.utils.Constants.ErrorMessageConstant.FILE_UPLOADED;
import static io.springboot.survey.utils.Constants.ErrorMessageConstant.NO_SURVEYS_FOUND;
import static io.springboot.survey.utils.Constants.TeamConstants.TEAM_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
class SurveyServiceImplementationTest {

    @InjectMocks
    SurveyServiceImplementation surveyServiceImplementation;
    @Mock
    UserRepo userRepo;
    @Mock
    SurveyRepo surveyRepo;
    @Mock
    SurveyStatusRepo surveyStatusRepo;
    @Mock
    TeamRepo teamRepo;
    @Mock
    UploadFileRepo uploadFileRepo;



    @Nested
    @DisplayName("Get All Survey Test")
    class AllSurvey {
        @Test
        @DisplayName("Model Size Not Equal To Zero")
        void getAllSurvey() {
            SurveyModel surveyModel=Mockito.mock(SurveyModel.class);
            Page<SurveyModel> surveyModelPage=new PageImpl<>(new ArrayList<>(Collections.singletonList(surveyModel)));
            when(userRepo.getUserIdByUserEmail(Mockito.anyString())).thenReturn(1);
            when(surveyRepo.getCountByCreatedAndArchived(Mockito.anyInt(),Mockito.anyBoolean())).thenReturn(1);
            when(surveyRepo.findByCreatorUserIdAndArchived(Mockito.anyInt(), Mockito.any(), Mockito.anyBoolean()))
                    .thenReturn(surveyModelPage);
            when(surveyStatusRepo.getSizeBySurveyIdAndTaken(Mockito.anyInt(),Mockito.anyBoolean())).thenReturn(1);
            MappingJacksonValue response1=surveyServiceImplementation.getAllSurvey(new GetRequestParam("dummy@nineleaps.com",-1,2,"creationDate"));
            MappingJacksonValue response2=surveyServiceImplementation.getAllSurvey(new GetRequestParam("dummy@nineleaps.com",1,1,"creationDate"));
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));
        }
        @Test
        @DisplayName("Model Size Equal To Zero")
        void zeroModelSize() {
            when(userRepo.getUserIdByUserEmail(Mockito.anyString())).thenReturn(1);
            when(surveyRepo.getCountByCreatedAndArchived(Mockito.anyInt(),Mockito.anyBoolean())).thenReturn(0);
            GetRequestParam getRequestParam =new GetRequestParam("dummy@nineleaps.com",0,10,"creationDate");
            Exception exception=assertThrows(ResourceNotFoundException.class,
                    ()->surveyServiceImplementation.getAllSurvey(getRequestParam));
            assertEquals(NO_SURVEYS_FOUND,exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Get Archived Survey Test")
    class ArchivedSurveyTest {
        @Test
        @DisplayName("Model Size Not Equal To Zero")
        void getArchivedSurvey() {
            SurveyModel surveyModel=Mockito.mock(SurveyModel.class);
            Page<SurveyModel> surveyModelPage=new PageImpl<>(new ArrayList<>(Collections.singletonList(surveyModel)));
            when(userRepo.getUserIdByUserEmail(Mockito.anyString())).thenReturn(1);
            when(surveyRepo.getCountByCreatedAndArchived(Mockito.anyInt(),Mockito.anyBoolean())).thenReturn(1);
            when(surveyRepo.findByCreatorUserIdAndArchived(Mockito.anyInt(), Mockito.any(), Mockito.anyBoolean()))
                    .thenReturn(surveyModelPage);
            when(surveyStatusRepo.getSizeBySurveyIdAndTaken(Mockito.anyInt(),Mockito.anyBoolean())).thenReturn(1);

            MappingJacksonValue response1=surveyServiceImplementation.getArchivedSurvey(new GetRequestParam("dummy@nineleaps.com",1,2,"creationDate"));
            MappingJacksonValue response2=surveyServiceImplementation.getArchivedSurvey(new GetRequestParam("dummy@nineleaps.com",1,1,"creationDate"));
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));
        }
        @Test
        @DisplayName("Model Size Equal To Zero")
        void zeroModelSize() {
            when(userRepo.getUserIdByUserEmail(Mockito.anyString())).thenReturn(1);
            when(surveyRepo.getCountByCreatedAndArchived(Mockito.anyInt(),Mockito.anyBoolean())).thenReturn(0);
            GetRequestParam getRequestParam =new GetRequestParam("dummy@nineleaps.com",1,2,"creationDate");
            Exception exception=assertThrows(ResourceNotFoundException.class,
                    ()->surveyServiceImplementation.getArchivedSurvey(getRequestParam));
            assertEquals(NO_SURVEYS_FOUND,exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Decode Link Test ")
    class DecodeLinkTest {
        @BeforeEach
        void init()
        {
            SurveyStatusDto surveyStatusDto=Mockito.mock(SurveyStatusDto.class);
            SurveyByNameAndCreatorId survey=Mockito.mock(SurveyByNameAndCreatorId.class);
            when(userRepo.getSurveyDataByNameAndId(Mockito.anyString(),Mockito.anyString(),Mockito.anyBoolean()))
                    .thenReturn(survey);
            when(userRepo.getSurveyById(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(surveyStatusDto)));
        }
        @Test
        @DisplayName("Team Name Not Null")
        void decodeLink() {
            SurveyStatusModel surveyStatusModel=Mockito.mock(SurveyStatusModel.class);
            Tuple tuple=Mockito.mock(Tuple.class);
            TeamModel teamModel=Mockito.mock(TeamModel.class);
            when(userRepo.getUserNameAndUserEmail(Mockito.anyInt())).thenReturn(tuple);
            when(surveyStatusRepo.findBySurveyIdAndUserIdAndTeamIdAndTaken(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt(),Mockito.anyBoolean()))
                    .thenReturn(surveyStatusModel);
            when(teamRepo.findByTeamName(Mockito.anyString())).thenReturn(teamModel);
            when(teamRepo.getTeamId(Mockito.anyString())).thenReturn(1);
            String link = "Rm9vZCBGZWVkYmFjayBTdXJ2ZXkvbWVlbmFrc2hpLmthdXNoaWtAbmluZWxlYXBzLmNvbQ==";
            SurveyResponse response1=surveyServiceImplementation.decodeLink(link,"dummy");
            when(teamRepo.findByTeamName(Mockito.anyString())).thenReturn(null);
            Exception exception=assertThrows(ResourceNotFoundException.class,
                    ()->surveyServiceImplementation.decodeLink(link,"dummy"));
            assertAll(()->assertNotNull(response1),
                    ()->assertEquals(TEAM_NOT_FOUND,exception.getMessage()));
        }
        @Test
        @DisplayName("Team Name  Null")
        void teamNameNull() {
            when(surveyStatusRepo.findBySurveyIdAndUserIdAndTeamIdAndTaken(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt(),Mockito.anyBoolean()))
                    .thenReturn(null);
            String link = "Rm9vZCBGZWVkYmFjayBTdXJ2ZXkvbWVlbmFrc2hpLmthdXNoaWtAbmluZWxlYXBzLmNvbQ==";
            SurveyResponse response=surveyServiceImplementation.decodeLink(link,"null");
            assertNotNull(response);
        }
    }

    @Nested
    @DisplayName("Upload File Test")
    class UploadFileTest {
        @Test
        @DisplayName("Success")
        void uploadFile() throws CustomRetryException {
            MultipartFile file = Mockito.mock(MultipartFile.class);
            UploadFileModel uploadFileModel = Mockito.mock(UploadFileModel.class);
            when(uploadFileRepo.save(Mockito.any())).thenReturn(uploadFileModel);
            UploadFileResponse response;
            response = surveyServiceImplementation.uploadFile(file);
            assertEquals(FILE_UPLOADED, response.getResponse());
        }
        @Test
        @DisplayName("Exception")
        void uploadFileException() {
            CustomRetryException exception=assertThrows(CustomRetryException.class,()->surveyServiceImplementation.uploadFile(null));
            assertNotNull(exception);
        }

    }

    @Nested
    @DisplayName("Tool Tip Test")
    class ToolTipTest {
        @Test
        @DisplayName("Success")
        void tooltip() {
            SurveyStatusDto surveyStatusDto=Mockito.mock(SurveyStatusDto.class);
            SurveyModel surveyModel=Mockito.mock(SurveyModel.class);
           when(userRepo.getTemplateIdByName(Mockito.anyString())).thenReturn(1);
           when(surveyRepo.findSurveyModelByTemplateId(Mockito.anyInt()))
                   .thenReturn(new ArrayList<>(Collections.singleton(surveyModel)));
           when(userRepo.getSurveyById(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(surveyStatusDto)));
            Map<String, Integer> response1=surveyServiceImplementation.tooltip("dummy");
            SurveyStatusDto surveyStatusDto1=new SurveyStatusDto() {
                @Override
                public int getUserId() {
                    return 0;}
                @Override
                public Integer getTeamId() {
                    return -1; }
            };
            when(userRepo.getSurveyById(Mockito.anyInt())).thenReturn(new ArrayList<>(Collections.singleton(surveyStatusDto1)));
            Map<String, Integer> response2=surveyServiceImplementation.tooltip("dummy");
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));
        }

        @Test
        @DisplayName("Exception")
        void tooltipException() {
            when(userRepo.getTemplateIdByName(Mockito.anyString())).thenReturn(1);
            when(surveyRepo.findSurveyModelByTemplateId(Mockito.anyInt()))
                    .thenReturn(new ArrayList<>());
            Exception exception=assertThrows(ResourceNotFoundException.class,
                    ()->surveyServiceImplementation.tooltip("dummy"));
            assertEquals(TEMPLATE_NOT_USED,exception.getMessage());
        }
    }

    @Test
    @DisplayName("Retry Mechanism Test")
    void retryMechanism() {
        String response=surveyServiceImplementation.retryMechanism(Mockito.mock(CustomRetryException.class));
        assertNotNull(response);
    }
}