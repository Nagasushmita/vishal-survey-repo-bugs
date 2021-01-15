package io.springboot.survey.annotation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

import static io.springboot.survey.utils.Constants.ValidationConstant.ALLOWED_FILE_TYPE;


public class ValidFileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    @Override
    public void initialize(ValidFile constraintAnnotation) {
    // do nothing
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        boolean result=true;

        String contentType = value.getContentType();
        if (!isSupportedContentType(Objects.requireNonNull(contentType))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    ALLOWED_FILE_TYPE)
                    .addConstraintViolation();
            result = false;
        }

        return result;
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpeg")
                || contentType.equals("application/pdf");
    }

}
