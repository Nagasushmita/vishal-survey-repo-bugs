package io.springboot.survey.response;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class SurveyResponse {

    private String surveyName;
    private String creatorEmail;
    private String templateName;
    private String questionText;
    private String teamName;
    private List<String> designation;
    private String filter;
    private int number;
    private Set<String> emails;

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
