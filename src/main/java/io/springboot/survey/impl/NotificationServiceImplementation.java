package io.springboot.survey.impl;

import io.springboot.survey.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

import static io.springboot.survey.utils.Constants.CommonConstant.*;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;

@Component
public class NotificationServiceImplementation implements NotificationService {


    private final JavaMailSender javaMailSender;

     private String otp;
     private final Random random=new SecureRandom();
     private static final Logger logger= LoggerFactory.getLogger(NotificationServiceImplementation.class.getSimpleName());

    /**
     * get otp
     *
     * @return String
     */
    @Override
    public String getOtp() {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return otp;
    }

    /**
     * Set otp
     *
     * @param otp : otp
     */
    private void setOtp(String otp) {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        this.otp = otp;
    }

    @Autowired
    public NotificationServiceImplementation(JavaMailSender javaMailSender)
    {
        this.javaMailSender=javaMailSender;
    }

    /**
     * generate otp
     *
     * @return String
     */
    private String otpGeneration()
    {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return String.format(STRING_FORMAT, random.nextInt(SHORT_BOUND));
    }

    /**
     * send otp to the user email.
     *
     * @param email : email of the user.
     */
    @Override
    public void sendNotification(String email) {
        logger.info(STARTING_METHOD_EXECUTION);
        String generatedOtp = otpGeneration();
        setOtp(generatedOtp);
        SimpleMailMessage mail =new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject(OTP);
        mail.setText(OTP_IS+ otp);
        logger.info(LOGGER_OTP_MESSAGE,otp);
        javaMailSender.send(mail);
        logger.info(EXITING_METHOD_EXECUTION);
    }
}
