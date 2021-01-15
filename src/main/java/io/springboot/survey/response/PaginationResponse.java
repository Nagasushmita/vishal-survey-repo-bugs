package io.springboot.survey.response;
import com.fasterxml.jackson.annotation.JsonFilter;
import io.springboot.survey.models.TeamModel;
import io.springboot.survey.models.TemplateModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

import static io.springboot.survey.utils.Constants.FilterConstants.PAGINATION_FILTER;


@Getter
@Setter
@NoArgsConstructor
@JsonFilter(PAGINATION_FILTER)
public class PaginationResponse {

    private List<TemplateResponse> templateResponsesList;
    private List<TemplateModel> templateModelList;
    private List<HashMap<String, String>> hashMapList;
    private List<TeamModel> teamModelList;
    private Integer pageRequired;
}
