package io.springboot.survey.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PendingTakenResponse {

    private String userName;
    private String email;
    private long timestamp;
    private String teamName;
    private String orgId;
    private long expirationDate;
}
