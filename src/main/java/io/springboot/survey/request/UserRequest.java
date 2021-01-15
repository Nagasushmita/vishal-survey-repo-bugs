package io.springboot.survey.request;

import com.google.gson.GsonBuilder;
import io.springboot.survey.annotation.Email;
import io.springboot.survey.annotation.EmptyNotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.*;
import static io.springboot.survey.utils.Constants.ValidationConstant.INVALID_CREATOR_EMAIL;

@Getter
@Setter
@NoArgsConstructor
public class UserRequest {
    @EmptyNotNull(message = TEAM_NAME_NOT_NULL)
    private String teamName;
    @EmptyNotNull(message = EMAIL_NOT_NULL)
    @Email
    private String email;
    @EmptyNotNull(message = SURVEY_NOT_NULL)
    private String surveyName;
    @EmptyNotNull(message = CREATOR_EMAIL_NOT_NULL)
    @Email(message = INVALID_CREATOR_EMAIL)
    private String creatorEmail;

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}

