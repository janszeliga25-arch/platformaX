package pl.platformax.platformaxbackend.api.org.activity.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.platformax.platformaxbackend.api.org.activity.dto.CreateActivityRequest;

public class ActivityDateRangeValidator
        implements ConstraintValidator<ValidActivityDateRange, CreateActivityRequest> {

    @Override
    public boolean isValid(CreateActivityRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }
        if (request.endDateTime() == null || request.startDateTime() == null) {
            return true;
        }
        return request.endDateTime().isAfter(request.startDateTime());
    }
}
