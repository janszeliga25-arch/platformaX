package pl.platformax.platformaxbackend.api.org.activity.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ActivityDateRangeValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidActivityDateRange {

    String message() default "endDateTime must be strictly after startDateTime";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
