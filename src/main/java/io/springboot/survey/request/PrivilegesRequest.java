package io.springboot.survey.request;

import com.google.gson.GsonBuilder;
import io.springboot.survey.annotation.EmptyNotNull;
import io.springboot.survey.response.PrivilegeSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.PRIVILEGES_LIST_EMPTY;
import static io.springboot.survey.utils.Constants.NullEmptyConstant.ROLE_NAME_NOT_NULL;

@Getter
@Setter
@NoArgsConstructor
public class PrivilegesRequest {
    @EmptyNotNull(message = ROLE_NAME_NOT_NULL)
    private String roleName;
    @NotEmpty(message = PRIVILEGES_LIST_EMPTY)
    private List<PrivilegeSet> privileges;

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}