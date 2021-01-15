package io.springboot.survey.request;

import com.google.gson.GsonBuilder;
import io.springboot.survey.annotation.Email;
import io.springboot.survey.annotation.EmptyNotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.EMAIL_NOT_NULL;


@Getter
@Setter
@NoArgsConstructor
public class UpdateUsersRequest {
    @EmptyNotNull(message = EMAIL_NOT_NULL)
    @Email
    private String email;
    private String designation;
    private String roleName;

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
