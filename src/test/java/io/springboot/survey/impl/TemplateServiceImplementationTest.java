package io.springboot.survey.impl;

import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.mapper.GetTemplate;
import io.springboot.survey.mapper.SurveyModelDto;
import io.springboot.survey.models.SurveyModel;
import io.springboot.survey.models.TemplateModel;
import io.springboot.survey.pojo.template.GetTemplateParam;
import io.springboot.survey.repository.*;
import io.springboot.survey.response.GetSurveyResponse;
import io.springboot.survey.response.TemplateCountResponse;
import io.springboot.survey.response.TemplateInformation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.springboot.survey.utils.Constants.ErrorMessageConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@Tag("Service")
class TemplateServiceImplementationTest {

    @InjectMocks
    TemplateServiceImplementation templateServiceImplementation;

    @Mock
    TemplateRepo templateRepo;

    @Mock
    UserRepo userRepo;

    @Mock
    TemplateQuestionRepo templateQuestionRepo;

    @Mock
    QuestionTypeRepo questionTypeRepo;

    @Mock
    TemplateAnswerRepo templateAnswerRepo;

    @Mock
    SurveyRepo surveyRepo;

    @Nested
    class TemplateUsedCountTest {
        @Test
        @DisplayName("Template Model List Not Empty")
        void getTemplateUsedCountNotEmpty() {
        TemplateModel templateModel=Mockito.mock(TemplateModel.class);
            SurveyModel surveyModel = Mockito.mock(SurveyModel.class);
            List<TemplateModel> templateModelList = new ArrayList<>(Collections.singletonList(templateModel));
            when(templateRepo.findByIsArchivedFalse()).thenReturn(templateModelList);
            when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(234);
            when(surveyRepo.findByCreatorUserIdAndAndTemplateIdAndArchivedFalse(234, 0)).thenReturn(surveyModel);
            List<TemplateCountResponse> response1 = templateServiceImplementation.getUnarchivedTemplate("dummy@nineleaps.com");
            when(surveyRepo.findByCreatorUserIdAndAndTemplateIdAndArchivedFalse(234, 0)).thenReturn(null);
            List<TemplateCountResponse> response2 = templateServiceImplementation.getUnarchivedTemplate("dummy@nineleaps.com");
            assertAll(() -> assertNotNull(response1),
                    () -> assertNotNull(response2));
        }
        @Test
        @DisplayName("Template Model List Empty")
        void getTemplateUsedCountEmpty() {
            when(templateRepo.findByIsArchivedFalse()).thenReturn(new ArrayList<>());
            Exception exception = assertThrows(ResourceNotFoundException.class,
                    () -> templateServiceImplementation.getUnarchivedTemplate("dummy@nineleaps.com"));
            assertNotNull(exception);
        }
    }


    @Nested
    class GetTemplateTest {
        @Test
        @DisplayName("Radio And Check  Type Question Test")
        void getTemplate() {
            GetTemplate getTemplate=Mockito.mock(GetTemplate.class);
            List<GetTemplate> model=new ArrayList<>();
            model.add(getTemplate);
            when(templateAnswerRepo.getAnswerByQuesId(0)).thenReturn(new ArrayList<>());
            when(templateRepo.getQuestionModelByName("testTemplate")).thenReturn(model);
            when(questionTypeRepo.getQuestionNameByTypeId(0)).thenReturn("radio");
            List<GetSurveyResponse> responses = templateServiceImplementation.getTemplate("testTemplate");
            assertNotNull(responses);
        }
        @Test
        @DisplayName("Rating Type Question Test")
        void getTemplateRatingQuestion() {
            GetTemplate getTemplate=Mockito.mock(GetTemplate.class);
            List<GetTemplate> model=new ArrayList<>();
            model.add(getTemplate);
            when(templateAnswerRepo.getAnswerByQuesId(0)).thenReturn(new ArrayList<>());
            when(templateRepo.getQuestionModelByName("testTemplate")).thenReturn(model);
            when(questionTypeRepo.getQuestionNameByTypeId(0)).thenReturn("rating");
            List<GetSurveyResponse> responses = templateServiceImplementation.getTemplate("testTemplate");
            assertNotNull(responses);
        }

        @Test
        @DisplayName("File Type Question Test")
        void getTemplateFileQuestion() {
            GetTemplate getTemplate=Mockito.mock(GetTemplate.class);
            List<GetTemplate> model=new ArrayList<>();
            model.add(getTemplate);
            when(templateAnswerRepo.getAnswerByQuesId(0)).thenReturn(new ArrayList<>());
            when(templateRepo.getQuestionModelByName("testTemplate")).thenReturn(model);
            when(questionTypeRepo.getQuestionNameByTypeId(0)).thenReturn("file");
            List<GetSurveyResponse> responses = templateServiceImplementation.getTemplate("testTemplate");
            assertNotNull(responses);
        }

        @Test
        @DisplayName("Text Type Question Test")
        void getTemplateTextQuestion() {
            GetTemplate getTemplate=Mockito.mock(GetTemplate.class);
            List<GetTemplate> model=new ArrayList<>();
            model.add(getTemplate);
            when(templateAnswerRepo.getAnswerByQuesId(0)).thenReturn(new ArrayList<>());
            when(templateRepo.getQuestionModelByName("testTemplate")).thenReturn(model);
            when(questionTypeRepo.getQuestionNameByTypeId(0)).thenReturn("text");
            List<GetSurveyResponse> responses = templateServiceImplementation.getTemplate("testTemplate");
            assertNotNull(responses);
        }

        @Test
        @DisplayName("Default Type Exception Test")
        void defaultType() {
            GetTemplate getTemplate=Mockito.mock(GetTemplate.class);
            List<GetTemplate> model=new ArrayList<>();
            model.add(getTemplate);
            when(templateRepo.getQuestionModelByName("testTemplate")).thenReturn(model);
            when(questionTypeRepo.getQuestionNameByTypeId(0)).thenReturn("default");
            Exception exception=assertThrows(IllegalArgumentException.class,()->templateServiceImplementation.getTemplate("testTemplate"));
            assertNotNull(exception);

        }
    }

    @Nested
    class GetMyTemplateTest {
        @Test
        @DisplayName("Template List Empty")
        void getMyTemplateEmpty() {
            TemplateModel templateModel = Mockito.mock(TemplateModel.class);
            List<TemplateModel> templateModels = new ArrayList<>(Collections.singleton(templateModel));
            Page<TemplateModel> pagedResponse = new PageImpl<>(templateModels);
            when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(0);
            when(surveyRepo.getTemplateIdByCreatorAndArchived(0, false)).thenReturn(new ArrayList<>());
            when(templateRepo.findByCreatorUserIdAndIsArchivedFalse(Mockito.anyInt(), Mockito.any())).thenReturn(pagedResponse);
            when(templateQuestionRepo.findByTemplateIdSize(0)).thenReturn(1);
           when(templateRepo.findByCreatorUserIdSize(Mockito.anyInt(),Mockito.anyBoolean())).thenReturn(3);
            MappingJacksonValue response1 = templateServiceImplementation.getMyTemplate(new GetTemplateParam("dummy@nineleaps.com", -1, 10, "creationDate"));
            MappingJacksonValue response2 = templateServiceImplementation.getMyTemplate(new GetTemplateParam("dummy@nineleaps.com", 1, 1, "creationDate"));
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));

        }

        @Test
        @DisplayName("Template List Not Empty")
        void getMyTemplateNotEmpty() {
            TemplateModel templateModel1 = new TemplateModel();
            templateModel1.setTemplateId(1);
            TemplateModel templateModel2 = new TemplateModel();
            templateModel2.setTemplateId(2);
            when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(234);
            when(surveyRepo.getTemplateIdByCreatorAndArchived(234, false)).thenReturn(new ArrayList<>(Collections.singleton(1)));
            when(templateRepo.findByCreatorUserIdAndIsArchivedFalse(234)).thenReturn(new ArrayList<>(Arrays.asList(templateModel1, templateModel2)));
            MappingJacksonValue response1 = templateServiceImplementation.getMyTemplate(new GetTemplateParam("dummy@nineleaps.com", 1, 10, "creationDate"));
            MappingJacksonValue response2 = templateServiceImplementation.getMyTemplate(new GetTemplateParam("dummy@nineleaps.com", 1, 1, "creationDate"));
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));
        }

        @Test
        @DisplayName("Template Not Found")
        void getMyTemplateNotFound() {
            when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(234);
            when(surveyRepo.getTemplateIdByCreatorAndArchived(234, false)).thenReturn(new ArrayList<>(Collections.singleton(1)));
            when(templateRepo.findByCreatorUserIdAndIsArchivedFalse(234)).thenReturn(new ArrayList<>());
            GetTemplateParam getTemplateParam=new GetTemplateParam("dummy@nineleaps.com", 1, 10, "creationDate");
            Exception exception = assertThrows(ResourceNotFoundException.class,
                    () -> templateServiceImplementation.getMyTemplate(getTemplateParam));
            assertAll(() -> assertNotNull(exception),
                    () -> assertEquals(NO_TEMPLATE_FOUND, exception.getMessage()));
        }
    }
    @Nested
    class UsedTemplatesTest {
        @Test
        @DisplayName("SurveyModel List Not Empty")
        void getUsedTemplatesNotEmpty() {
            when(surveyRepo.getTemplateIdByArchived(Mockito.anyBoolean())).thenReturn(new ArrayList<>(Collections.singleton(1)));
            when(surveyRepo.getCountByTemplateId(Mockito.anyInt())).thenReturn(2);
            when(templateRepo.findByTemplateId(Mockito.anyInt())).thenReturn(Mockito.any());
            MappingJacksonValue response1 = templateServiceImplementation.getUsedTemplates(1, 10, "creationDate");
            when(surveyRepo.getCountByTemplateId(Mockito.anyInt())).thenReturn(1);
            MappingJacksonValue response2 = templateServiceImplementation.getUsedTemplates(1, 1, "creationDate");
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));
        }
        @Test
        @DisplayName("SurveyModel List  Empty")
        void getUsedTemplatesEmpty() {
            when(surveyRepo.getTemplateIdByArchived(false)).thenReturn(new ArrayList<>());
            Exception exception = assertThrows(ResourceNotFoundException.class,
                    () -> templateServiceImplementation.getUsedTemplates(1, 10, "creationDate"));
            assertAll(() -> assertNotNull(exception),
                    () -> assertEquals(NO_TEMPLATE_USED, exception.getMessage()));
        }
    }
    @Nested
    class GetAllTemplateByUserId {
        @Test
        @DisplayName("TemplateId list Empty")
        void getAllTemplateByUserIdEmpty() {
            TemplateModel templateModel =Mockito.mock(TemplateModel.class);
            List<TemplateModel> templateModels = new ArrayList<>(Collections.singleton(templateModel));
            Page<TemplateModel> pagedResponse = new PageImpl<>(templateModels);
            when(userRepo.getUserIdByUserEmail(Mockito.anyString())).thenReturn(234);
            when(surveyRepo.getSurveyIdByCreatorId(Mockito.anyInt(),Mockito.anyBoolean())).thenReturn(new ArrayList<>());
            when(templateRepo.findByIsArchivedFalse()).thenReturn(templateModels);
            when(templateRepo.findByIsArchivedFalse(Mockito.any())).thenReturn(pagedResponse);
            when(templateRepo.findByIsArchivedFalse(Mockito.any())).thenReturn(pagedResponse);
            MappingJacksonValue response1 = templateServiceImplementation.getAllTemplateByUserId(new GetTemplateParam("dummy@nineleaps.com", 1, 1, "creationDate"));
            MappingJacksonValue response2 = templateServiceImplementation.getAllTemplateByUserId(new GetTemplateParam("dummy@nineleaps.com", -1, 10, "creationDate"));
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));
        }
        @Test
        @DisplayName("TemplateId list Not Empty")
        void getAllTemplateByUserIdNotEmpty() {
            TemplateModel templateModel1 =Mockito.mock(TemplateModel.class);
            TemplateModel templateModel2 = Mockito.mock(TemplateModel.class);
            when(userRepo.getUserIdByUserEmail(Mockito.anyString())).thenReturn(234);
            when(surveyRepo.getSurveyIdByCreatorId(Mockito.anyInt(),Mockito.anyBoolean())).thenReturn(new ArrayList<>(Collections.singleton(1)));
            when(templateRepo.findByIsArchivedFalse()).thenReturn(new ArrayList<>(Arrays.asList(templateModel1,templateModel2)));
            MappingJacksonValue response1 = templateServiceImplementation.getAllTemplateByUserId(new GetTemplateParam("dummy@nineleaps.com", 1, 1, "creationDate"));
            MappingJacksonValue response2 = templateServiceImplementation.getAllTemplateByUserId(new GetTemplateParam("dummy@nineleaps.com", 1, 10, "creationDate"));
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));
        }
    }

    @Nested
    class ArchivedTemplateTest {
        @Test
        @DisplayName("Template Model List Not Empty")
        void getArchivedTemplateNotEmpty() {
            TemplateModel templateModel = Mockito.mock(TemplateModel.class);
            List<TemplateModel> templateModels = new ArrayList<>(Collections.singleton(templateModel));
            Page<TemplateModel> pagedResponse = new PageImpl<>(templateModels);
            when(userRepo.getUserIdByUserEmail(Mockito.anyString())).thenReturn(234);
            when(templateRepo.findByCreatorUserIdSize(Mockito.anyInt(),Mockito.anyBoolean())).thenReturn(2);
            when(templateRepo.findByCreatorUserIdAndIsArchivedTrue(Mockito.anyInt(), Mockito.any())).thenReturn(pagedResponse);
            MappingJacksonValue response1 = templateServiceImplementation.getArchivedTemplate(new GetTemplateParam("dummy@nineleaps.com", 1, 10, "creationDate"));
            MappingJacksonValue response2 = templateServiceImplementation.getArchivedTemplate(new GetTemplateParam("dummy@nineleaps.com", -1, 1, "creationDate"));
            assertAll(()->assertNotNull(response1),
                    ()->assertNotNull(response2));
        }
        @Test
        @DisplayName("Template Model List Empty")
        void getArchivedTemplateEmpty() {
            when(userRepo.getUserIdByUserEmail("dummy@nineleaps.com")).thenReturn(234);
            when(templateRepo.findByCreatorUserIdSize(234,true)).thenReturn(0);
            GetTemplateParam getTemplateParam=new GetTemplateParam("dummy@nineleaps.com", 1, 10, "creationDate");

            Exception exception = assertThrows(ResourceNotFoundException.class,
                    () -> templateServiceImplementation.getArchivedTemplate(getTemplateParam));
            assertNotNull(exception);
        }
    }

    @Nested
    @DisplayName("Template Information Test")
    class TemplateInformationTest {
        @Test
        @DisplayName("SurveyModel List Not Empty")
        void templateInformationNotEmpty() {
            SurveyModelDto surveyModelDto=Mockito.mock(SurveyModelDto.class);
            List<SurveyModelDto> list=new ArrayList<>();
            list.add(surveyModelDto);
            Tuple tuple =Mockito.mock(Tuple.class);
            Object [] objects=new Object[3];
            objects[0]="string";
            objects[1]= 1L;
            objects[2]= 3;
            when(templateRepo.getSurveyModelByName("testTemplate")).thenReturn(list);
            when(templateRepo.getDescAndCreationDate("testTemplate")).thenReturn(objects);
            when(userRepo.getUserNameAndUserEmail(Mockito.anyInt())).thenReturn(tuple);
            TemplateInformation response = templateServiceImplementation.templateInformation("testTemplate");
            assertNotNull(response);
        }
        @Test
        @DisplayName("SurveyModel List  Empty")
        void templateInformationEmpty() {
            when(templateRepo.getSurveyModelByName("testTemplate")).thenReturn(new ArrayList<>());
            Exception exception = assertThrows(ResourceNotFoundException.class,
                    () -> templateServiceImplementation.templateInformation("testTemplate"));
            assertAll(() -> assertNotNull(exception),
                    () -> assertEquals(NO_SURVEY_CREATED, exception.getMessage()));
        }
    }


}