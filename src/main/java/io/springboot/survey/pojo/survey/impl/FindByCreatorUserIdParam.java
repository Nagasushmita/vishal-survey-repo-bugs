package io.springboot.survey.pojo.survey.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created By: Vishal Jha
 * Date: 20/10/20
 * Time: 3:16 PM
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class FindByCreatorUserIdParam {
    Integer userId;
    Integer page;
    Integer pageSize;
    String sortBy;
    boolean archived;
}
