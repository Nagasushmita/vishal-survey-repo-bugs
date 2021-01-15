package io.springboot.survey.service;

import org.springframework.stereotype.Service;

@Service
public interface NotificationService {

    String getOtp();
    void sendNotification(String email) ;

}
