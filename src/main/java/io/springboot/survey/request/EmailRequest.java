package io.springboot.survey.request;

import com.google.gson.GsonBuilder;
import io.springboot.survey.annotation.Email;
import io.springboot.survey.annotation.EmptyNotNull;
import io.springboot.survey.response.ListOfMails;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.*;

@Getter
@Setter
@NoArgsConstructor
public class EmailRequest {
    @EmptyNotNull(message = SENDER_MAIL_NOT_NULL)
    @Email
    private String senderMail;
    @EmptyNotNull(message = SURVEY_NOT_NULL)
    private String surveyName;
    @NotEmpty(message = MAIL_LIST_NOT_NULL)
    private List<ListOfMails> mailsList;
    private long expirationDate;

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
