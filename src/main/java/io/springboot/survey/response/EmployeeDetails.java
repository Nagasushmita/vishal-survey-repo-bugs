package io.springboot.survey.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeDetails {

    private String empId;
    private String name;
    private String email;
    private String role;
    private String gender;
    private String designation;
}
