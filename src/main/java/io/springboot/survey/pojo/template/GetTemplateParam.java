package io.springboot.survey.pojo.template;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created By: Vishal Jha
 * Date: 20/10/20
 * Time: 4:53 PM
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetTemplateParam {
    String email;
    Integer page;
    Integer pageSize;
    String sortBy;
}
