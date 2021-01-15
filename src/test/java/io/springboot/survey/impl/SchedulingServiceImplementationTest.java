package io.springboot.survey.impl;

import io.springboot.survey.request.ScheduleEmailRequest;
import io.springboot.survey.response.ListOfMails;
import io.springboot.survey.response.ScheduleEmailResponse;
import io.springboot.survey.service.MailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@Tag("Service")
class SchedulingServiceImplementationTest {

    @InjectMocks
    SchedulingServiceImplementation schedulingServiceImplementation;
    @Mock
    MailService mailService;
    @Mock
    Scheduler scheduler;

    @Nested
    @DisplayName("QuarterlySchedule Email Test")
    class ScheduleEmailTest {
        @Test
        @DisplayName("Bad Request")
        void badRequest() {
            ScheduleEmailRequest scheduleEmailRequest =new ScheduleEmailRequest();
            scheduleEmailRequest.setDateTime(System.currentTimeMillis()-(2*86400000));
            ResponseEntity<ScheduleEmailResponse> response=schedulingServiceImplementation.scheduleEmail(scheduleEmailRequest);
            assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        }

        @Test
        @DisplayName("Frequency - Once")
        void once() throws SchedulerException {
            Date date=new Date();
            Timestamp timestamp=new Timestamp(date.getTime());
            ScheduleEmailRequest scheduleEmailRequest =getMailRequest();
            scheduleEmailRequest.setFrequency("Once");
            doNothing().when(mailService).setStatus(scheduleEmailRequest);
            when(scheduler.scheduleJob(Mockito.any(),Mockito.any())).thenReturn(timestamp);
            ResponseEntity<ScheduleEmailResponse> response=schedulingServiceImplementation.scheduleEmail(scheduleEmailRequest);
            assertEquals(HttpStatus.OK,response.getStatusCode());
        }

        @Test
        @DisplayName("Frequency - Weekly")
        void weekly() throws SchedulerException {
            Date date=new Date();
            Timestamp timestamp=new Timestamp(date.getTime());
            ScheduleEmailRequest scheduleEmailRequest =getMailRequest();
            scheduleEmailRequest.setFrequency("Weekly");
            doNothing().when(mailService).setStatus(scheduleEmailRequest);
            when(scheduler.scheduleJob(Mockito.any(),Mockito.any())).thenReturn(timestamp);
            ResponseEntity<ScheduleEmailResponse> response=schedulingServiceImplementation.scheduleEmail(scheduleEmailRequest);
            assertEquals(HttpStatus.OK,response.getStatusCode());
        }
        @Test
        @DisplayName("Frequency - Monthly")
        void monthly() throws SchedulerException {
            Date date=new Date();
            Timestamp timestamp=new Timestamp(date.getTime());
            ScheduleEmailRequest scheduleEmailRequest =getMailRequest();
            scheduleEmailRequest.setFrequency("Monthly");
            doNothing().when(mailService).setStatus(scheduleEmailRequest);
            when(scheduler.scheduleJob(Mockito.any(),Mockito.any())).thenReturn(timestamp);
            ResponseEntity<ScheduleEmailResponse> response=schedulingServiceImplementation.scheduleEmail(scheduleEmailRequest);
            assertEquals(HttpStatus.OK,response.getStatusCode());
        }
        @Test
        @DisplayName("Frequency - Quarterly")
        void quarterly() throws SchedulerException {
            Date date=new Date();
            Timestamp timestamp=new Timestamp(date.getTime());
            ScheduleEmailRequest scheduleEmailRequest =getMailRequest();
            scheduleEmailRequest.setFrequency("Quarterly");
            doNothing().when(mailService).setStatus(scheduleEmailRequest);
            when(scheduler.scheduleJob(Mockito.any(),Mockito.any())).thenReturn(timestamp);
            ResponseEntity<ScheduleEmailResponse> response=schedulingServiceImplementation.scheduleEmail(scheduleEmailRequest);
            assertEquals(HttpStatus.OK,response.getStatusCode());
        }
        @Test
        @DisplayName("Frequency - Yearly")
        void yearly() throws SchedulerException {
            Date date=new Date();
            Timestamp timestamp=new Timestamp(date.getTime());
            ScheduleEmailRequest scheduleEmailRequest =getMailRequest();
            scheduleEmailRequest.setFrequency("Yearly");
            doNothing().when(mailService).setStatus(scheduleEmailRequest);
            when(scheduler.scheduleJob(Mockito.any(),Mockito.any())).thenReturn(timestamp);
            ResponseEntity<ScheduleEmailResponse> response=schedulingServiceImplementation.scheduleEmail(scheduleEmailRequest);
            assertEquals(HttpStatus.OK,response.getStatusCode());
        }

        @Test
        @DisplayName("Default Case")
        void defaultCase() {
            ScheduleEmailRequest scheduleEmailRequest =getMailRequest();
            scheduleEmailRequest.setFrequency("default");
            ResponseEntity<ScheduleEmailResponse> response=schedulingServiceImplementation.scheduleEmail(scheduleEmailRequest);
            assertEquals(HttpStatus.OK,response.getStatusCode());
        }

        @Test
        @DisplayName("Error")
        void exception() {
            ScheduleEmailRequest scheduleEmailRequest =getMailRequest();
            ResponseEntity<ScheduleEmailResponse> response=schedulingServiceImplementation.scheduleEmail(scheduleEmailRequest);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
        }
    }
    private ScheduleEmailRequest getMailRequest()
    {
        ListOfMails listOfMails= Mockito.mock(ListOfMails.class);
        ScheduleEmailRequest scheduleEmailRequest =new ScheduleEmailRequest();
        scheduleEmailRequest.setDateTime(System.currentTimeMillis()+(2*86400000));
        scheduleEmailRequest.setEndDateTime(System.currentTimeMillis()+(5*86400000));
        scheduleEmailRequest.setMailsList(new ArrayList<>(Collections.singletonList(listOfMails)));
        scheduleEmailRequest.setExpiryHours(1499073941L);
        scheduleEmailRequest.setTimeZone(ZoneId.of("Asia/Kolkata"));
        return scheduleEmailRequest;
    }
}