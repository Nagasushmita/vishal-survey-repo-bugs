package io.springboot.survey.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TemplateInformation {
    private String templateName;
    private String templateDesc;
    private long createdOn;
    private Integer questionCount;
    private Integer surveyCount;
    private List<HashMap<String, String>> surveyDetails;
}
