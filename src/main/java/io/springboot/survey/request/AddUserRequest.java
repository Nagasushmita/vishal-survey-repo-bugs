package io.springboot.survey.request;
import com.google.gson.GsonBuilder;
import io.springboot.survey.response.EmployeeDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.EMPLOYEE_LIST_NOT_NULL;

@Getter
@Setter
@NoArgsConstructor
public class AddUserRequest {

    @NotEmpty(message = EMPLOYEE_LIST_NOT_NULL)
    private List<EmployeeDetails> employeeDetailsList;
    private String roleName;

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
