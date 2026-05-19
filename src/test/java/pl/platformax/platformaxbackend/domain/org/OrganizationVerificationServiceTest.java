package pl.platformax.platformaxbackend.domain.org;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(OrganizationVerificationService.class)
class OrganizationVerificationServiceTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationVerificationService verificationService;

    @Test
    void newlyRegisteredOrganization_startsAsPending() {
        Organization org = organizationRepository.saveAndFlush(new Organization("TestOrg", "1111111111"));

        assertEquals(OrganizationStatus.PENDING, org.getStatus());
        assertNull(org.getVerifiedAt());
    }

    @Test
    void verifyOrganization_changesPendingToVerified() {
        Organization org = organizationRepository.saveAndFlush(new Organization("VerifyOrg", "2222222222"));

        verificationService.verifyOrganization(org.getId());

        Organization updated = organizationRepository.findById(org.getId()).orElseThrow();
        assertEquals(OrganizationStatus.VERIFIED, updated.getStatus());
    }

    @Test
    void verifyOrganization_setsVerifiedAt() {
        Organization org = organizationRepository.saveAndFlush(new Organization("VerifiedAtOrg", "3333333333"));

        verificationService.verifyOrganization(org.getId());

        Organization updated = organizationRepository.findById(org.getId()).orElseThrow();
        assertNotNull(updated.getVerifiedAt());
    }

    @Test
    void verifyOrganization_alreadyVerified_isIdempotent_doesNotResetVerifiedAt() {
        Organization org = organizationRepository.saveAndFlush(new Organization("IdempotentOrg", "4444444444"));
        verificationService.verifyOrganization(org.getId());
        Organization afterFirst = organizationRepository.findById(org.getId()).orElseThrow();
        var firstVerifiedAt = afterFirst.getVerifiedAt();

        verificationService.verifyOrganization(org.getId());

        Organization afterSecond = organizationRepository.findById(org.getId()).orElseThrow();
        assertEquals(OrganizationStatus.VERIFIED, afterSecond.getStatus());
        assertEquals(firstVerifiedAt, afterSecond.getVerifiedAt());
    }

    @Test
    void verifyOrganization_unknownId_throwsDomainException() {
        assertThrows(OrganizationNotFoundException.class, () ->
                verificationService.verifyOrganization(999999L));
    }
}
