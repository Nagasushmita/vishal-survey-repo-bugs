package io.springboot.survey.service;

import io.springboot.survey.pojo.mail.MailParam;
import io.springboot.survey.request.EmailRequest;
import io.springboot.survey.response.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public interface MailService {
    ResponseEntity<ResponseMessage> sendEmail(EmailRequest emailRequest, boolean isReminder);
    void setStatus(Object object);
    void sendMail(MailParam mailParam);
}
