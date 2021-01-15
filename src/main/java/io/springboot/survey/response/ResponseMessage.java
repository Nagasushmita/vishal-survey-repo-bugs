package io.springboot.survey.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseMessage {
    private int statusCode;
    private String message;

    public ResponseMessage(int statusCode, String error) {
        this.statusCode = statusCode;
        this.message = error;
    }
}
