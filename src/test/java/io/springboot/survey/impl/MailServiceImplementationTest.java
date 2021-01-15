package io.springboot.survey.impl;

import io.springboot.survey.exception.APIException;
import io.springboot.survey.mapper.SurveyByNameAndCreatorId;
import io.springboot.survey.models.SurveyModel;
import io.springboot.survey.models.SurveyStatusModel;
import io.springboot.survey.pojo.mail.MailParam;
import io.springboot.survey.repository.SurveyRepo;
import io.springboot.survey.repository.SurveyStatusRepo;
import io.springboot.survey.repository.TeamRepo;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.request.EmailRequest;
import io.springboot.survey.request.ScheduleEmailRequest;
import io.springboot.survey.response.ListOfMails;
import io.springboot.survey.response.ResponseMessage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;

import javax.mail.internet.MimeMessage;
import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
class MailServiceImplementationTest {

    @InjectMocks
    MailServiceImplementation mailServiceImplementation;

    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    ITemplateEngine templateEngine;
    @Mock
    UserRepo userRepo;
    @Mock
    TeamRepo teamRepo;
    @Mock
    SurveyRepo surveyRepo;
    @Mock
    SurveyStatusRepo surveyStatusRepo;

    @Nested
    @DisplayName("Send Mail Test")
    class SendMailTest {
        @Test
        @DisplayName("Success")
        void sendMail() {
            MailServiceImplementation serviceImplementation = Mockito.mock(MailServiceImplementation.class);
            MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
            Object[] obj = new Object[2];
            obj[1] = "teamName";
            Tuple tuple = Mockito.mock(Tuple.class);
            when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(userRepo.getUserByEmail(Mockito.anyString())).thenReturn(tuple);
            when(templateEngine.process(Mockito.anyString(), Mockito.any())).thenReturn("Template");
            when(surveyRepo.getSurveyIdAndLink(Mockito.anyString(), Mockito.anyInt())).thenReturn(obj);
            doAnswer(invocationOnMock -> {
                Object arg0 = invocationOnMock.getArgument(0);
                assertNotNull(arg0);
                return null;
            }).when(serviceImplementation).sendMail(Mockito.any(MailParam.class));
            serviceImplementation.sendMail(new MailParam("dummy@nineleaps.com", "test", "testTeam", "testSurvey"));
            mailServiceImplementation.sendMail(new MailParam("dummy@nineleaps.com", "test", "", "testSurvey"));
        }
        @Test
        @DisplayName("Exception")
        void exception() {
            MailParam mailParam=new MailParam("dummy@nineleaps.com", "test", "", "testSurvey");
            Exception exception = assertThrows(Exception.class, () -> mailServiceImplementation.sendMail(mailParam));
            assertNotNull(exception);
        }

        @Test
        @DisplayName("Recipient Email Null")
        void recipientNull() {
            MailServiceImplementation serviceImplementation = Mockito.mock(MailServiceImplementation.class);
            doAnswer(invocationOnMock -> {
                Object arg0 = invocationOnMock.getArgument(0);
                assertNotNull(arg0);
                return null;
            }).when(serviceImplementation).sendMail(Mockito.any(MailParam.class));
            serviceImplementation.sendMail(new MailParam("dummy@nineleaps.com", "", "testTeam", "testSurvey"));
            mailServiceImplementation.sendMail(new MailParam("dummy@nineleaps.com", "", "", "testSurvey"));
        }
    }
    @Test
    @DisplayName("Send Email Test ")
    void sendEmail() {
        SurveyModel surveyModel=Mockito.mock(SurveyModel.class);
        SurveyByNameAndCreatorId survey=Mockito.mock(SurveyByNameAndCreatorId.class);
        EmailRequest request=new EmailRequest();
        ListOfMails listOfMails=new ListOfMails();
        listOfMails.setEmail("dummy@nineleaps.com");
        listOfMails.setTeamName("dummyTeam");
        request.setMailsList(new ArrayList<>(Collections.singletonList(listOfMails)));
        MimeMessage mimeMessage= Mockito.mock(MimeMessage.class);
        Object [] obj=new Object[2];
        obj[0]="teamName";
        Tuple tuple=Mockito.mock(Tuple.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(userRepo.getUserByEmail(null)).thenReturn(tuple);
        when(userRepo.getUserByEmail(Mockito.anyString())).thenReturn(tuple);
        when(templateEngine.process(Mockito.anyString(),Mockito.any())).thenReturn("Template");
        when(surveyRepo.getSurveyIdAndLink(null,0)).thenReturn(obj);
        when( surveyRepo.findBySurveyNameAndCreatorUserIdAndArchivedFalse(null,0)).thenReturn(surveyModel);
        when(userRepo.getSurveyDataByNameAndId(null,null,false)).thenReturn(survey);
        when(teamRepo.getTeamId(Mockito.anyString())).thenReturn(1);
        SurveyStatusModel surveyStatusModel=Mockito.mock(SurveyStatusModel.class);
        when(surveyStatusRepo.save(Mockito.any())).thenReturn(surveyStatusModel);
        ResponseEntity<ResponseMessage> response1=mailServiceImplementation.sendEmail(request,false);
        ResponseEntity<ResponseMessage> response2=mailServiceImplementation.sendEmail(request,true);
        when(userRepo.getUserByEmail(Mockito.anyString())).thenReturn(null);
        Exception exception=assertThrows(APIException.class,()->mailServiceImplementation.sendEmail(request,true));
        assertEquals(HttpStatus.OK,response1.getStatusCode());
        assertAll(()->assertEquals(HttpStatus.OK,response1.getStatusCode()),
                ()->assertEquals(HttpStatus.OK,response2.getStatusCode()),
                ()->assertNotNull(exception));

    }


    @DisplayName("Set Status Test")
    @Nested
    class SetStatusTest {
        @BeforeEach
        void setUp() {
            SurveyStatusModel surveyStatusModel=Mockito.mock(SurveyStatusModel.class);
            SurveyModel surveyModel = Mockito.mock(SurveyModel.class);
            SurveyByNameAndCreatorId survey = Mockito.mock(SurveyByNameAndCreatorId.class);
            when(surveyRepo.findBySurveyNameAndCreatorUserIdAndArchivedFalse(null, 1)).thenReturn(surveyModel);
            when(userRepo.getSurveyDataByNameAndId(null, null, false)).thenReturn(survey);
            when(userRepo.getUserIdByUserEmail(null)).thenReturn(1);
            when(surveyStatusRepo.save(Mockito.any())).thenReturn(surveyStatusModel);
        }
        @Test
        @DisplayName("Mail Request")
        void setStatus() {
            MailServiceImplementation serviceImplementation=Mockito.mock(MailServiceImplementation.class);
            ScheduleEmailRequest request = new ScheduleEmailRequest();
            ListOfMails listOfMails = new ListOfMails();
            listOfMails.setEmail("dummy@nineleaps.com");
            listOfMails.setTeamName("dummyTeam");
            request.setMailsList(new ArrayList<>(Collections.singletonList(listOfMails)));
            doAnswer(invocationOnMock -> {
                Object arg0= invocationOnMock.getArgument(0);
                assertNotNull(arg0);
                return null;
            }).when(serviceImplementation).setStatus(Mockito.any());
            serviceImplementation.setStatus(request);
            mailServiceImplementation.setStatus(request);
        }
        @Test
        @DisplayName("Email Request")
        void emailRequest() {
            MailServiceImplementation serviceImplementation=Mockito.mock(MailServiceImplementation.class);
            EmailRequest request = new EmailRequest();
            ListOfMails listOfMails = new ListOfMails();
            listOfMails.setEmail("dummy@nineleaps.com");
            listOfMails.setTeamName(null);
            request.setMailsList(new ArrayList<>(Collections.singletonList(listOfMails)));
                doAnswer(invocationOnMock -> {
                Object arg0= invocationOnMock.getArgument(0);
                assertNotNull(arg0);
                return null;
            }).when(serviceImplementation).setStatus(Mockito.any());
            serviceImplementation.setStatus(request);
            mailServiceImplementation.setStatus(request);

        }
    }
}