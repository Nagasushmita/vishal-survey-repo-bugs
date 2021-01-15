package io.springboot.survey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.request.CreateSurveyRequest;
import io.springboot.survey.request.DeleteArchiveRequest;
import io.springboot.survey.request.UserSurveyRequest;
import io.springboot.survey.response.*;
import io.springboot.survey.service.SurveyCrudService;
import io.springboot.survey.service.SurveyResponseService;
import io.springboot.survey.service.SurveyService;
import io.springboot.survey.utils.AuthorizationService;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.springboot.survey.utils.Constants.ErrorMessageConstant.*;
import static io.springboot.survey.utils.Constants.ValidationConstant.MOCK_TOKEN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SurveyController.class)
@Tag("Controller")
class SurveyControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    SurveyService surveyService;
    @MockBean
    SurveyCrudService surveyCrudService;
    @MockBean
    SurveyResponseService surveyResponseService;
    @MockBean
    UserRepo userRepo;
    @MockBean
    AuthorizationService authorizationService;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp()
    {
         UserModel userModel = Mockito.mock(UserModel.class);
        when(authorizationService.showEndpointsAction(Mockito.anyString())).thenReturn(true);
        when(authorizationService.authorizationManager(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
        when(userRepo.findByUserEmail(Mockito.anyString())).thenReturn(userModel);
    }


   private CreateSurveyRequest getSurveyRequest()
    {
        CreateSurveyRequest createSurveyRequest=new CreateSurveyRequest();
        createSurveyRequest.setLink("link");
        createSurveyRequest.setSurveyName("dummySurvey");
        SurveyData surveyData=new SurveyData();
        surveyData.setQuesType("dummy");
        surveyData.setNumberOfOptions(1);
        surveyData.setAnswerText("dummy");
        surveyData.setQuestion("dummy Question");
        surveyData.setMandatory(true);
        surveyData.setAnswers(new ArrayList<>(Collections.singleton("answer")));
        surveyData.setFile("file");
        createSurveyRequest.setSurveyDataList(new ArrayList<>(Collections.singleton(surveyData)));
        createSurveyRequest.setCreatorEmail("dummy@nineleaps.com");
        return createSurveyRequest;

    }


    @Nested
    @DisplayName("Upload File test")
    class UploadFileTest {
        @Test
        @DisplayName("PNG File")
        void uploadFile() throws Exception {
            MockMvc mockMvc
                    = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
            MockMultipartFile firstFile = new MockMultipartFile("file", "filename.png", "image/png", "some xml".getBytes());
            UploadFileResponse uploadFileResponse = new UploadFileResponse();
            when(surveyService.uploadFile(Mockito.any())).thenReturn(uploadFileResponse);
            mockMvc.perform(multipart("/surveyManagement/v1/user/upload-file")
                    .file(firstFile)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN))
                    .andExpect(status().isOk())
                    .andReturn();
        }
        @Test
        @DisplayName("jpg File")
        void jpgFile() throws Exception {
            MockMvc mockMvc
                    = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
            MockMultipartFile firstFile = new MockMultipartFile("file", "filename.jpg", "image/jpeg", "some xml".getBytes());
            UploadFileResponse uploadFileResponse = new UploadFileResponse();
            when(surveyService.uploadFile(Mockito.any())).thenReturn(uploadFileResponse);
            mockMvc.perform(multipart("/surveyManagement/v1/user/upload-file")
                    .file(firstFile)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN))
                    .andExpect(status().isOk())
                    .andReturn();
        }
        @Test
        @DisplayName("pdf File")
        void pdfFile() throws Exception {
            MockMvc mockMvc
                    = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
            MockMultipartFile firstFile = new MockMultipartFile("file", "filename.pdf", "application/pdf", "some xml".getBytes());
            UploadFileResponse uploadFileResponse = new UploadFileResponse();
            when(surveyService.uploadFile(Mockito.any())).thenReturn(uploadFileResponse);
            mockMvc.perform(multipart("/surveyManagement/v1/user/upload-file")
                    .file(firstFile)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN))
                    .andExpect(status().isOk())
                    .andReturn();
        }

        @Test
        @DisplayName("Invalid File Type Test")
        void invalidFileType() throws Exception {
            MockMvc mockMvc
                    = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
            MockMultipartFile firstFile = new MockMultipartFile("file", "filename.gif", "image/gif", "some xml".getBytes());
            UploadFileResponse uploadFileResponse = new UploadFileResponse();
            when(surveyService.uploadFile(Mockito.any())).thenReturn(uploadFileResponse);
            mockMvc.perform(multipart("/surveyManagement/v1/user/upload-file")
                    .file(firstFile)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN))
                    .andExpect(status().isUnprocessableEntity())
                    .andReturn();
        }
    }


    @Nested
    @DisplayName("Create Survey Test")
    class CreateSurvey {
        @Test
        @DisplayName("Created - 201")
        void createSurvey() throws Exception {
            ResponseEntity<ResponseMessage> expectedResponseBody = new ResponseEntity<>(new ResponseMessage(HttpStatus.CREATED.value(), SUCCESS), HttpStatus.CREATED);
            CreateSurveyRequest createSurveyRequest = getSurveyRequest();
            when(surveyCrudService.createSurvey(Mockito.any())).thenReturn(expectedResponseBody);
            MvcResult mvcResult = mvc.perform(post("/surveyManagement/v1/io.springboot.survey/create")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(createSurveyRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();
            verify(surveyCrudService, times(1)).createSurvey(Mockito.any());
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            ResponseMessage responseMessage = expectedResponseBody.getBody();
            assertEquals(objectMapper.writeValueAsString(responseMessage), actualResponseBody);
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception{
            CreateSurveyRequest createSurveyRequest = new CreateSurveyRequest();
            mvc.perform(post("/surveyManagement/v1/io.springboot.survey/create")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(createSurveyRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Archive Survey Test")
    class ArchiveSurveyTest {
        @Test
        @DisplayName("Success - 200")
        void archiveSurveySuccess() throws Exception{
            DeleteArchiveRequest deleteArchiveRequest=getDeleteArchiveRequest();
            ResponseEntity<ResponseMessage> expectedResponseBody = new ResponseEntity<>(new ResponseMessage(HttpStatus.OK.value(), SUCCESS), HttpStatus.OK);
            when(surveyCrudService.archiveSurvey(Mockito.any())).thenReturn(expectedResponseBody);
          MvcResult mvcResult=  mvc.perform(put("/surveyManagement/v1/io.springboot.survey/archive")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(deleteArchiveRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            verify(surveyCrudService, times(1)).archiveSurvey(Mockito.any());
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            ResponseMessage responseMessage = expectedResponseBody.getBody();
            assertEquals(objectMapper.writeValueAsString(responseMessage), actualResponseBody);

        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception{
            DeleteArchiveRequest deleteArchiveRequest=new DeleteArchiveRequest();
            mvc.perform(put("/surveyManagement/v1/io.springboot.survey/archive")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(deleteArchiveRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }


    @Nested
    @DisplayName("UnArchive Survey Test")
    class UnArchiveTest {
        @Test
        @DisplayName("Success - 200")
        void unarchiveSurvey() throws Exception{
            DeleteArchiveRequest archiveRequest=getDeleteArchiveRequest();
            ResponseEntity<ResponseMessage> expectedResponseBody = new ResponseEntity<>(new ResponseMessage(HttpStatus.OK.value(), UNARCHIVED), HttpStatus.OK);
            when(surveyCrudService.unarchiveSurvey(Mockito.any())).thenReturn(expectedResponseBody);
            MvcResult mvcResult=  mvc.perform(put("/surveyManagement/v1/io.springboot.survey/unarchive")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(archiveRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            verify(surveyCrudService, times(1)).unarchiveSurvey(Mockito.any());
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            ResponseMessage responseMessage = expectedResponseBody.getBody();
            assertEquals(objectMapper.writeValueAsString(responseMessage), actualResponseBody);
        }

        @DisplayName("Invalid Data - 422")
        @Test
        void inValidData() throws Exception {
            DeleteArchiveRequest deleteArchiveRequest=new DeleteArchiveRequest();
            mvc.perform(put("/surveyManagement/v1/io.springboot.survey/unarchive")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(deleteArchiveRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Delete Survey Test")
    class DeleteSurvey {
        @Test
        @DisplayName("Success - 204")
        void deleteSurveySuccess() throws Exception {
            DeleteArchiveRequest archiveRequest=getDeleteArchiveRequest();
            ResponseEntity<Void> expectedResponseBody = ResponseEntity.noContent().build();
            when(surveyCrudService.deleteSurvey(Mockito.any())).thenReturn(expectedResponseBody);
              mvc.perform(delete("/surveyManagement/v1/io.springboot.survey")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(archiveRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent()) ;
              verify(surveyCrudService, times(1)).deleteSurvey(Mockito.any());
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception{
            DeleteArchiveRequest deleteArchiveRequest=new DeleteArchiveRequest();
            mvc.perform(delete("/surveyManagement/v1/io.springboot.survey")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(deleteArchiveRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Survey Response Test")
    class SurveyResponseTest {
        @Test
        @DisplayName("Success - 200")
        void surveyResponse() throws Exception{
            SurveyData surveyData=new SurveyData();
            UserSurveyRequest userSurveyRequest = new UserSurveyRequest();
            userSurveyRequest.setTeamName("test");
            userSurveyRequest.setEmail("test@nineleaps.com");
            userSurveyRequest.setCreatorEmail("test@nineleaps.com");
            userSurveyRequest.setSurveyName("testSurvey");
            userSurveyRequest.setSurveyDataList(new ArrayList<>(Collections.singleton(surveyData)));
            ResponseEntity<ResponseMessage> expectedResponseBody = new ResponseEntity<>(new ResponseMessage(HttpStatus.OK.value(), RESPONSE_STORED), HttpStatus.OK);
            when(surveyResponseService.surveyResponse(Mockito.any())).thenReturn(expectedResponseBody);
          MvcResult mvcResult=  mvc.perform(post("/surveyManagement/v1/user/submit-io.springboot.survey")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(userSurveyRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            verify(surveyResponseService, times(1)).surveyResponse(Mockito.any());
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            ResponseMessage responseMessage = expectedResponseBody.getBody();
            assertEquals(objectMapper.writeValueAsString(responseMessage), actualResponseBody);
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception{
            UserSurveyRequest userSurveyRequest = new UserSurveyRequest();
            mvc.perform(post("/surveyManagement/v1/user/submit-io.springboot.survey")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(userSurveyRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Get Survey Test")
    class GetSurveyTest {
        @Test
        @DisplayName("Success - 200")
        void getSurvey() throws Exception {
        GetSurveyResponse getSurveyResponse = getSurveyResponse();
        List<GetSurveyResponse> responseList=new ArrayList<>(Collections.singleton(getSurveyResponse));
        when(surveyResponseService.getSurvey(Mockito.anyString(),Mockito.anyString())).thenReturn(responseList);
          MvcResult mvcResult= mvc.perform((get("/surveyManagement/v1/io.springboot.survey-preview")
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("surveyName", "Team Performance Survey")
                    .param("creatorEmail", "vishal.jha@nineleaps.com")))
                    .andExpect(status().isOk())
                    .andReturn();
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertEquals(objectMapper.writeValueAsString(responseList), actualResponseBody);

        }

        private GetSurveyResponse getSurveyResponse() {
            GetSurveyResponse getSurveyResponse =new GetSurveyResponse();
            getSurveyResponse.setQuesType("dummy");
            getSurveyResponse.setNumberOfOptions(1);
            getSurveyResponse.setSurveyName("dummy");
            getSurveyResponse.setQuestion("dummy Question");
            getSurveyResponse.setMandatory(true);
            getSurveyResponse.setAnswers(new ArrayList<>(Collections.singleton("answer")));
            getSurveyResponse.setSurveyDescription("dummy description");
            return getSurveyResponse;
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/io.springboot.survey-preview")
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("surveyName", "Team Performance Survey")
                    .param("creatorEmail", "")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }


    @Nested
    @DisplayName("Get All Survey Test")
    class GetAllSurveyTest {
        @Test
        @DisplayName("Success -200")
        void getAllSurvey() throws Exception {
           mvc.perform((get("/surveyManagement/v1/surveys")
                    .contentType(MediaType.TEXT_PLAIN)
                   .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                   .contentType(MediaType.APPLICATION_JSON)
                   .param("page", "0")
                    .param("email", "vishal.jha@nineleaps.com")))
                    .andExpect(status().isOk());
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void InvalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/surveys")
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("page", "0")
                    .param("email", "")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }


    @Nested
    @DisplayName("Get Archived Survey Test")
    class GetArchivedSurvey {
        @Test
        @DisplayName("Success - 200")
        void getArchivedSurvey() throws Exception {
             mvc.perform((get("/surveyManagement/v1/surveys/archive")
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("page", "0")
                    .param("email", "vishal.jha@nineleaps.com")))
                    .andExpect(status().isOk())
                    .andReturn();
        }


        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/surveys/archive")
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("page", "0")
                    .param("email", "")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Survey By Link Test")
    class SurveyByLinkTest {
        @DisplayName("Success - 200")
        @Test
        void getSurveyByLink() throws Exception {
            GetSurveyResponse getSurveyResponse = new GetSurveyResponse();
            List<GetSurveyResponse> responseList=new ArrayList<>(Collections.singleton(getSurveyResponse));
            when(surveyResponseService.getSurveyByLink(Mockito.anyString())).thenReturn(responseList);
            MvcResult mvcResult= mvc.perform((get("/surveyManagement/v1/io.springboot.survey-link")
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("link", "dummyLink")))
                    .andExpect(status().isOk())
                    .andReturn();
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertEquals(objectMapper.writeValueAsString(responseList), actualResponseBody);
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
             mvc.perform((get("/surveyManagement/v1/io.springboot.survey-link")
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("link", "")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }


    @Nested
    @DisplayName("Decode Test")
    class DecodeTest {
        @Test
        @DisplayName("Success - 200")
        void decode() throws Exception {
            SurveyResponse surveyResponse=new SurveyResponse();
            surveyResponse.setSurveyName("testSurvey");
             when(surveyService.decodeLink(Mockito.anyString(),Mockito.anyString())).thenReturn(surveyResponse);
            MvcResult mvcResult= mvc.perform((get("/surveyManagement/v1/decode-link")
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("teamName","teamName")
                    .param("link", "dummyLink")))
                    .andExpect(status().isOk())
                    .andReturn();
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertEquals(objectMapper.writeValueAsString(surveyResponse), actualResponseBody);
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/decode-link")
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("teamName","")
                    .param("link", "dummyLink")))
                    .andExpect(status().isUnprocessableEntity())
                    .andReturn();
        }
    }
    @Nested
    @DisplayName("Tooltip Test")
    class TooltipTest{
    @Test
    @DisplayName("Success - 200")
    void tooltip() throws Exception {
        mvc.perform((get("/surveyManagement/v1/io.springboot.survey/report-type")
                .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                .contentType(MediaType.TEXT_PLAIN)
            .param("templateName","templateName")))
            .andExpect(status().isOk());
        verify(surveyService, times(1)).tooltip(Mockito.anyString());
    }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform((get("/surveyManagement/v1/io.springboot.survey/report-type")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .param("templateName","")))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Test
    @DisplayName("Option Mapping Test")
    void optionMapping() throws Exception {
        mvc.perform((options("/surveyManagement/v1/io.springboot.survey/report-type")
                .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                .contentType(MediaType.TEXT_PLAIN)
                .param("templateName","templateName")))
                .andExpect(status().isOk());
    }

    private DeleteArchiveRequest getDeleteArchiveRequest()
    {
        DeleteArchiveRequest deleteArchiveRequest=new DeleteArchiveRequest();
        deleteArchiveRequest.setSurveyName("testSurvey");
        deleteArchiveRequest.setCreatorEmail("test@nineleaps.com");
        deleteArchiveRequest.setCreationDate(System.currentTimeMillis());
        return  deleteArchiveRequest;
    }

}