package io.springboot.survey.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
class NotificationServiceImplementationTest {

    @InjectMocks
    NotificationServiceImplementation notificationServiceImplementation;
    @Mock
    JavaMailSender javaMailSender;

    @Test
    @DisplayName("Get Otp Test")
    void getOtp() {
        String response=notificationServiceImplementation.getOtp();
        assertNull(response);
    }

    @Test
    @DisplayName("Send Notification Test")
    void sendNotification() {
        NotificationServiceImplementation serviceImplementation=Mockito.mock(NotificationServiceImplementation.class);
        doNothing().when(javaMailSender).send(Mockito.any(SimpleMailMessage.class));
        doAnswer(invocationOnMock -> {
            Object arg0= invocationOnMock.getArgument(0);
            assertNotNull(arg0);
            return null;
        }).when(serviceImplementation).sendNotification(Mockito.anyString());
        serviceImplementation.sendNotification("dummy@nineleps.com");
        notificationServiceImplementation.sendNotification("dummy@nineleps.com");
    }
}