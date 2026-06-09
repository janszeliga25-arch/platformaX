package pl.platformax.platformaxbackend.api.org.activity.dto;

import pl.platformax.platformaxbackend.domain.activity.Activity;
import pl.platformax.platformaxbackend.domain.activity.ActivityStatus;
import pl.platformax.platformaxbackend.domain.activity.ActivityType;

import java.time.LocalDateTime;

public record CreateActivityResponse(
        Long id,
        Long organizationId,
        String title,
        String description,
        ActivityType activityType,
        ActivityStatus status,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        boolean online,
        LocalDateTime createdAt
) {
    public static CreateActivityResponse from(Activity activity) {
        return new CreateActivityResponse(
                activity.getId(),
                activity.getOrganizationId(),
                activity.getTitle(),
                activity.getDescription(),
                activity.getActivityType(),
                activity.getStatus(),
                activity.getStartDateTime(),
                activity.getEndDateTime(),
                activity.isOnline(),
                activity.getCreatedAt()
        );
    }
}
