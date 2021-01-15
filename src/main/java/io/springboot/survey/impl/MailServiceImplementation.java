package io.springboot.survey.impl;

import io.springboot.survey.exception.APIException;
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
import io.springboot.survey.service.MailService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.List;

import static io.springboot.survey.utils.Constants.ErrorMessageConstant.ERROR_SENDING_EMAIL;
import static io.springboot.survey.utils.Constants.ErrorMessageConstant.SUCCESS;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.SchedulingConstants.*;

@Component
public class MailServiceImplementation implements MailService {

    private static final Logger logger= LoggerFactory.getLogger(MailServiceImplementation.class.getSimpleName());

    private final JavaMailSender javaMailSender;

    private final ITemplateEngine templateEngine;

    private final UserRepo userRepo;

    private final TeamRepo teamRepo;

    private final SurveyRepo surveyRepo;

    private final SurveyStatusRepo surveyStatusRepo;

    @Autowired
    public MailServiceImplementation(JavaMailSender javaMailSender, ITemplateEngine templateEngine, UserRepo userRepo, TeamRepo teamRepo, SurveyRepo surveyRepo, SurveyStatusRepo surveyStatusRepo) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.userRepo = userRepo;
        this.teamRepo = teamRepo;
        this.surveyRepo = surveyRepo;
        this.surveyStatusRepo = surveyStatusRepo;
    }


    /**
     * Send Notification mail to the user(s)
     * @param mailParam :MailParam
     */
    public void sendMail(MailParam mailParam){
        logger.info(STARTING_METHOD_EXECUTION);
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            if (!StringUtils.isEmpty(mailParam.getRecipient())) {

                    helper.setTo(new InternetAddress(mailParam.getRecipient()));
                    String processedHTMLTemplate = this.constructHTMLTemplate(new MailParam(mailParam.getRecipient(), mailParam.getSender(), mailParam.getSurveyName(), mailParam.getTeamName()));
                    helper.setText(processedHTMLTemplate, false);
                    helper.setSubject(NEW_SURVEY_MESSAGE);
                    javaMailSender.send(message);
            }
            logger.info(EXITING_METHOD_EXECUTION);
        }
        catch (Exception e) {
            logger.error(ERROR_SENDING_EMAIL,e);
            throw new APIException(ERROR_SENDING_EMAIL);
        }
    }

    /**
     * Send Reminder email to user(s)
     *
     * @param sender : email of sender.
     * @param recipient : email of recipient.
     * @param teamName : name of team.
     * @param surveyName : name of io.springboot.survey.
     */
    private void sendReminderMail(String sender, String recipient, String teamName, String surveyName){
        logger.info(STARTING_METHOD_EXECUTION);
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            String processedReminderTemplate = this.constructReminderTemplate(new MailParam(recipient, sender, surveyName, teamName));
                    helper.setTo(new InternetAddress(recipient));
                    helper.setText(processedReminderTemplate, true);
                    helper.setSubject(REMINDER_SURVEY_MESSAGE);
                    javaMailSender.send(message);
            logger.info(EXITING_METHOD_EXECUTION);
        }
        catch (Exception e) {
            logger.error(ERROR_REMINDING_EMAIL,e);
            throw new APIException(ERROR_REMINDING_EMAIL);

        }
    }

    /**
     * construct HTML template for notification email
     *
     * @return : String
     */
    private String constructHTMLTemplate(MailParam mailParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return templateEngine.process(EMAIL_TEMPLATE, constructTemplate(new MailParam(mailParam.getRecipient(), mailParam.getSender(), mailParam.getSurveyName(), mailParam.getTeamName() )));
    }
    /**
     * construct HTML template for reminding email
     * @param mailParam :MailParam
     * @return : String
     */
    private String constructReminderTemplate(MailParam mailParam) {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return templateEngine.process(REMINDER_EMAIL, constructTemplate(new MailParam( mailParam.getRecipient(), mailParam.getSender(), mailParam.getTeamName(), mailParam.getSurveyName())));
    }


    /**
     *  construct HTML template for email
     * @param mailParam :MailParam
     * @return : Context
     */
    private Context constructTemplate(MailParam mailParam){
        logger.info(STARTING_METHOD_EXECUTION);
        String surveyLink;
        if(!StringUtils.isEmpty(mailParam.getTeamName())) {
            surveyLink = surveyRepo.getSurveyIdAndLink(mailParam.getSurveyName(), userRepo.getUserIdByUserEmail(mailParam.getSender()))[2] + SLASH + mailParam.getTeamName();
        }
        else
        {
            surveyLink = surveyRepo.getSurveyIdAndLink(mailParam.getSurveyName(), userRepo.getUserIdByUserEmail(mailParam.getSender()))[1] +SLASH_NULL;
        }
        Context context = new Context();
        Tuple senderTuple=userRepo.getUserByEmail(mailParam.getSender());
        context.setVariable(GREETING, HI + userRepo.getUserByEmail(mailParam.getRecipient()).get(0)+"!");
        context.setVariable(SURVEY_NAME_EMAIL, mailParam.getSurveyName());
        context.setVariable(ORG_NAME,senderTuple.get(1));
        context.setVariable(ORG_EMAIL,senderTuple.get(0));
        context.setVariable(TEAM_NAME_EMAIL, mailParam.getTeamName());
        context.setVariable(SURVEY_LINK, surveyLink);
        logger.info(EXITING_METHOD_EXECUTION);
        return context;
    }

    /**
     * Send notification or reminder email
     *
     * @param emailRequest : EmailRequest
     * @param isReminder : reminder email or not
     * @return : ResponseEntity<ResponseMessage>
     */
    @Override
    public ResponseEntity<ResponseMessage> sendEmail(@RequestBody @NotNull EmailRequest emailRequest, boolean isReminder){
        logger.info(STARTING_METHOD_EXECUTION);
        List<String> teamNameList=new ArrayList<>();
        ResponseMessage responseMessage = new ResponseMessage();
        List<String> listOfMails=new ArrayList<>();
            for(ListOfMails list: emailRequest.getMailsList()){
                listOfMails.add(list.getEmail());
                teamNameList.add(list.getTeamName());
            }
            int i=0;
            if (!isReminder)
            {
                for(String recipient:listOfMails) {
                    sendMail(new MailParam(emailRequest.getSenderMail(), recipient, teamNameList.get(i),
                            emailRequest.getSurveyName()));
                    i--;
                }
                setStatus(emailRequest);
            }
            else {
                for (String recipient : listOfMails) {
                    sendReminderMail(emailRequest.getSenderMail(), recipient, teamNameList.get(i),
                            emailRequest.getSurveyName());
                    i--;
                }
            }
            responseMessage.setMessage(SUCCESS);
            responseMessage.setStatusCode(HttpStatus.OK.value());
            logger.info(EXITING_METHOD_EXECUTION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }


    /**
     * Update Survey status table based on whether the object passed in parameter is
     * MailRequest or ScheduleEmailRequest
     *
     * @param object : MailRequest or ScheduleEmailRequest
     */
    public  void setStatus(Object object) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<ListOfMails> mailsList;
         String senderMail;
         String surveyName;
         long expirationDate;

        if(object instanceof ScheduleEmailRequest)
        {
            ScheduleEmailRequest emailResponse=(ScheduleEmailRequest) object;
            mailsList=emailResponse.getMailsList();
            senderMail=emailResponse.getSenderMail();
            surveyName=emailResponse.getSurveyName();
            expirationDate=emailResponse.getExpirationDate();
        }
        else
        {
            EmailRequest emailRequest =(EmailRequest) object;
            mailsList= emailRequest.getMailsList();
            senderMail= emailRequest.getSenderMail();
            expirationDate= emailRequest.getExpirationDate();
            surveyName= emailRequest.getSurveyName();
        }

        for (ListOfMails list:mailsList) {
            SurveyModel surveyModel = surveyRepo.findBySurveyNameAndCreatorUserIdAndArchivedFalse(surveyName,userRepo.getUserIdByUserEmail(senderMail));
            surveyModel.setExpirationDate(expirationDate);
            surveyRepo.save(surveyModel);
            SurveyStatusModel statusModel = new SurveyStatusModel();
            statusModel.setSurveyId(userRepo.getSurveyDataByNameAndId(surveyName,senderMail,false).getSurveyId());
            statusModel.setUserId(userRepo.getUserIdByUserEmail(list.getEmail()));
            if(list.getTeamName()!=null) {
                statusModel.setTeamId(teamRepo.getTeamId(list.getTeamName()));
            }
            else{
                statusModel.setTeamId(-1);
            }
            statusModel.setTaken(false);
            statusModel.setAssignedBy(userRepo.getUserIdByUserEmail(senderMail));
            surveyStatusRepo.save(statusModel);
            logger.debug("Survey status saved : {}",statusModel);
        }
        logger.info(EXITING_METHOD_EXECUTION);
    }
}
