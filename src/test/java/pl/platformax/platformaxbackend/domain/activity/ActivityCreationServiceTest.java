package pl.platformax.platformaxbackend.domain.activity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import pl.platformax.platformaxbackend.domain.org.Organization;
import pl.platformax.platformaxbackend.domain.org.OrganizationNotFoundException;
import pl.platformax.platformaxbackend.domain.org.OrganizationRepository;
import pl.platformax.platformaxbackend.domain.org.OrganizationVerificationService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({ActivityCreationService.class, OrganizationVerificationService.class})
class ActivityCreationServiceTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 6, 1, 10, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 6, 1, 12, 0);

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationVerificationService verificationService;

    @Autowired
    private ActivityCreationService activityCreationService;

    @Autowired
    private ActivityRepository activityRepository;

    private Organization saveVerifiedOrg(String name, String krs) {
        Organization org = organizationRepository.saveAndFlush(new Organization(name, krs));
        verificationService.verifyOrganization(org.getId());
        return organizationRepository.findById(org.getId()).orElseThrow();
    }

    @Test
    void createActivity_validInput_returnsDraft() {
        Organization org = saveVerifiedOrg("VerifiedOrg", "1111111111");

        Activity activity = activityCreationService.createActivity(
                org.getId(), "Training Session", "A great training", ActivityType.TRAINING, START, null);

        assertNotNull(activity.getId());
        assertEquals(ActivityStatus.DRAFT, activity.getStatus());
        assertEquals(org.getId(), activity.getOrganizationId());
    }

    @Test
    void createActivity_withEndDateTime_persisted() {
        Organization org = saveVerifiedOrg("VerifiedOrg2", "2222222222");

        Activity activity = activityCreationService.createActivity(
                org.getId(), "Workshop", "Workshop desc", ActivityType.WORKSHOP, START, END);

        assertEquals(END, activity.getEndDateTime());
    }

    @Test
    void createActivity_pendingOrganization_throwsNotVerifiedException() {
        Organization org = organizationRepository.saveAndFlush(new Organization("PendingOrg", "3333333333"));

        assertThrows(OrganizationNotVerifiedException.class, () ->
                activityCreationService.createActivity(
                        org.getId(), "Title", "Desc", ActivityType.TRAINING, START, null));
    }

    @Test
    void createActivity_unknownOrganization_throwsNotFoundException() {
        assertThrows(OrganizationNotFoundException.class, () ->
                activityCreationService.createActivity(
                        999999L, "Title", "Desc", ActivityType.TRAINING, START, null));
    }

    @Test
    void createActivity_nullOrganizationId_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                activityCreationService.createActivity(
                        null, "Title", "Desc", ActivityType.TRAINING, START, null));
    }

    @Test
    void createActivity_blankTitle_throwsIllegalArgument() {
        Organization org = saveVerifiedOrg("VerifiedOrg3", "4444444444");

        assertThrows(IllegalArgumentException.class, () ->
                activityCreationService.createActivity(
                        org.getId(), "   ", "Desc", ActivityType.TRAINING, START, null));
    }

    @Test
    void createActivity_blankDescription_throwsIllegalArgument() {
        Organization org = saveVerifiedOrg("VerifiedOrg4", "5555555555");

        assertThrows(IllegalArgumentException.class, () ->
                activityCreationService.createActivity(
                        org.getId(), "Title", "", ActivityType.TRAINING, START, null));
    }

    @Test
    void createActivity_nullActivityType_throwsIllegalArgument() {
        Organization org = saveVerifiedOrg("VerifiedOrg5", "6666666666");

        assertThrows(IllegalArgumentException.class, () ->
                activityCreationService.createActivity(
                        org.getId(), "Title", "Desc", null, START, null));
    }

    @Test
    void createActivity_nullStartDateTime_throwsIllegalArgument() {
        Organization org = saveVerifiedOrg("VerifiedOrg6", "7777777777");

        assertThrows(IllegalArgumentException.class, () ->
                activityCreationService.createActivity(
                        org.getId(), "Title", "Desc", ActivityType.TRAINING, null, null));
    }

    @Test
    void createActivity_endBeforeStart_throwsIllegalArgument() {
        Organization org = saveVerifiedOrg("VerifiedOrg7", "8888888888");
        LocalDateTime endBeforeStart = START.minusHours(1);

        assertThrows(IllegalArgumentException.class, () ->
                activityCreationService.createActivity(
                        org.getId(), "Title", "Desc", ActivityType.TRAINING, START, endBeforeStart));
    }

    @Test
    void createActivity_endEqualToStart_throwsIllegalArgument() {
        Organization org = saveVerifiedOrg("VerifiedOrg8", "9999999999");

        assertThrows(IllegalArgumentException.class, () ->
                activityCreationService.createActivity(
                        org.getId(), "Title", "Desc", ActivityType.TRAINING, START, START));
    }
}
