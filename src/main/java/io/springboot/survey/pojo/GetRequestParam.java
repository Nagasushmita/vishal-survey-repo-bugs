package io.springboot.survey.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created By: Vishal Jha
 * Date: 20/10/20
 * Time: 1:50 PM
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetRequestParam {
    String email;
    int page;
    Integer pageSize;
    String sortBy;

}
