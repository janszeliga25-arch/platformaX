package pl.platformax.platformaxbackend.domain.activity;

public class ActivityCannotBePublishedException extends RuntimeException {

    public ActivityCannotBePublishedException(Long activityId, ActivityStatus currentStatus) {
        super("Activity cannot be published: " + activityId + " (status=" + currentStatus + ")");
    }
}
