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
public class CreateSurveyRequest {

    @EmptyNotNull(message = SURVEY_NOT_NULL)
    private String surveyName;
    @EmptyNotNull(message = LINK_NOT_NULL)
    private String link;
    @NotEmpty(message = SURVEY_DATA_LIST_NOT_NULL)
    private List<SurveyData> surveyDataList;
    private String surveyDesc;
    private String templateName;
    @EmptyNotNull(message = EMAIL_NOT_NULL)
    @Email
    private String creatorEmail;

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
