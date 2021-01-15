package io.springboot.survey.request;

import com.google.gson.GsonBuilder;
import io.springboot.survey.annotation.Email;
import io.springboot.survey.annotation.EmptyNotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.*;

@Getter
@Setter
@NoArgsConstructor
public class CreateTeamRequest {
    @EmptyNotNull(message = TEAM_NAME_NOT_NULL)
    private String teamName;
    @EmptyNotNull(message = MANGER_EMAIL_NOT_NULL)
    @Email
    private String managerEmail;
    @EmptyNotNull(message = CREATOR_EMAIL_NOT_NULL)
    @Email
    private String creatorEmail;
    @EmptyNotNull(message = STATUS_NOT_NULL)
    private String status;
    @EmptyNotNull(message = PROJECT_NAME_NOT_NULL)
    private String projectName;
    @NotEmpty(message = EMAIL_LIST_NOT_NULL)
    private List<String> emailList;

    @Override
    public String toString() {
        return  new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
