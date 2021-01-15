package io.springboot.survey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.request.CreateTemplateRequest;
import io.springboot.survey.response.SurveyData;
import io.springboot.survey.service.TemplateResponseService;
import io.springboot.survey.service.TemplateService;
import io.springboot.survey.service.TemplateServiceCrud;
import io.springboot.survey.utils.AuthorizationService;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;

import static io.springboot.survey.utils.Constants.ValidationConstant.MOCK_TOKEN;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TemplateController.class)
@Tag("Controller")
class TemplateControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    AuthorizationService authorizationService;
    @MockBean
    UserRepo userRepo;
    @MockBean
    TemplateService templateService;
    @MockBean
    TemplateServiceCrud templateServiceCrud;
    @MockBean
    TemplateResponseService templateResponseService;


    @BeforeEach
    void setUp() {
        UserModel userModel = Mockito.mock(UserModel.class);
        when(authorizationService.showEndpointsAction(Mockito.anyString())).thenReturn(true);
        when(authorizationService.authorizationManager(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        when(userRepo.findByUserEmail(Mockito.anyString())).thenReturn(userModel);
        when(userRepo.getTemplateIdByName(Mockito.anyString())).thenReturn(1);

    }


    @DisplayName("Create Template Test")
    @Nested
    class CreateTemplateTest {
        @Test
        @DisplayName("Success - 201")
        void createTemplate() throws Exception {
            CreateTemplateRequest createTemplateRequest=getTemplateRequest();
            mvc.perform(post("/surveyManagement/v1/template/create")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(createTemplateRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());
            verify(templateServiceCrud,times(1)).createTemplate(Mockito.any(CreateTemplateRequest.class));
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            CreateTemplateRequest createTemplateRequest=new CreateTemplateRequest();
            mvc.perform(post("/surveyManagement/v1/template/create")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .content(objectMapper.writeValueAsString(createTemplateRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Delete Template Test")
    class DeleteTemplateTest {
        @Test
        @DisplayName("Success - 204")
        void deleteTemplate() throws Exception {
            mvc.perform(delete("/surveyManagement/v1/template")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("templateName","dummyTemplate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
            verify(templateServiceCrud,times(1)).deleteTemplates(Mockito.anyString());
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(delete("/surveyManagement/v1/template")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("templateName", "")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("Invalid Template Name")
        void invalidTemplateName() throws Exception {
            when(userRepo.getTemplateIdByName(Mockito.anyString())).thenReturn(null);
                mvc.perform(delete("/surveyManagement/v1/template")
                        .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                        .param("templateName", "dummyTemplate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnprocessableEntity());
            }
        }


    @Nested
    @DisplayName("Archive Template Test")
    class ArchiveTemplateTest {
        @Test
        @DisplayName("Success - 200")
        void archiveTemplate() throws Exception {
            mvc.perform(put("/surveyManagement/v1/template/archive")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("templateName","dummyTemplate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(templateServiceCrud,times(1)).archiveTemplate(Mockito.anyString());
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(put("/surveyManagement/v1/template/archive")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("templateName","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Template Question And Answers Test")
    class TemplateQuestionAndAnswersTest {
        @Test
        @DisplayName("Success - 200")
        void getTemplateQuestionsAndAnswers() throws Exception {
            mvc.perform(get("/surveyManagement/v1/io.springboot.survey/template-preview")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("templateName","dummyTemplate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(templateService,times(1)).getTemplate(Mockito.anyString());
        }
        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(get("/surveyManagement/v1/io.springboot.survey/template-preview")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("templateName","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("All Templates Test")
    class AllTemplates {
        @Test
        @DisplayName("Success - 200")
        void findAllTemplateByUserIdAndArchived() throws Exception {
            mvc.perform(get("/surveyManagement/v1/io.springboot.survey/templates/all")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","dummy@nineleaps.com")
                    .param("page","0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(get("/surveyManagement/v1/io.springboot.survey/templates/all")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","")
                    .param("page","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());

        }
    }

    @Nested
    @DisplayName("Template Used Count Test")
    class TemplateUsedCountTest {
        @Test
        @DisplayName("Success - 200")
        void getAllTemplate() throws Exception {
            mvc.perform(get("/surveyManagement/v1/io.springboot.survey/templates")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","dummy@nineleaps.com")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(templateService,times(1)).getUnarchivedTemplate(Mockito.anyString());
        }

        @Test
        @DisplayName("Invalid Data -  422")
        void invalidData() throws Exception {
            mvc.perform(get("/surveyManagement/v1/io.springboot.survey/templates")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("email","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());

        }
    }

    @Nested
    @DisplayName("Template By User Test")
    class TemplateByUserTest {
        @Test
        @DisplayName("Success - 200")
        void findTemplateByUser() throws Exception {
            mvc.perform(get("/surveyManagement/v1/templates")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("page","0")
                    .param("email","dummy@nineleaps.com")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(get("/surveyManagement/v1/templates")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("page","")
                    .param("email","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Used Template Test")
    class UsedTemplateTest {
        @Test
        @DisplayName("Success - 200")
        void getUsedTemplates() throws Exception {
            mvc.perform(get("/surveyManagement/v1/template-report/templates/used")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("page","0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(templateService,times(1)).getUsedTemplates(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyString());
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(get("/surveyManagement/v1/template-report/templates/used")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("page","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @DisplayName("Template Response Test")
    @Nested
    class TemplateResponse {
        @Test
        @DisplayName("Success - 200")
        void templateResponses() throws Exception {
            mvc.perform(get("/surveyManagement/v1/template-report/responses")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("templateName","dummyTemplate")
                    .param("team","true")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(templateResponseService,times(1)).getAllTemplateResponse(Mockito.anyString(),Mockito.anyBoolean());
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(get("/surveyManagement/v1/template-report/responses")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("templateName","")
                    .param("team","true")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Unarchive Template Test")
    class UnarchiveTemplateTest {
        @Test
        @DisplayName("Success - 200")
        void unarchiveTemplate() throws Exception {
            mvc.perform(put("/surveyManagement/v1/template/unarchive")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("templateName","dummyTemplate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(templateServiceCrud,times(1)).unarchiveTemplate(Mockito.anyString());
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(put("/surveyManagement/v1/template/unarchive")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("templateName","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Get Archive Template Test")
    class GetArchiveTemplateTest {
        @Test
        @DisplayName("Success - 200")
        void viewArchivedTemplate() throws Exception {
            mvc.perform(get("/surveyManagement/v1/templates/archive")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("page","0")
                    .param("email","dummy@nineleaps.com")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(get("/surveyManagement/v1/templates/archive")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("page","")
                    .param("email","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Template Information Test")
    class TemplateInformationTest {
        @Test
        void templateInformation() throws Exception {
            mvc.perform(get("/surveyManagement/v1/template-report/details")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("templateName","dummyTemplate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(templateService,times(1)).templateInformation(Mockito.anyString());
        }

        @Test
        @DisplayName("Invalid Data - 422")
        void invalidData() throws Exception {
            mvc.perform(get("/surveyManagement/v1/template-report/details")
                    .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                    .param("templateName","")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    private CreateTemplateRequest getTemplateRequest()
    {
        CreateTemplateRequest createTemplateRequest=new CreateTemplateRequest();
        SurveyData surveyData=new SurveyData();
        createTemplateRequest.setTemplateName("dummyTemplate");
        createTemplateRequest.setEmail("dummy@nineleaps.com");
        createTemplateRequest.setSurveyDataList(new ArrayList<>(Collections.singletonList(surveyData)));
        return createTemplateRequest;
    }
}