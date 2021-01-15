package io.springboot.survey.request;

import com.google.gson.GsonBuilder;
import io.springboot.survey.annotation.Email;
import io.springboot.survey.annotation.EmptyNotNull;
import io.springboot.survey.response.SurveyData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.*;

@Getter
@Setter
@NoArgsConstructor
public class CreateTemplateRequest {
    @EmptyNotNull(message = TEMPLATE_NAME_NOT_NULL)
    private String templateName;
    private String templateDesc;
    @EmptyNotNull(message = EMAIL_NOT_NULL)
    @Email
    private String email;
    @NotEmpty(message = SURVEY_DATA_LIST_NOT_NULL)
    private List<SurveyData> surveyDataList;

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
