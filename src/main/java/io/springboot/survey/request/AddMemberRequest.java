package io.springboot.survey.request;

import com.google.gson.GsonBuilder;
import io.springboot.survey.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.*;
import static io.springboot.survey.utils.Constants.ValidationConstant.INVALID_CREATOR_EMAIL;

@Getter
@Setter
@NoArgsConstructor
public class AddMemberRequest {
    @EmptyNotNull(message = TEAM_NAME_NOT_NULL)
    private String teamName;
    @EmptyNotNull(message = CREATOR_EMAIL_NOT_NULL)
    @Email(message = INVALID_CREATOR_EMAIL)
    private String creatorEmail;
    @NotEmpty(message =MEMBER_LIST_NOT_NULL)
    private List<String> memberList;

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
