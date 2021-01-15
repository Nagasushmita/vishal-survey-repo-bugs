package io.springboot.survey.request;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.MAIL_LIST_NOT_NULL;

@Getter
@Setter
@NoArgsConstructor
public class ModifyUserRequest {
    @NotEmpty(message = MAIL_LIST_NOT_NULL)
    private List<String> listOfMails;

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
