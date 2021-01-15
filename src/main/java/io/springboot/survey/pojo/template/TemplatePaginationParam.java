package io.springboot.survey.pojo.template;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created By: Vishal Jha
 * Date: 20/10/20
 * Time: 5:05 PM
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TemplatePaginationParam {
     List<Integer> templateId;
    int page;
    Integer pageSize;
    String sortBy;

}
