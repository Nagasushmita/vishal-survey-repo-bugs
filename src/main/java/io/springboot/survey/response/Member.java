package io.springboot.survey.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Member {

   private String teamName;
    private String name;
    private String email;
    private String orgId;
    private String creatorName;
    private String creatorEmail;
    private String managerName;
    private String managerEmail;
    private String gender;
    private String designation;
}
