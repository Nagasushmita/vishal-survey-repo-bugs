package io.springboot.survey.pojo.user;

import io.springboot.survey.response.UserFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created By: Vishal Jha
 * Date: 20/10/20
 * Time: 5:20 PM
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllParam {
    String email;
    UserFilter userFilter;
    Integer page;
    int pageSize;
    String sortBy;
}
