package pl.platformax.platformaxbackend.api.org.activity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.platformax.platformaxbackend.api.org.activity.validation.ValidActivityDateRange;
import pl.platformax.platformaxbackend.domain.activity.ActivityType;

import java.time.LocalDateTime;

@ValidActivityDateRange
public record CreateActivityRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull ActivityType activityType,
        @NotNull LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
}
