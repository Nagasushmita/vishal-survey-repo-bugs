package io.springboot.survey.request;

import com.google.gson.GsonBuilder;
import io.springboot.survey.annotation.EmptyNotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static io.springboot.survey.utils.Constants.NullEmptyConstant.OTP_NOT_NULL;

@Getter
@Setter
@NoArgsConstructor
public class OtpRequest {
    @EmptyNotNull(message = OTP_NOT_NULL)
    private String otpValue;

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}

