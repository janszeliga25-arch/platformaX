package pl.platformax.platformaxbackend.domain.activity;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.platformax.platformaxbackend.domain.org.OrganizationNotFoundException;
import pl.platformax.platformaxbackend.domain.org.OrganizationRepository;
import pl.platformax.platformaxbackend.domain.org.OrganizationStatus;

import java.time.LocalDateTime;

@Service
public class ActivityCreationService {

    private final ActivityRepository activityRepository;
    private final OrganizationRepository organizationRepository;

    public ActivityCreationService(ActivityRepository activityRepository,
                                   OrganizationRepository organizationRepository) {
        this.activityRepository = activityRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public Activity createActivity(Long organizationId, String title, String description,
                                   ActivityType activityType, LocalDateTime startDateTime,
                                   LocalDateTime endDateTime) {
        if (organizationId == null) {
            throw new IllegalArgumentException("organizationId must not be null");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
        if (activityType == null) {
            throw new IllegalArgumentException("activityType must not be null");
        }
        if (startDateTime == null) {
            throw new IllegalArgumentException("startDateTime must not be null");
        }
        if (endDateTime != null && !endDateTime.isAfter(startDateTime)) {
            throw new IllegalArgumentException("endDateTime must be after startDateTime");
        }

        var organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));

        if (organization.getStatus() != OrganizationStatus.VERIFIED) {
            throw new OrganizationNotVerifiedException(organizationId);
        }

        Activity activity = new Activity(organizationId, title, description, activityType, startDateTime);
        if (endDateTime != null) {
            activity.setEndDateTime(endDateTime);
        }
        return activityRepository.save(activity);
    }
}
