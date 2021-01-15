package io.springboot.survey.request;

import com.google.gson.GsonBuilder;
import io.springboot.survey.annotation.Email;
import io.springboot.survey.annotation.EmptyNotNull;
import io.springboot.survey.response.ListOfMails;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.time.ZoneId;
import java.util.List;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.*;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleEmailRequest {

    @EmptyNotNull(message = SENDER_MAIL_NOT_NULL)
    @Email
    private String senderMail;
    @EmptyNotNull(message = SURVEY_NOT_NULL)
    private String surveyName;
    @NotEmpty(message = MAIL_LIST_NOT_NULL)
    private List<ListOfMails> mailsList;
    private boolean reminder;
    private long expirationDate;
    private long dateTime;
    private long endDateTime;
    @EmptyNotNull(message = FREQUENCY_NOT_NULL)
    private String frequency;
    private ZoneId timeZone;
    private String surveyLink;
    private Long expiryHours;

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
