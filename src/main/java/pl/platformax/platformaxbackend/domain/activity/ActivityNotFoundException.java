package pl.platformax.platformaxbackend.domain.activity;

public class ActivityNotFoundException extends RuntimeException {

    public ActivityNotFoundException(Long activityId) {
        super("Activity not found: " + activityId);
    }
}
