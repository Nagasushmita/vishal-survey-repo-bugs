package io.springboot.survey.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticationResponse {

    private String jwt ;
    private String status;
    private String response;
    private String name;
    private String role;
}
