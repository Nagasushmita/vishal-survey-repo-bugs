package io.springboot.survey.utils;

import io.springboot.survey.models.SurveyModel;
import io.springboot.survey.pojo.mail.MailParam;
import io.springboot.survey.repository.SurveyRepo;
import io.springboot.survey.repository.UserRepo;
import io.springboot.survey.service.MailService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class EmailJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);

    private final MailService mailService;
    private final UserRepo userRepo;
    private final SurveyRepo surveyRepo;

    public EmailJob(MailService mailService, UserRepo userRepo, SurveyRepo surveyRepo) {
        this.mailService = mailService;
        this.userRepo = userRepo;
        this.surveyRepo = surveyRepo;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext){
        logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String surveyName = jobDataMap.getString("surveyName");
        String sender = jobDataMap.getString("senderEmail");
        String target = jobDataMap.getString("listOfMails");
        String teamName = jobDataMap.getString("listOfTeamName");
        List<String> recipientList = new ArrayList<>(Arrays.asList(target.split(".")));
        List<String> teamNameList = new ArrayList<>(Arrays.asList(teamName.split(",")));
        Long expiryHours=jobDataMap.getLong("expiryTime");
        try {
            updateExpiryTime(expiryHours,surveyName,sender);
            int i=0;
            for(String recipient:recipientList) {
                mailService.sendMail(new MailParam(sender, recipient, teamNameList.get(i), surveyName));
                i++;
            }
        }
        catch (Exception e) {
            logger.info(e.getMessage());
        }
    }
    private void updateExpiryTime(Long expiryHours, String surveyName, String creatorEmail){
        SurveyModel surveyModel=userRepo.getSurveyBySurveyNameAndCreatorEmail(surveyName,creatorEmail);
        surveyModel.setExpirationDate(System.currentTimeMillis()+expiryHours);
        surveyRepo.save(surveyModel);
    }
}


