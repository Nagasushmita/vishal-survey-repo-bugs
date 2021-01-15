package io.springboot.survey.impl;

import io.springboot.survey.pojo.scheduling.WeeklyMonthlyTriggerParam;
import io.springboot.survey.request.ScheduleEmailRequest;
import io.springboot.survey.response.ListOfMails;
import io.springboot.survey.response.ScheduleEmailResponse;
import io.springboot.survey.service.MailService;
import io.springboot.survey.service.SchedulingService;
import io.springboot.survey.utils.EmailJob;
import org.jetbrains.annotations.NotNull;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static io.springboot.survey.utils.Constants.CommonConstant.STRING_COMMA;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.NullEmptyConstant.DATE_TIME_CONDITION;
import static io.springboot.survey.utils.Constants.SchedulingConstants.*;
import static io.springboot.survey.utils.Constants.ValidationConstant.SENDER_EMAIL;
import static io.springboot.survey.utils.Constants.ValidationConstant.SURVEY_NAME;

@Component
public class SchedulingServiceImplementation implements SchedulingService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingServiceImplementation.class);

    private final MailService mailService;

    private final Scheduler scheduler;

    public SchedulingServiceImplementation(MailService mailService, Scheduler scheduler) {
        this.mailService = mailService;
        this.scheduler = scheduler;
    }


    /**
     * Build job detail for task to be scheduled
     *
     * @param scheduleEmailRequest : ScheduleEmailRequest
     * @return : JobDetail
     */
    private JobDetail buildJobDetail(@NotNull ScheduleEmailRequest scheduleEmailRequest) {
        logger.info(STARTING_METHOD_EXECUTION);
        List<String> teamNameList=new ArrayList<>();
        List<String> listOfMails=new ArrayList<>();
        for(ListOfMails list:scheduleEmailRequest.getMailsList()){
            listOfMails.add(list.getEmail());
            teamNameList.add(list.getTeamName());
        }
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(LIST_OF_MAILS, String.join(STRING_COMMA, listOfMails));
        jobDataMap.put(LIST_OF_TEAM_NAME, String.join(STRING_COMMA, teamNameList));
        jobDataMap.put(REMINDER, scheduleEmailRequest.isReminder());
        jobDataMap.put(SURVEY_LINK_JOB, scheduleEmailRequest.getSurveyLink());
        jobDataMap.put(EXPIRY_HOURS, Long.toString(scheduleEmailRequest.getExpiryHours()));
        logger.info(EXITING_METHOD_EXECUTION);
        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), EMAIL_JOBS)
                .withDescription(SEND_EMAIL_JOBS)
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    /**
     * Trigger for weekly job
     * @param param :WeeklyMonthlyTriggerParam
     * @return : Trigger
     */
    private Trigger buildJobTriggerWeekly(WeeklyMonthlyTriggerParam param) {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return TriggerBuilder.newTrigger()
                .forJob(param.getJobDetail())
                .withIdentity(param.getJobDetail().getKey().getName(), WEEKLY_EMAIL_TRIGGER)
                .withDescription(EMAIL_WEEKLY)
                .startAt(Date.from(param.getStartAt().toInstant()))
                .withSchedule(CronScheduleBuilder.weeklyOnDayAndHourAndMinute(param.getNumber(), param.getHour(), param.getMinute())).
                        endAt(Date.from(param.getEndAt().toInstant()
                        )).build();
    }

    /**
     * Trigger for monthly job
     * @param param :WeeklyMonthlyTriggerParam
     * @return : Trigger
     */
    private Trigger buildJobTriggerMonthly(WeeklyMonthlyTriggerParam param) {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return TriggerBuilder.newTrigger()
                .forJob(param.getJobDetail())
                .withIdentity(param.getJobDetail().getKey().getName(), MONTHLY_EMAIL_TRIGGER)
                .withDescription(EMAIL_MONTHLY)
                .startAt(Date.from(param.getStartAt().toInstant()))
                .withSchedule(CronScheduleBuilder.monthlyOnDayAndHourAndMinute(param.getNumber(), param.getHour(), param.getMinute())).
                        endAt(Date.from(param.getEndAt().toInstant()
                        )).build();
    }
    /**
     * Trigger for quarterly job
     *
     * @param jobDetail : JobDetail.
     * @param startAt : ZonedDateTime --> start date of job.
     * @param endAt :  ZonedDateTime --> end date of job.
     * @return : Trigger
     */
    private Trigger buildJobTriggerQuarterly(JobDetail jobDetail, @NotNull ZonedDateTime startAt, @NotNull ZonedDateTime endAt) {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(),QUARTERLY_EMAIL_TRIGGER)
                .withDescription(EMAIL_QUARTERLY)
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(CronScheduleBuilder.cronSchedule(CRON_QUARTERLY)).endAt(Date.from(endAt.
                        toInstant()
                )).build();
    }
    /**
     * Trigger for yearly job
     *
     * @param jobDetail : JobDetail.
     * @param startAt : ZonedDateTime --> start date of job.
     * @param endAt :  ZonedDateTime --> end date of job.
     * @return : Trigger
     */

    private Trigger buildJobTriggerYearly(JobDetail jobDetail, @NotNull ZonedDateTime startAt, @NotNull ZonedDateTime endAt) {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), YEARLY_EMAIL_TRIGGER)
                .withDescription(EMAIL_YEARLY)
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(CronScheduleBuilder.cronSchedule(CRON_YEARLY)).endAt(Date.from(endAt.
                        toInstant()
                )).build();
    }
    /**
     * Trigger for one time job
     *
     * @param jobDetail : JobDetail.
     * @param startAt : ZonedDateTime --> start date of job.
     * @return : Trigger
     */

    private Trigger buildJobTrigger(JobDetail jobDetail, @NotNull ZonedDateTime startAt) {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), EMAIL_TRIGGER)
                .withDescription(SEND_EMAIL_ONCE)
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().
                        withMisfireHandlingInstructionIgnoreMisfires()).
                        build();
    }

    /**
     * schedule Email for different frequency
     *
     * @param scheduleEmailRequest : ScheduleEmailRequest
     * @return ResponseEntity<ScheduleEmailResponse>
     */
    @Override
    public ResponseEntity<ScheduleEmailResponse> scheduleEmail(@NotNull ScheduleEmailRequest scheduleEmailRequest) {
        logger.info(STARTING_METHOD_EXECUTION);
        try {
            LocalDateTime dateTimestamp = Instant.ofEpochMilli(scheduleEmailRequest.getDateTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime endDateTimestamp =  Instant.ofEpochMilli(scheduleEmailRequest.getEndDateTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime currentTime =  Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime();

            if (dateTimestamp.isBefore(currentTime)) {
                ScheduleEmailResponse scheduleEmailResponse = new ScheduleEmailResponse(false,
                        DATE_TIME_CONDITION);
                logger.info(EXITING_METHOD_EXECUTION);
                return ResponseEntity.badRequest().body(scheduleEmailResponse);
            }


            int dayOfWeek = dateTimestamp.getDayOfWeek().getValue();
            int hour = dateTimestamp.getHour();
            int minute = dateTimestamp.getMinute();
            int dayOfMonth = dateTimestamp.getDayOfMonth();

            ZonedDateTime dateTime = ZonedDateTime.of(dateTimestamp, scheduleEmailRequest.getTimeZone());
            ZonedDateTime endDateTime = ZonedDateTime.of(endDateTimestamp, scheduleEmailRequest.getTimeZone());


            JobDetail jobDetail = buildJobDetail(scheduleEmailRequest);

            mailService.setStatus(scheduleEmailRequest);

            switch (scheduleEmailRequest.getFrequency()) {
                case ONCE:
                    Trigger trigger = buildJobTrigger(jobDetail, dateTime);
                    scheduler.scheduleJob(jobDetail, trigger);
                    logger.debug("Once");
                    break;
                case WEEKLY:
                    Trigger weeklyTrigger = buildJobTriggerWeekly(new WeeklyMonthlyTriggerParam(jobDetail, dateTime, endDateTime, dayOfWeek,
                            hour, minute));
                    logger.debug("Weekly");
                    scheduler.scheduleJob(jobDetail, weeklyTrigger);
                    break;
                case MONTHLY:
                    Trigger monthlyTrigger = buildJobTriggerMonthly(new WeeklyMonthlyTriggerParam(jobDetail, dateTime, endDateTime, dayOfMonth,
                            hour, minute));
                    logger.debug("Monthly");
                    scheduler.scheduleJob(jobDetail, monthlyTrigger);
                    break;
                case QUARTERLY:
                    Trigger quarterlyTrigger = buildJobTriggerQuarterly(jobDetail, dateTime, endDateTime);
                    scheduler.scheduleJob(jobDetail, quarterlyTrigger);
                    logger.debug("Quarterly");
                    break;
                case YEARLY:
                    Trigger yearlyTrigger = buildJobTriggerYearly(jobDetail, dateTime, endDateTime);
                    scheduler.scheduleJob(jobDetail, yearlyTrigger);
                    logger.debug("Yearly");
                    break;
                default:
            }
            ScheduleEmailResponse scheduleEmailResponse = new ScheduleEmailResponse(true,
                    jobDetail.getKey().getName(), jobDetail.getKey().getGroup(),
                    EMAIL_SCHEDULED_SUCCESSFULLY);
            logger.info(EXITING_METHOD_EXECUTION);
            return ResponseEntity.ok(scheduleEmailResponse);

        } catch (Exception ex) {
            logger.error("Error occurred while scheduling mail :: ",ex);
            ScheduleEmailResponse scheduleEmailResponse = new ScheduleEmailResponse(false,
                    ERROR_EMAIL_SCHEDULING);
            logger.info(EXITING_METHOD_EXECUTION);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(scheduleEmailResponse);
        }
    }
}
