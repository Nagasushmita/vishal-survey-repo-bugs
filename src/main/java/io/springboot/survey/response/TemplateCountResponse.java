package io.springboot.survey.response;

import io.springboot.survey.models.TemplateModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TemplateCountResponse {
    private  TemplateModel templateModel;
    private int useCount;
    private boolean isUsed;
}
