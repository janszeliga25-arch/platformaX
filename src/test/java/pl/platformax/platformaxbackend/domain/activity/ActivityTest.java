package pl.platformax.platformaxbackend.domain.activity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ActivityTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 6, 1, 10, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 6, 1, 12, 0);

    @Test
    void newActivity_startsAsDraft() {
        Activity activity = new Activity(1L, "Title", "Description", ActivityType.TRAINING, START, null);

        assertEquals(ActivityStatus.DRAFT, activity.getStatus());
    }

    @Test
    void newActivity_createdAtIsSet() {
        Activity activity = new Activity(1L, "Title", "Description", ActivityType.TRAINING, START, null);

        assertNotNull(activity.getCreatedAt());
    }

    @Test
    void newActivity_updatedAtEqualsCreatedAt() {
        Activity activity = new Activity(1L, "Title", "Description", ActivityType.TRAINING, START, null);

        assertEquals(activity.getCreatedAt(), activity.getUpdatedAt());
    }

    @Test
    void newActivity_onlineDefaultsFalse() {
        Activity activity = new Activity(1L, "Title", "Description", ActivityType.TRAINING, START, null);

        assertFalse(activity.isOnline());
    }

    @Test
    void newActivity_withEndDateTime_stored() {
        Activity activity = new Activity(1L, "Title", "Description", ActivityType.TRAINING, START, END);

        assertEquals(END, activity.getEndDateTime());
    }

    @Test
    void constructor_nullOrganizationId_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new Activity(null, "Title", "Description", ActivityType.TRAINING, START, null));
    }

    @Test
    void constructor_blankTitle_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new Activity(1L, "  ", "Description", ActivityType.TRAINING, START, null));
    }

    @Test
    void constructor_blankDescription_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new Activity(1L, "Title", "", ActivityType.TRAINING, START, null));
    }

    @Test
    void constructor_nullActivityType_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new Activity(1L, "Title", "Description", null, START, null));
    }

    @Test
    void constructor_nullStartDateTime_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new Activity(1L, "Title", "Description", ActivityType.TRAINING, null, null));
    }

    @Test
    void constructor_endDateTimeEqualToStart_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new Activity(1L, "Title", "Description", ActivityType.TRAINING, START, START));
    }

    @Test
    void constructor_endDateTimeBeforeStart_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new Activity(1L, "Title", "Description", ActivityType.TRAINING, START, START.minusMinutes(1)));
    }

    @Test
    void setOnline_updatesUpdatedAt() throws InterruptedException {
        Activity activity = new Activity(1L, "Title", "Description", ActivityType.TRAINING, START, null);
        LocalDateTime before = activity.getUpdatedAt();
        Thread.sleep(2);

        activity.setOnline(true);

        assertTrue(activity.isOnline());
        assertTrue(activity.getUpdatedAt().isAfter(before));
    }
}
