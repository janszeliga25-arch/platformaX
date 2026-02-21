package pl.platformax.platformaxbackend.domain.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import pl.platformax.platformaxbackend.domain.org.Organization;
import pl.platformax.platformaxbackend.domain.org.OrganizationRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class OrgAccountRepositoryTest {

    @Autowired
    private OrgAccountRepository orgAccountRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    void shouldEnforceUniqueEmailConstraint() {
        Organization org = organizationRepository.saveAndFlush(new Organization("Test Org", "0000999999"));

        orgAccountRepository.saveAndFlush(new OrgAccount(org, "org@example.com", "{bcrypt}hash", OrgRole.ORG_ADMIN));

        assertThrows(DataIntegrityViolationException.class, () ->
                orgAccountRepository.saveAndFlush(new OrgAccount(org, "org@example.com", "{bcrypt}hash2", OrgRole.ORG_STAFF))
        );
    }
}