package pl.platformax.platformaxbackend.domain.activity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ActivityTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 6, 1, 10, 0);

    @Test
    void newActivity_startsAsDraft() {
        Activity activity = new Activity(1L, "Title", "Description", ActivityType.TRAINING, START);

        assertEquals(ActivityStatus.DRAFT, activity.getStatus());
    }

    @Test
    void newActivity_createdAtIsSet() {
        Activity activity = new Activity(1L, "Title", "Description", ActivityType.TRAINING, START);

        assertNotNull(activity.getCreatedAt());
    }

    @Test
    void newActivity_updatedAtEqualsCreatedAt() {
        Activity activity = new Activity(1L, "Title", "Description", ActivityType.TRAINING, START);

        assertEquals(activity.getCreatedAt(), activity.getUpdatedAt());
    }

    @Test
    void newActivity_onlineDefaultsFalse() {
        Activity activity = new Activity(1L, "Title", "Description", ActivityType.TRAINING, START);

        assertFalse(activity.isOnline());
    }

    @Test
    void setTitle_updatesUpdatedAt() throws InterruptedException {
        Activity activity = new Activity(1L, "Title", "Description", ActivityType.TRAINING, START);
        LocalDateTime before = activity.getUpdatedAt();
        Thread.sleep(2);

        activity.setTitle("New Title");

        assertTrue(activity.getUpdatedAt().isAfter(before));
    }

    @Test
    void setStatus_updatesUpdatedAt() throws InterruptedException {
        Activity activity = new Activity(1L, "Title", "Description", ActivityType.TRAINING, START);
        LocalDateTime before = activity.getUpdatedAt();
        Thread.sleep(2);

        activity.setStatus(ActivityStatus.PUBLISHED);

        assertEquals(ActivityStatus.PUBLISHED, activity.getStatus());
        assertTrue(activity.getUpdatedAt().isAfter(before));
    }
}
