package pl.platformax.platformaxbackend.domain.org;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(OrganizationVerificationService.class)
class OrganizationVerificationServiceTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationVerificationService verificationService;

    @Test
    void newlyRegisteredOrganization_startsAsPending() {
        Organization org = organizationRepository.save(new Organization("Test Org", "1111111111"));
        assertThat(org.getStatus()).isEqualTo(OrganizationStatus.PENDING);
    }

    @Test
    void verifyOrganization_changesStatus_fromPendingToVerified() {
        Organization org = organizationRepository.save(new Organization("Test Org", "2222222222"));

        verificationService.verifyOrganization(org.getId());

        Organization reloaded = organizationRepository.findById(org.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(OrganizationStatus.VERIFIED);
    }

    @Test
    void verifyOrganization_setsVerifiedAt() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        Organization org = organizationRepository.save(new Organization("Test Org", "3333333333"));

        verificationService.verifyOrganization(org.getId());

        Organization reloaded = organizationRepository.findById(org.getId()).orElseThrow();
        assertThat(reloaded.getVerifiedAt()).isNotNull();
        assertThat(reloaded.getVerifiedAt()).isAfter(before);
    }

    @Test
    void verifyOrganization_isIdempotent_doesNotResetVerifiedAt() {
        Organization org = organizationRepository.save(new Organization("Test Org", "4444444444"));
        verificationService.verifyOrganization(org.getId());

        Organization afterFirst = organizationRepository.findById(org.getId()).orElseThrow();
        LocalDateTime firstVerifiedAt = afterFirst.getVerifiedAt();

        verificationService.verifyOrganization(org.getId());

        Organization afterSecond = organizationRepository.findById(org.getId()).orElseThrow();
        assertThat(afterSecond.getStatus()).isEqualTo(OrganizationStatus.VERIFIED);
        assertThat(afterSecond.getVerifiedAt()).isEqualTo(firstVerifiedAt);
    }

    @Test
    void verifyOrganization_unknownId_throwsDomainException() {
        assertThatThrownBy(() -> verificationService.verifyOrganization(999L))
                .isInstanceOf(OrganizationNotFoundException.class)
                .hasMessageContaining("999");
    }
}
